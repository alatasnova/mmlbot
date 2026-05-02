package org.am.mmlbot.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.am.mmlbot.MuscleMemoryAction;
import org.am.mmlbot.RankedAction;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import org.am.mmlbot.Situation;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import org.am.mmlbot.client.bot.MmlBot;
import org.am.mmlbot.client.bot.MmlCommands;
import org.am.mmlbot.client.bot.PvpBot;

public class MmlbotClient implements ClientModInitializer {
    private static final MmlBot bot = new PvpBot();

    private List<RankedAction> botMemory = new ArrayList<>();
    private Entity currentEnemy;

    private RankedAction currentRankedAction;
    private int currentActionTick;

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register(this::onPlayerJoin);
        ClientTickEvents.END_CLIENT_TICK.register(this::onEndTick);
        AttackEntityCallback.EVENT.register(this::onPlayerAttack);
    }

    private ActionResult onPlayerAttack(PlayerEntity playerEntity, World world, Hand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {

        playerEntity.sendMessage(Text.literal(playerEntity.getName() + " ударила " + entity.getName()), false);

        if (MinecraftClient.getInstance().player != null){
            if (playerEntity.getName().equals(MinecraftClient.getInstance().player.getName())){
                playerEntity.sendMessage(Text.literal("Теперь " + entity.getName() + " наш враг"), false);
                currentEnemy = entity;
                performRankedAction(botMemory.getLast());
            }
        }

        return ActionResult.PASS;
        ClientTickEvents.END_CLIENT_TICK.register(bot::tick);

        ClientCommandRegistrationCallback.EVENT
                .register((dispatcher, registryAccess) ->
                        MmlCommands.register(dispatcher)
                );
    }

    public void onPlayerJoin(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client){
        ClientPlayerEntity player = client.player;
        if (player == null)
            return;

        player.sendMessage(Text.literal("MMLBot Успешно запушен."), false);

        player.sendMessage(Text.literal("Создание тестового RankedAction..."), false);

        List<MuscleMemoryAction> muscleMemoryActions = new ArrayList<>();

        muscleMemoryActions.add(new MuscleMemoryAction(
                false, false, false, 0, 0, 180, 0f
        ));
        muscleMemoryActions.add(new MuscleMemoryAction(
                false, false, false, 0, 0, 0, 45
        ));

//        muscleMemoryActions.add(new MuscleMemoryAction(
//                false, false, false, 0, -1, -0.5f, -1f
//        ));
//        muscleMemoryActions.add(new MuscleMemoryAction(
//                false, false, false, 1, -1, -4.0f, -0f
//        ));
//        muscleMemoryActions.add(new MuscleMemoryAction(
//                true, false, false, 1, 1, 6.28f, -1f
//        ));
//        muscleMemoryActions.add(new MuscleMemoryAction(
//                true, true, false, 1, 0, 0.0f, 3f
//        ));
//        muscleMemoryActions.add(new MuscleMemoryAction(
//                false, false, false, 0, 0, 1.5f, 2f
//        ));

        botMemory.add(new RankedAction(
                125,
                new Situation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, true),
                muscleMemoryActions
        ));
    }

    public void performRankedAction(RankedAction rankedAction){
        currentRankedAction = rankedAction;
        currentActionTick = 0;
    }

    public void onEndTick(MinecraftClient client){
        if (client.player == null || currentRankedAction == null)
            return;

        // Try to perform current ranked action
        currentRankedAction.performByTick(currentActionTick);
        currentActionTick++;
    }

    public static MmlBot getBot() {
        return bot;
    }
}

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
    private static final PvpBot bot = new PvpBot();

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register(this::onPlayerJoin);
        ClientTickEvents.END_CLIENT_TICK.register(bot::tick);
        AttackEntityCallback.EVENT.register(this::onPlayerAttack);
        ClientCommandRegistrationCallback.EVENT
                .register((dispatcher, registryAccess) ->
                        MmlCommands.register(dispatcher)
                );
    }

    private ActionResult onPlayerAttack(PlayerEntity playerEntity, World world, Hand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {

        playerEntity.sendMessage(Text.literal(playerEntity.getName() + " ударила " + entity.getName()), false);

        if (MinecraftClient.getInstance().player != null){
            if (playerEntity.getName().equals(MinecraftClient.getInstance().player.getName())){
                playerEntity.sendMessage(Text.literal("Теперь " + entity.getName() + " наш враг"), false);
                bot.setCurrentEnemy(entity);
                bot.enable(MinecraftClient.getInstance());
            }
        }

        return ActionResult.PASS;
    }

    public void onPlayerJoin(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client){
        ClientPlayerEntity player = client.player;
        if (player == null)
            return;

        player.sendMessage(Text.literal("MMLBot Успешно запушен."), false);

        player.sendMessage(Text.literal("Создание тестового RankedAction..."), false);

        List<MuscleMemoryAction> muscleMemoryActions = new ArrayList<>();

        muscleMemoryActions.add(new MuscleMemoryAction(
                false, false, false, 1, 0, 180, 0f
        ));
        muscleMemoryActions.add(new MuscleMemoryAction(
                false, false, false, 0, 0, 0, 45
        ));

        bot.addRankedAction(new RankedAction(
                125,
                new Situation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, true),
                muscleMemoryActions
        ));
    }

    public static MmlBot getBot() {
        return bot;
    }
}

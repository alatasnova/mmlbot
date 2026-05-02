package org.am.mmlbot.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.am.mmlbot.client.bot.MmlBot;
import org.am.mmlbot.client.bot.MmlCommands;
import org.am.mmlbot.client.bot.PvpBot;

public class MmlbotClient implements ClientModInitializer {
    private static final MmlBot bot = new PvpBot();

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register(this::onPlayerJoin);

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
    }

    public static MmlBot getBot() {
        return bot;
    }
}

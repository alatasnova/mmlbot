package org.am.mmlbot.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

public class MmlbotClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register(this::onPlayerJoin);
    }

    public void onPlayerJoin(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client){
        ClientPlayerEntity player = client.player;
        if (player == null)
            return;

        player.sendMessage(Text.literal("MMLBot Успешно запушен."), false);
    }
}

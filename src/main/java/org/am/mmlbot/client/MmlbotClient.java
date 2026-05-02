package org.am.mmlbot.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class MmlbotClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register(this::onPlayerJoin);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                client.options.forwardKey.setPressed(true);
                client.options.jumpKey.setPressed(true);
            }
        });
    }

    public void onPlayerJoin(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client){
        ClientPlayerEntity player = client.player;
        if (player == null)
            return;

        player.sendMessage(Text.literal("MMLBot Успешно запушен."), false);
    }
}

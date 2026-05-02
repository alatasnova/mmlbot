package org.am.mmlbot;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class DebugUtils {
    public static void chat(String message){
        MinecraftClient mc = MinecraftClient.getInstance();
        if(mc.player == null) return;

        mc.player.sendMessage(Text.literal(message), false);
    }
}

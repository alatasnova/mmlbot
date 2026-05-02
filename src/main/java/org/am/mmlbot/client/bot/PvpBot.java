package org.am.mmlbot.client.bot;

import net.minecraft.client.MinecraftClient;

public class PvpBot extends MmlBot{
    @Override
    protected void onEnable(MinecraftClient client) { }

    @Override
    protected void onDisable(MinecraftClient client) {
        client.options.forwardKey.setPressed(false);
        client.options.jumpKey.setPressed(false);
    }

    @Override
    protected void onTick(MinecraftClient client) {
        client.options.forwardKey.setPressed(true);
        client.options.jumpKey.setPressed(true);
    }
}

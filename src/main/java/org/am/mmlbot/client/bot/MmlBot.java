package org.am.mmlbot.client.bot;

import net.minecraft.client.MinecraftClient;

public abstract class MmlBot {
    private boolean enabled = false;

    public final void enable(MinecraftClient client) {
        if (enabled) return;
        enabled = true;
        onEnable(client);
    }

    public final void disable(MinecraftClient client) {
        if (!enabled) return;
        enabled = false;
        onDisable(client);
    }

    public final void tick(MinecraftClient client) {
        if (!enabled) return;
        onTick(client);
    }

    protected abstract void onEnable(MinecraftClient client);
    protected abstract void onDisable(MinecraftClient client);
    protected abstract void onTick(MinecraftClient client);
}

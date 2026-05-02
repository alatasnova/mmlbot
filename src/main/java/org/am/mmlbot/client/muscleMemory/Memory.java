package org.am.mmlbot.client.muscleMemory;

import net.minecraft.client.MinecraftClient;

import java.util.List;

public class Memory {
    public float rating;
    public Situation situation;
    public List<Action> actions;

    private static final int actionDuration = 10;

    public Memory(float rating, Situation situation, List<Action> actions) {
        this.rating = rating;
        this.situation = situation;
        this.actions = actions;
    }

    public int getActionIdByTick(int tick) {
        int elementId = tick / actionDuration;
        if (elementId >= actions.size() || elementId < 0)
            return -1;
        return elementId;
    }

    public void performByTick(int tick) {
        MinecraftClient mc = MinecraftClient.getInstance();
        assert mc.player != null;

        int actionId = getActionIdByTick(tick);
        if (actionId == -1)
            return;

        Action action = actions.get(actionId);

        // Set vertical input
        mc.options.forwardKey.setPressed(action.verticalAxis == 1);
        mc.options.backKey.setPressed(action.verticalAxis == -1);

        // Set hHorizontal input
        mc.options.rightKey.setPressed(action.horizontalAxis == 1);
        mc.options.leftKey.setPressed(action.horizontalAxis == -1);

        // Sprint, jump, attack
        mc.options.sprintKey.setPressed(action.sprint);
        mc.options.attackKey.setPressed(action.attack);
        mc.options.jumpKey.setPressed(action.jump);

        // Rotate
        mc.player.rotate(action.dYaw / actionDuration, true,
                action.dPitch / actionDuration, true);
    }
}

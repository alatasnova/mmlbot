package org.am.mmlbot;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.List;

public class RankedAction {
    public float rating;
    public Situation situation;
    public List<MuscleMemoryAction> muscleMemoryActions;

    private static int actionDurationTicks = 20;

    public RankedAction(float rating, Situation situation, List<MuscleMemoryAction> muscleMemoryActions) {
        this.rating = rating;
        this.situation = situation;
        this.muscleMemoryActions = muscleMemoryActions;
    }

    public int getActionIdByTick(int currentActionTick){
        int elementId = currentActionTick / actionDurationTicks;
        if (elementId >= muscleMemoryActions.size() || elementId < 0)
            return -1;
        return elementId;
    }

    public void performByTick(int currentActionTick) {
        MinecraftClient mc = MinecraftClient.getInstance();
        assert mc.player != null;

        int actionId = getActionIdByTick(currentActionTick);
        if (actionId == -1)
            return;

        MuscleMemoryAction action = muscleMemoryActions.get(actionId);
//        mc.player.sendMessage(Text.literal("Выполняем " + action.toString()), false);
        // performing
        switch (action.moveForwardBackward) {
            case -1:
                mc.options.forwardKey.setPressed(false);
                mc.options.backKey.setPressed(true);
                break;
            case 0:
                mc.options.forwardKey.setPressed(false);
                mc.options.backKey.setPressed(false);
                break;
            case 1:
                mc.options.forwardKey.setPressed(true);
                mc.options.backKey.setPressed(false);
                break;
        }
        switch (action.moveLeftRight){
            case -1:
                mc.options.rightKey.setPressed(false);
                mc.options.leftKey.setPressed(true);
                break;
            case 0:
                mc.options.rightKey.setPressed(false);
                mc.options.leftKey.setPressed(false);
                break;
            case 1:
                mc.options.rightKey.setPressed(true);
                mc.options.leftKey.setPressed(false);
                break;
        }
        mc.options.sprintKey.setPressed(action.sprint);
        mc.options.attackKey.setPressed(action.attack);
        mc.options.jumpKey.setPressed(action.jump);

        mc.player.rotate(action.rotateYaw / (float) actionDurationTicks, true, action.rotatePitch / (float) actionDurationTicks, true);
    }
}

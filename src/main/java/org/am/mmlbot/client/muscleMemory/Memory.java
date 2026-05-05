package org.am.mmlbot.client.muscleMemory;

import net.minecraft.client.MinecraftClient;
import org.am.mmlbot.client.mixin.MinecraftClientAccessor;

import java.util.ArrayList;
import java.util.List;

public class Memory {
    public float rating;
    public Situation situation;

    public List<Action> actions;

    private static final int actionDuration = 5;


    public Memory(float rating, Situation situation, List<Action> actions) {
        this.rating = rating;
        this.situation = situation;
        this.actions = actions;
    }

    public static Memory generateRandom(int rating, Situation situation, int maxActionsLength) {
        List<Action> newActions = new ArrayList<>();
        for(int i = 0; i < maxActionsLength; i++) { // idea: vary length? - mirka: yup <3
            newActions.add(Action.generateRandom());
        }
        return new Memory(rating, situation.getWithRandomizedSituationWeights(), newActions);
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

        boolean shouldAttackThisTick = action.attack && (tick % actionDuration) == 0;
        if (shouldAttackThisTick) {
            ((MinecraftClientAccessor) mc).invokeDoAttack(); //idk why
        }
        mc.options.attackKey.setPressed(action.attack);

        mc.options.jumpKey.setPressed(action.jump);

        // Rotate
        mc.player.rotate(action.dYaw / actionDuration, true,
                action.dPitch / actionDuration, true);
    }

    public float getRating() { return rating; }

    public Memory generateMutated(Situation situation, float delta) {
        List<Action> newActions = new ArrayList<>(this.actions.size());
        for (Action action : this.actions) {
            newActions.add(action.generateMutated(delta));
        }

        return new Memory(this.rating * (1 - delta) * 0.25f, situation.generateMutated(delta), newActions);
    }
}

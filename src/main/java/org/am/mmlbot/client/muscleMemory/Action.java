package org.am.mmlbot.client.muscleMemory;

import net.minecraft.util.math.random.Random;

public class Action {
    boolean jump;
    boolean sprint;
    boolean attack;
    int verticalAxis;
    int horizontalAxis;
    float dYaw;
    float dPitch;

    private final static float maxRotationAnglePerAction = 180f;

    public Action(boolean jump, boolean sprint, boolean attack, int verticalAxis, int horizontalAxis, float dYaw, float dPitch){
        this.jump = jump;
        this.sprint = sprint;
        this.attack = attack;
        this.horizontalAxis = horizontalAxis;
        this.verticalAxis = verticalAxis;
        this.dYaw = dYaw;
        this.dPitch = dPitch;
    }

    public static Action generateRandom(){
        Random random = Random.create();
        return new Action(random.nextBoolean(),
                random.nextBoolean(), random.nextBoolean(),
                random.nextBetween(-1, 1), random.nextBetween(-1, 1),
                (random.nextFloat() - 0.5f) * maxRotationAnglePerAction,
                (random.nextFloat() - 0.5f) * maxRotationAnglePerAction
        );
    }

    @Override
    public String toString() {
        return "sprint: " + sprint + ", jump: " + jump + ", attack: " + attack + ", verticalAxis: " + verticalAxis + ", horizontalAxis: " + horizontalAxis + ", rotate: " + dYaw;
    }
}
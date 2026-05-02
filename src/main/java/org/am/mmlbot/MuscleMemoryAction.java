package org.am.mmlbot;

import net.minecraft.util.math.random.Random;

public class MuscleMemoryAction{
    boolean jump;
    boolean sprint;
    boolean attack;
    int moveForwardBackward;
    int moveLeftRight;
    float rotateYaw;
    float rotatePitch;

    private final static float maxRotationAnglePerAction = 180f;

    public MuscleMemoryAction(boolean jump, boolean sprint, boolean attack, int moveForwardBackward, int moveLeftRight, float rotateYaw, float rotatePitch){
        this.jump = jump;
        this.sprint = sprint;
        this.attack = attack;
        this.moveLeftRight = moveLeftRight;
        this.moveForwardBackward = moveForwardBackward;
        this.rotateYaw = rotateYaw;
        this.rotatePitch = rotatePitch;
    }

    public static MuscleMemoryAction generateRandom(){
        Random random = Random.create();
        return new MuscleMemoryAction(random.nextBoolean(),
                random.nextBoolean(), random.nextBoolean(),
                random.nextBetween(-1, 1), random.nextBetween(-1, 1),
                (random.nextFloat() - 0.5f) * maxRotationAnglePerAction,
                (random.nextFloat() - 0.5f) * maxRotationAnglePerAction
        );
    }

    @Override
    public String toString() {
        return "sprint: " + sprint + ", jump: " + jump + ", attack: " + attack + ", moveForwardBackward: " + moveForwardBackward + ", moveLeftRight: " + moveLeftRight + ", rotate: " + rotateYaw;
    }
}
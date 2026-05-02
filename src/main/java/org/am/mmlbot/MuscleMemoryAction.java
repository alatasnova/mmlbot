package org.am.mmlbot;

public class MuscleMemoryAction{
    boolean jump;
    boolean sprint;
    boolean attack;
    int moveForwardBackward;
    int moveLeftRight;
    float rotateYaw;
    float rotatePitch;

    public MuscleMemoryAction(boolean jump, boolean sprint, boolean attack, int moveForwardBackward, int moveLeftRight, float rotateYaw, float rotatePitch){
        this.jump = jump;
        this.sprint = sprint;
        this.attack = attack;
        this.moveLeftRight = moveLeftRight;
        this.moveForwardBackward = moveForwardBackward;
        this.rotateYaw = rotateYaw;
        this.rotatePitch = rotatePitch;
    }

    @Override
    public String toString() {
        return "sprint: " + sprint + ", jump: " + jump + ", attack: " + attack + ", moveForwardBackward: " + moveForwardBackward + ", moveLeftRight: " + moveLeftRight + ", rotate: " + rotateYaw;
    }
}
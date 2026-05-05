package org.am.mmlbot.client.muscleMemory;

import net.minecraft.util.math.random.Random;

public class Action {
    public boolean jump;
    public boolean sprint;
    public boolean attack;
    public int verticalAxis;
    public int horizontalAxis;
    public float dYaw;
    public float dPitch;

    private final static float maxRotationAnglePerAction = 180f;

    private static final Random random = Random.create();

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

    public Action generateMutated(float delta) {
        int horizontal = this.horizontalAxis;
        int vertical = this.verticalAxis;
        if (random.nextFloat() < delta)
            horizontal = Math.clamp(horizontal + random.nextBetween(-1, 1), -1, 1);
        if (random.nextFloat() < delta)
            vertical = Math.clamp(vertical + random.nextBetween(-1, 1), -1, 1);

        return new Action(
                this.jump ^ (random.nextFloat() < delta),
                this.sprint ^ (random.nextFloat() < delta),
                this.attack ^ (random.nextFloat() < delta),
                vertical, horizontal,
                this.dYaw + random.nextFloat() * delta,
                this.dPitch + random.nextFloat() * delta
        );
    }
}
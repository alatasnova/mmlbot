package org.am.mmlbot.client.muscleMemory;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class Situation {
    public static final int nParams = 7;
    public float distanceToEnemy;
    public float yawToEnemy;
    public float pitchToEnemy;
    public float enemyPitchToBot;
    public float enemyYawToBot;
    public float closingSpeed;
    public boolean isGrounded;

    private static final Random random = Random.create();

    public List<Float> situationWeights;

    public Situation(float distanceToEnemy, float yawToEnemy, float pitchToEnemy, float enemyPitchToBot, float enemyYawToBot, float closingSpeed, boolean isGrounded, List<Float> situationWeights){
        this.distanceToEnemy = distanceToEnemy;
        this.yawToEnemy = yawToEnemy;
        this.pitchToEnemy = pitchToEnemy;
        this.enemyPitchToBot = enemyPitchToBot;
        this.enemyYawToBot = enemyYawToBot;
        this.closingSpeed = closingSpeed;
        this.isGrounded = isGrounded;
        this.situationWeights = situationWeights;
    }

    public static List<Float> getDefaultWeights() {
        List<Float> list = new ArrayList<>(7);
        for (int i = 0; i < nParams; i++){
            list.add(1f);
        }
        return list;
    }

    public void applyConstantWeights(){ // TODO: normal weights
        this.distanceToEnemy = distanceToEnemy * 1.5f;
        this.yawToEnemy = yawToEnemy * 0.2f;
        this.pitchToEnemy = pitchToEnemy * 0.2f;
        this.enemyPitchToBot = enemyPitchToBot * 0.0025f;// 0.0025f;
        this.enemyYawToBot = enemyYawToBot * 0.025f;// 0.0025f;
        this.closingSpeed = closingSpeed * 0.2f;// 0.2f;
//        this.isGrounded = true; // remove
    }

    public Situation getWithRandomizedSituationWeights(){
        List<Float> newSituationWeights = new ArrayList<>(Situation.nParams);
        for (int i = 0; i < Situation.nParams; i ++){
            newSituationWeights.add(random.nextFloat() * 0.5f);
        }
        return new Situation(this.distanceToEnemy, this.yawToEnemy, this.pitchToEnemy, this.enemyPitchToBot, this.enemyYawToBot, this.closingSpeed, this.isGrounded, newSituationWeights);
    }

    public List<Float> generateMutatedSituationWeights(float delta){
        return this.situationWeights.stream().map((x) -> Math.clamp(x + random.nextFloat() * delta, 0.0f, 0.5f)).toList();
    }

    public Situation generateMutated(float delta){
        return new Situation(this.distanceToEnemy, this.yawToEnemy, this.pitchToEnemy, this.enemyPitchToBot, this.enemyYawToBot, this.closingSpeed, this.isGrounded, this.generateMutatedSituationWeights(delta));
    }

    public Situation(Entity bot, Entity enemy, List<Float> situationWeights) {
        this.situationWeights = situationWeights;

        // Позиции для расстояния и скорости сближения (по ногам, как обычно)
        Vec3d botPos = bot.getEntityPos();
        Vec3d enemyPos = enemy.getEntityPos();
        double dx = enemyPos.x - botPos.x;
        double dy = enemyPos.y - botPos.y;
        double dz = enemyPos.z - botPos.z;
        this.distanceToEnemy = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);

        // Скорость сближения: отрицательная производная расстояния
        if (this.distanceToEnemy > 0.0) {
            Vec3d botVel = bot.getVelocity();
            Vec3d enemyVel = enemy.getVelocity();
            double relVelX = botVel.x - enemyVel.x;
            double relVelY = botVel.y - enemyVel.y;
            double relVelZ = botVel.z - enemyVel.z;
            double distRate = (dx * relVelX + dy * relVelY + dz * relVelZ) / this.distanceToEnemy;
            this.closingSpeed = (float) -distRate; // положительно при сближении
        } else {
            this.closingSpeed = 0.0F;
        }

        // Глаза для точного направления взгляда
        Vec3d botEye = bot.getEyePos();
        Vec3d enemyEye = enemy.getEyePos();

        // Направление от бота к врагу
        double dxBot = enemyEye.x - botEye.x;
        double dyBot = enemyEye.y - botEye.y;
        double dzBot = enemyEye.z - botEye.z;
        double hDistBot = Math.sqrt(dxBot * dxBot + dzBot * dzBot);
        float yawNeeded = (float) Math.toDegrees(Math.atan2(-dxBot, dzBot));
        float pitchNeeded = (float) Math.toDegrees(-Math.atan2(dyBot, hDistBot));

        // Разница с текущим поворотом бота
        this.yawToEnemy = wrapAngleDelta(yawNeeded - bot.getYaw());
        this.pitchToEnemy = wrapAngleDelta(pitchNeeded - bot.getPitch());

        // Направление от врага к боту
        double dxEnemy = botEye.x - enemyEye.x;
        double dyEnemy = botEye.y - enemyEye.y;
        double dzEnemy = botEye.z - enemyEye.z;
        double hDistEnemy = Math.sqrt(dxEnemy * dxEnemy + dzEnemy * dzEnemy);
        float yawNeededEnemy = (float) Math.toDegrees(Math.atan2(-dxEnemy, dzEnemy));
        float pitchNeededEnemy = (float) Math.toDegrees(-Math.atan2(dyEnemy, hDistEnemy));

        // Разница с текущим поворотом врага
        this.enemyYawToBot = wrapAngleDelta(yawNeededEnemy - enemy.getYaw());
        this.enemyPitchToBot = wrapAngleDelta(pitchNeededEnemy - enemy.getPitch());

        // Заземлён ли бот
        this.isGrounded = bot.isOnGround();

        applyConstantWeights();
    }

    private static float wrapAngleDelta(float deltaDeg) {
        return ((deltaDeg + 180.0f) % 360.0f + 360.0f) % 360.0f - 180.0f;
    }

    public float calculateWeighedDistance(Situation situation){ // TODO: should we train weights individually for each situation?
        float isGroundedDt = 10 * situationWeights.get(6);
        if (situation.isGrounded == isGrounded)
            isGroundedDt = 0;

        return (float) (Math.pow(situation.distanceToEnemy - distanceToEnemy, 2) * situationWeights.get(0) +
                        Math.pow(situation.yawToEnemy - yawToEnemy, 2) * situationWeights.get(1) +
                        Math.pow(situation.pitchToEnemy - pitchToEnemy, 2) * situationWeights.get(2) +
                        Math.pow(situation.enemyPitchToBot - enemyPitchToBot, 2) * situationWeights.get(3) +
                        Math.pow(situation.enemyYawToBot - enemyYawToBot, 2) * situationWeights.get(4) +
                        Math.pow(situation.closingSpeed - closingSpeed, 2) * situationWeights.get(5) + isGroundedDt);
    }

    @Override
    public String toString() {
        return String.format(
                          """
                          EnemyData {
                          Distance to enemy: %.2f blocks
                          Yaw to enemy:      %.2f°
                          Pitch to enemy:    %.2f°
                          Enemy pitch to bot: %.2f°
                          Enemy yaw to bot:   %.2f°
                          Closing speed:     %.2f blocks/s
                          Is grounded:       %s
                          }
                          """,
                distanceToEnemy,
                yawToEnemy,
                pitchToEnemy,
                enemyPitchToBot,
                enemyYawToBot,
                closingSpeed,
                isGrounded ? "Yes" : "No"
        );
    }
}

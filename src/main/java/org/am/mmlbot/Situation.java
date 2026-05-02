package org.am.mmlbot;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class Situation {
    public float distanceToEnemy;
    public float yawToEnemy;
    public float pitchToEnemy;
    public float enemyPitchToBot;
    public float enemyYawToBot;
    public float closingSpeed;
    public boolean isGrounded;

    public Situation(float distanceToEnemy, float yawToEnemy, float pitchToEnemy, float enemyPitchToBot, float enemyYawToBot, float closingSpeed, boolean isGrounded){
        this.distanceToEnemy = distanceToEnemy;
        this.yawToEnemy = yawToEnemy;
        this.pitchToEnemy = pitchToEnemy;
        this.enemyPitchToBot = enemyPitchToBot;
        this.enemyYawToBot = enemyYawToBot;
        this.closingSpeed = closingSpeed;
        this.isGrounded = isGrounded;
    }

    @Override
    public String toString() {
        return String.format(
                "EnemyData {\n" +
                        "  Distance to enemy: %.2f blocks\n" +
                        "  Yaw to enemy:      %.2f°\n" +
                        "  Pitch to enemy:    %.2f°\n" +
                        "  Enemy pitch to bot: %.2f°\n" +
                        "  Enemy yaw to bot:   %.2f°\n" +
                        "  Closing speed:     %.2f blocks/s\n" +
                        "  Is grounded:       %s\n" +
                        "}",
                distanceToEnemy,
                yawToEnemy,
                pitchToEnemy,
                enemyPitchToBot,
                enemyYawToBot,
                closingSpeed,
                isGrounded ? "Yes" : "No"
        );
    }

    public Situation(Entity enemy, Entity bot) {
        // Получаем позицию игрока и врага
        Vec3d botPos = bot.getEntityPos();
        Vec3d enemyPos = enemy.getEntityPos();

        // Расстояние до врага
        this.distanceToEnemy = (float) botPos.distanceTo(enemyPos);

        // Вычисляем разницу позиций
        double deltaX = enemyPos.x - botPos.x;
        double deltaY = enemyPos.y - botPos.y;
        double deltaZ = enemyPos.z - botPos.z;

        // Yaw и Pitch до врага (в градусах)
        this.yawToEnemy = (float) (Math.atan2(deltaZ, deltaX) * 180 / Math.PI) - 90;

        float horizontalDistance = (float) Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        this.pitchToEnemy = (float) (-Math.atan2(deltaY, horizontalDistance) * 180 / Math.PI);

        // Взгляд врага на бота (нужно вращение врага)
        Vec3d enemyLookVec = enemy.getRotationVecClient();
        Vec3d toPlayerDir = botPos.subtract(enemyPos).normalize();

        this.enemyYawToBot = (float) (Math.atan2(toPlayerDir.z, toPlayerDir.x) * 180 / Math.PI) - 90;
        this.enemyPitchToBot = (float) (-Math.asin(toPlayerDir.y) * 180 / Math.PI);

        // Скорость сближения
        Vec3d playerVelocity = bot.getVelocity();
        Vec3d enemyVelocity = enemy.getVelocity();
        Vec3d relativeVelocity = enemyVelocity.subtract(playerVelocity);
        Vec3d directionToEnemy = enemyPos.subtract(botPos).normalize();
        this.closingSpeed = (float) relativeVelocity.dotProduct(directionToEnemy);

        // На земле ли игрок
        this.isGrounded = bot.isOnGround();
    }

    public float calculateDistance(Situation situation){ // TODO: should we train weights individually for each situation?
        float isGroundedDt = 10; // nah we should really add weights idk
        if (situation.isGrounded && isGrounded)
            isGroundedDt = 0;

        return (float) (Math.pow(situation.distanceToEnemy - distanceToEnemy, 2) +
                        Math.pow(situation.yawToEnemy - yawToEnemy, 2) +
                        Math.pow(situation.pitchToEnemy - pitchToEnemy, 2) +
                        Math.pow(situation.enemyPitchToBot - enemyPitchToBot, 2) +
                        Math.pow(situation.enemyYawToBot - enemyYawToBot, 2) +
                        Math.pow(situation.closingSpeed - closingSpeed, 2) + isGroundedDt);
    }
}

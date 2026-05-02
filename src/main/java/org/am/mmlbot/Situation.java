package org.am.mmlbot;

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
    }
}

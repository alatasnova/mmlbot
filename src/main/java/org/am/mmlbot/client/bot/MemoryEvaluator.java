package org.am.mmlbot.client.bot;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.am.mmlbot.DebugUtils;
import org.am.mmlbot.client.MmlbotClient;
import org.am.mmlbot.client.muscleMemory.Action;
import org.am.mmlbot.client.muscleMemory.Memory;
import org.jspecify.annotations.Nullable;

import java.util.LinkedList;
import java.util.Queue;

public class MemoryEvaluator {



    public float currentRating = 0;

    // REWARDS
    private final float missReward = -10f;
    private final float jumpReward = -4f;
    private final float rotationReward = -0.03f;
    private final float damageEnemyReward = 80f;
    private final float criticalEnemyDamageReward = 40f;
    private final float decreaseOwnHpReward = -5f;

    public MemoryEvaluator() {
        AttackEntityCallback.EVENT.register(this::onAttack);
    }

    private ActionResult onAttack(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {
        if (MmlbotClient.getBot().currentTarget == null)
            return ActionResult.PASS;

        if (!entity.getUuid().equals(MmlbotClient.getBot().currentTarget.getUuid()))
            return ActionResult.PASS;


        currentRating -= missReward; // roll back miss penalty;
        currentRating += damageEnemyReward; // TODO: vary by damage amount
        if (player.fallDistance > 0)
            currentRating += criticalEnemyDamageReward;

        return ActionResult.PASS;
    }

    public void onMemoryStarted(Memory memory) {
        currentRating = -20;
        for (Action action : memory.actions){
            if (action.attack)
                currentRating += missReward;
            if (action.jump)
                currentRating += jumpReward;
            currentRating += rotationReward * action.dYaw + rotationReward * action.dPitch;
        }
    }
}

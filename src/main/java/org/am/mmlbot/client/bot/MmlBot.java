package org.am.mmlbot.client.bot;

import net.minecraft.client.MinecraftClient;
import org.am.mmlbot.DebugUtils;
import org.am.mmlbot.MuscleMemoryAction;
import org.am.mmlbot.RankedAction;
import org.am.mmlbot.Situation;

import java.util.ArrayList;
import java.util.List;

public abstract class MmlBot {
    protected boolean enabled = false;
    public List<RankedAction> botMemory = new ArrayList<>();
    protected RankedAction currentRankedAction;
    protected int currentActionTick;

    // TODO: эти параметры индивидуальны для каждого наследуемого класса, надо их как то обозначить
    protected float minRankedActionDistance = 35f;
    protected int maxMuscleMemoryActionsLength = 2;

    public final void enable(MinecraftClient client) {
        if (enabled) return;
        enabled = true;
        onEnable(client);
    }

    public final void addRankedAction(RankedAction rankedAction){
        botMemory.add(rankedAction);
    }

    public final void performRankedAction(RankedAction rankedAction){
        currentRankedAction = rankedAction;
        currentActionTick = 0;
    }

    public List<MuscleMemoryAction> generateNewMuscleMemoryActions(){
        // TODO: mutate already existing may be more reliable
        DebugUtils.chat("Generating new random MMAction...");

        List<MuscleMemoryAction> result = new ArrayList<>();
        for(int i = 0; i < maxMuscleMemoryActionsLength; i++) { // idea: vary length?
            result.add(MuscleMemoryAction.generateRandom());
        }
        return result;
    }

    public final RankedAction getRankedActionBySituation(Situation situation){
        // TODO: LINEAR SEARCH IS VERY VERY SLOW. We have to migrate to optimized vector db as soon as possible.
        float minDistance = minRankedActionDistance; // flexible threshold
        RankedAction closestRankedAction = null;
        for (RankedAction rankedAction : botMemory){
            float distance = rankedAction.situation.calculateDistance(situation);
            if (distance < minDistance) {
                closestRankedAction = rankedAction;
                minDistance = distance;
            }
        }

        // Generating new ranked action if we haven't found something related
        if (closestRankedAction == null){
            closestRankedAction = new RankedAction(125, situation, generateNewMuscleMemoryActions());
            addRankedAction(closestRankedAction);
        }

        return closestRankedAction;
    }

    public final void disable(MinecraftClient client) {
        if (!enabled) return;
        enabled = false;
        onDisable(client);
    }

    public final void tick(MinecraftClient client) {
        if (!enabled) return;
        onTick(client);
    }

    protected abstract void onEnable(MinecraftClient client);
    protected abstract void onDisable(MinecraftClient client);
    protected abstract void onTick(MinecraftClient client);
}

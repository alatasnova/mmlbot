package org.am.mmlbot.client.bot;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.am.mmlbot.DebugUtils;
import org.am.mmlbot.client.MemoryManager;
import org.am.mmlbot.client.muscleMemory.Memory;
import org.am.mmlbot.client.muscleMemory.Situation;

import java.util.*;

public class MmlBot {
    protected boolean enabled = false;
    public final int maxMemorySize = 50;
    private final int maxRecentlyReplayedMemoriesLength = 5;
    public final int maxActionsLength = 2;

    public static final float minMemoryDistance = 20f;
    public static final float minMemoryMutateDistance = 80f;
    public final float newMemoryMutationRate = 0.25f;

    public final float ratingImportanceWhenChoosing = 0.1f;
    protected float memoriesForgettingSpeed = 0.00f;

    protected MemoryEvaluator memoryEvaluator;

    public List<Memory> botMemories = new ArrayList<>();

    protected Queue<Memory> recentlyReplayedMemories = new LinkedList<>();

    protected Memory currentMemory;
    protected int currentMemoryTick;
    protected MemoryManager memoryManager;

    public Entity currentTarget;

    public MmlBot(){
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
        memoryEvaluator = new MemoryEvaluator();
    }

    public final void enable() {
        if (enabled) return;
        enabled = true;
    }

    public final void disable() {
        if (!enabled) return;

        currentMemory = null;
        enabled = false;
    }

    public void setTarget(Entity entity){
        currentTarget = entity;
    }

    public final void addMemory(Memory memory) {
        if (botMemories.size() > maxMemorySize) {
            botMemories.stream()
                    .min(Comparator.comparingDouble(Memory::getRating))
                    .ifPresent(botMemories::remove);
        } else DebugUtils.chat("Current length " + botMemories.size());
        botMemories.add(memory);
    }

    public final void replayMemory(Memory memory){
        currentMemory = memory;
        currentMemoryTick = 0;
    }

    public final Memory getClosestMemoryBySituation(Situation situation){
        // TODO: LINEAR SEARCH IS VERY VERY SLOW. We have to migrate to optimized vector db as soon as possible.
        float minDistance = 100000;
        float maxRating = 100; // Starting from 100

        Memory closestMemory = null;
        Memory bestToMutate = null;
        for (Memory memory : botMemories){
            float distance = memory.situation.calculateWeighedDistance(situation) / (memory.rating * ratingImportanceWhenChoosing);
            if (distance < minDistance) {
                closestMemory = memory;
                minDistance = distance;
            }
            if (distance < minMemoryMutateDistance && memory.rating > maxRating){
                bestToMutate = memory;
                maxRating = memory.rating;
            }
        }

        // Generating new ranked action if we haven't found something related
        if (closestMemory == null){
            closestMemory = Memory.generateRandom(100, situation, maxActionsLength);
            addMemory(closestMemory);
            return closestMemory;
        }

        if (minDistance < minMemoryDistance){
            DebugUtils.chat("!!Using known action with rating " + closestMemory.rating);
            if (closestMemory.rating > 100)
                DebugUtils.chat("----- it's rating is greater than 100");
            return closestMemory;
        }

        if (bestToMutate != null){
            DebugUtils.chat("+++ Mutating action with rating " + bestToMutate.rating);
            bestToMutate = bestToMutate.generateMutated(situation, newMemoryMutationRate);
            addMemory(bestToMutate);
            return bestToMutate;
        }

        // yeah code duplication
        closestMemory = Memory.generateRandom(100, situation, maxActionsLength);
        addMemory(closestMemory);
        return closestMemory;
    }

    protected Situation generateObjectiveSituation(){
        MinecraftClient mc = MinecraftClient.getInstance();
        assert mc.player != null;

        return new Situation(mc.player, currentTarget, Situation.getDefaultWeights());
    }

    private void updateRatingsForPerformedActions() {
        float currentRewardMultiplier = 1.0f;
        DebugUtils.chat("Ranking current memory with " + memoryEvaluator.currentRating);
        for (Memory memory : recentlyReplayedMemories){
            if (memory == null)
                continue;
            memory.rating += memoryEvaluator.currentRating * currentRewardMultiplier;
            currentRewardMultiplier *= 0.5f;
        }

        // Forgetting
        Iterator<Memory> iterator = botMemories.iterator();
        while (iterator.hasNext()) {
            Memory memory = iterator.next();
            memory.rating -= memoriesForgettingSpeed;
            if (memory.rating < 0) {
                iterator.remove();
            }
        }
    }

    // THIS METHOD IS OK
    private void updateCurrentRankedAction(){
        Memory newMemory = getClosestMemoryBySituation(generateObjectiveSituation());


        recentlyReplayedMemories.add(newMemory);

        if (recentlyReplayedMemories.size() > maxRecentlyReplayedMemoriesLength)
            recentlyReplayedMemories.remove();

        memoryEvaluator.onMemoryStarted(newMemory);
        replayMemory(newMemory);
    }

    // THIS METHOD IS OK
    public final void tick(MinecraftClient client) {
        if (!enabled || client.player == null || currentTarget == null)
            return;

        if (currentMemory == null)
            updateCurrentRankedAction();

        // if action is completed, rate it and search for new
        if (currentMemory.getActionIdByTick(currentMemoryTick) == -1) {
            updateRatingsForPerformedActions();
            updateCurrentRankedAction();
        }

        currentMemory.performByTick(currentMemoryTick);
        currentMemoryTick++;
    }
}

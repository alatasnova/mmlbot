package org.am.mmlbot.client.bot;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.am.mmlbot.DebugUtils;
import org.am.mmlbot.client.muscleMemory.Action;
import org.am.mmlbot.client.muscleMemory.Memory;
import org.am.mmlbot.client.muscleMemory.Situation;

import java.util.ArrayList;
import java.util.List;

public class MmlBot {
    protected boolean enabled = false;

    public List<Memory> botMemories = new ArrayList<>();
    protected Memory currentMemory;
    protected int currentActionTick;

    protected Entity currentTarget;

    protected float minRankedActionDistance = 35f;
    protected int maxActionsLength = 2;

    public void setTarget(Entity entity){
        currentTarget = entity;
    }

    public final void addMemory(Memory memory){
        botMemories.add(memory);
    }

    public final void replayMemory(Memory memory){
        currentMemory = memory;
        currentActionTick = 0;
    }

    public List<Action> generateNewActions(){
        // TODO: mutate already existing may be more reliable
        DebugUtils.chat("Generating new random MMAction...");

        List<Action> result = new ArrayList<>();
        for(int i = 0; i < maxActionsLength; i++) { // idea: vary length? - mirka: yup
            result.add(Action.generateRandom());
        }
        return result;
    }

    public final Memory getClosestMemoryBySituation(Situation situation){
        // TODO: LINEAR SEARCH IS VERY VERY SLOW. We have to migrate to optimized vector db as soon as possible.
        float minDistance = minRankedActionDistance; // flexible threshold
        Memory closestMemory = null;
        for (Memory memory : botMemories){
            float distance = memory.situation.calculateDistance(situation);
            if (distance < minDistance) {
                closestMemory = memory;
                minDistance = distance;
            }
        }

        // Generating new ranked action if we haven't found something related
        if (closestMemory == null){
            closestMemory = new Memory(125, situation, generateNewActions());
            addMemory(closestMemory);
        }

        return closestMemory;
    }

    protected Situation getCurrentSituation(){
        MinecraftClient mc = MinecraftClient.getInstance();
        assert mc.player != null;

        Situation sit = new Situation(mc.player, currentTarget);

        DebugUtils.chat(sit.toString());

        return sit;
    }

    private void updateCurrentRankedAction(){
        Memory newMemory = getClosestMemoryBySituation(getCurrentSituation());
        replayMemory(newMemory);
    }

    public final void tick(MinecraftClient client) {
        if (!enabled || client.player == null)
            return;

        if (currentMemory == null)
            updateCurrentRankedAction();

        // if action is completed search for new
//        if (currentRankedAction.getActionIdByTick(currentActionTick) == -1)
//            updateCurrentRankedAction();

        currentMemory.performByTick(currentActionTick);
        currentActionTick++;
    }

    public final void enable() {
        if (enabled) return;
        enabled = true;
    }

    public final void disable() {
        if (!enabled) return;
        currentMemory = null; // noo why would we reset all the memory... save it?
        enabled = false;
    }
}

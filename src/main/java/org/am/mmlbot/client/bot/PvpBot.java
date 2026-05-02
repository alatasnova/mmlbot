package org.am.mmlbot.client.bot;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.am.mmlbot.DebugUtils;
import org.am.mmlbot.RankedAction;
import org.am.mmlbot.Situation;

public class PvpBot extends MmlBot{
    protected Entity currentEnemy;

    public void setCurrentEnemy(Entity entity){
        currentEnemy = entity;
    }

    @Override
    protected void onEnable(MinecraftClient client) {

    }

    @Override
    protected void onDisable(MinecraftClient client) {
        currentRankedAction = null;
    }

    protected Situation getCurrentSituation(){
        MinecraftClient mc = MinecraftClient.getInstance();
        assert mc.player != null;

        Situation sit = new Situation(mc.player, currentEnemy);

        DebugUtils.chat(sit.toString());

        return sit;
    }

    private void updateCurrentRankedAction(){
        RankedAction newRankedAction = getRankedActionBySituation(getCurrentSituation());
        performRankedAction(newRankedAction);
    }

    @Override
    protected void onTick(MinecraftClient client) {
        if (!enabled || client.player == null)
            return;

        if (currentRankedAction == null)
            updateCurrentRankedAction();

        // if action is completed search for new
//        if (currentRankedAction.getActionIdByTick(currentActionTick) == -1)
//            updateCurrentRankedAction();

        currentRankedAction.performByTick(currentActionTick);
        currentActionTick++;
    }
}

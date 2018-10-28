package com.evacipated.cardcrawl.mod.stslib.actions.defect;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.relics.GoldPlatedCables;

public class TriggerPassiveAction extends AbstractGameAction
{
    private AbstractOrb targetOrb;
    private int amount;

    public TriggerPassiveAction()
    {
        this(AbstractDungeon.player.orbs.get(0), 1);
    }

    public TriggerPassiveAction(AbstractOrb targetOrb)
    {
        this(targetOrb, 1);
    }

    public TriggerPassiveAction(int amount)
    {
        this(AbstractDungeon.player.orbs.get(0), amount);
    }

    public TriggerPassiveAction(int targindex, int amount)
    {
        this(AbstractDungeon.player.orbs.get(targindex), amount);
    }

    public TriggerPassiveAction(AbstractOrb targetOrb, int amount)
    {
        duration = Settings.ACTION_DUR_FAST;
        this.targetOrb = targetOrb;
        this.amount = amount;
        if (AbstractDungeon.player.hasRelic(GoldPlatedCables.ID) && AbstractDungeon.player.orbs.get(0) == targetOrb) {
            amount++;
        }
    }

    @Override
    public void update()
    {
        if (duration == Settings.ACTION_DUR_FAST && !AbstractDungeon.player.orbs.isEmpty()) {
            for (int i = 0; i < amount; i++) {
                targetOrb.onStartOfTurn();
                targetOrb.onEndOfTurn();
            }
        }
        tickDuration();
    }
}

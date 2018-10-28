package com.evacipated.cardcrawl.mod.stslib.actions.defect;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

public class EvokeSpecificOrbAction extends AbstractGameAction
{
    private AbstractOrb orb;

    public EvokeSpecificOrbAction(AbstractOrb orb)
    {
        duration = Settings.ACTION_DUR_FAST;
        this.orb = orb;
        actionType = ActionType.DAMAGE;
    }

    @Override
    public void update()
    {
        if (duration == Settings.ACTION_DUR_FAST) {
            if (orb != null) {
                AbstractDungeon.player.orbs.remove(orb);
                AbstractDungeon.player.orbs.add(0, orb);
                AbstractDungeon.player.evokeOrb();
            }
        }
        this.tickDuration();
    }
}

package com.evacipated.cardcrawl.mod.stslib.actions.defect;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

public class EvokeSpecificOrbAction extends AbstractGameAction
{
    private AbstractOrb orb;
    
    public EvokeSpecificOrbAction(final AbstractOrb orb) {
        this.duration = Settings.ACTION_DUR_FAST;
        this.orb = orb;
        this.actionType = ActionType.DAMAGE;
    }
    
    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
        	if (this.orb != null) {
        		int slot = AbstractDungeon.player.orbs.indexOf(orb);
            	AbstractDungeon.player.orbs.remove(slot);
            	AbstractDungeon.player.orbs.add(0, orb);
            	AbstractDungeon.player.evokeOrb();
        	}
        }
        this.tickDuration();
    }
}

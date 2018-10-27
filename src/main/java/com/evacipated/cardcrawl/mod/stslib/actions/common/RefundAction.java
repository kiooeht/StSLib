package com.evacipated.cardcrawl.mod.stslib.actions.common;

import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.*;
import java.util.*;

public class RefundAction extends AbstractGameAction
{
	private int energyGain;
	private int energyCap;
    private AbstractCard targetCard;
    
    public RefundAction(final AbstractCard targetCard) {
		this (targetCard, 999);
	}
    public RefundAction(final AbstractCard targetCard, final int energyCap) {
        this.targetCard = targetCard;
        this.setValues(AbstractDungeon.player, AbstractDungeon.player, 0);
        this.duration = Settings.ACTION_DUR_FAST;
		this.energyCap = energyCap;
		if (targetCard.costForTurn == -1) {
			this.energyGain = targetCard.energyOnUse;
		} else {
			this.energyGain = targetCard.costForTurn;
		}
        if (this.energyGain > energyCap) {
			this.energyGain = energyCap;
		}
    }
    
    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            AbstractDungeon.player.gainEnergy(this.energyGain);
            AbstractDungeon.actionManager.updateEnergyGain(this.energyGain);
            for (final AbstractCard c : AbstractDungeon.player.hand.group) {
                c.triggerOnGainEnergy(this.energyGain, true);
            }
        }
        this.tickDuration();
    }
}

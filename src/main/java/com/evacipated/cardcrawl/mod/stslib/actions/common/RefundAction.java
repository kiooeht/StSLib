package com.evacipated.cardcrawl.mod.stslib.actions.common;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class RefundAction extends AbstractGameAction
{
    private int energyGain;
    private int energyCap;
    private AbstractCard targetCard;

    public RefundAction(AbstractCard targetCard)
    {
        this(targetCard, 999);
    }

    public RefundAction(AbstractCard targetCard, int energyCap)
    {
        this(targetCard, energyCap, targetCard.energyOnUse);
    }
    public RefundAction(AbstractCard targetCard, int energyCap, int energyOnUse)
    {
        this.targetCard = targetCard;
        setValues(AbstractDungeon.player, AbstractDungeon.player, 0);
        duration = Settings.ACTION_DUR_FAST;
        this.energyCap = energyCap;
        if (targetCard.costForTurn == -1) {
            energyGain = energyOnUse;
        } else {
            energyGain = targetCard.costForTurn;
        }
        if (energyGain > energyCap) {
            energyGain = energyCap;
        }
    }

    @Override
    public void update()
    {
        if (duration == Settings.ACTION_DUR_FAST) {
            AbstractDungeon.player.gainEnergy(energyGain);
            AbstractDungeon.actionManager.updateEnergyGain(energyGain);
            for (AbstractCard c : AbstractDungeon.player.hand.group) {
                c.triggerOnGainEnergy(energyGain, true);
            }
        }
        tickDuration();
    }
}

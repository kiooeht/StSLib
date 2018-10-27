package com.evacipated.cardcrawl.mod.stslib.powers;

import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class ExhaustiveNegationPower extends AbstractPower
{
    public static final String POWER_ID = "stslib:ExhaustiveNegationPower";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = ExhaustiveNegationPower.powerStrings.NAME;
    public static final String[] DESCRIPTIONS = ExhaustiveNegationPower.powerStrings.DESCRIPTIONS;

    public ExhaustiveNegationPower(AbstractCreature owner, int amount)
    {
        name = ExhaustiveNegationPower.NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        isTurnBased = false;
        updateDescription();
        loadRegion("artifact");
    }

    public void onSpecificTrigger()
    {
        flash();
        if (amount <= 0) {
            AbstractDungeon.actionManager.addToTop(new RemoveSpecificPowerAction(owner, owner, ID));
        } else {
            AbstractDungeon.actionManager.addToTop(new ReducePowerAction(owner, owner, ID, 1));
        }
    }

    public void updateDescription()
    {
        description = ExhaustiveNegationPower.DESCRIPTIONS[0] + amount + ExhaustiveNegationPower.DESCRIPTIONS[1];
    }
}

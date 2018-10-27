package com.evacipated.cardcrawl.mod.stslib.powers;

import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.*;

public class ExhaustiveNegationPower extends AbstractPower
{
    public static final String POWER_ID = "stslib:ExhaustiveNegationPower";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    
    public ExhaustiveNegationPower(final AbstractCreature owner, final int amount) {
        this.name = ExhaustiveNegationPower.NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.isTurnBased = false;
        this.updateDescription();
        this.loadRegion("artifact");
    }
    
    public void onSpecificTrigger() {
        this.flash();
        if (this.amount <= 0) {
            AbstractDungeon.actionManager.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));
        }
        else {
            AbstractDungeon.actionManager.addToTop(new ReducePowerAction(this.owner, this.owner, this.ID, 1));
        }
    }
    
    public void updateDescription() {
        this.description = ExhaustiveNegationPower.DESCRIPTIONS[0] + this.amount + ExhaustiveNegationPower.DESCRIPTIONS[1];
    }
    
    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = ExhaustiveNegationPower.powerStrings.NAME;
        DESCRIPTIONS = ExhaustiveNegationPower.powerStrings.DESCRIPTIONS;
    }
}

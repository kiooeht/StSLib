package com.evacipated.cardcrawl.mod.stslib.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class StunMonsterPower extends AbstractPower
{
    public static final String POWER_ID = "stslib:Stunned";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private byte moveByte;
    private AbstractMonster.Intent moveIntent;

    public StunMonsterPower(AbstractMonster owner)
    {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        amount = 1;
        type = PowerType.DEBUFF;
        updateDescription();
        img = ImageMaster.loadImage("images/powers/32/stun.png");

        moveByte = owner.nextMove;
        moveIntent = owner.intent;
    }

    @Override
    public void updateDescription()
    {
        description = DESCRIPTIONS[0] + amount;
        if (amount == 1) {
            description += DESCRIPTIONS[1];
        } else {
            description += DESCRIPTIONS[2];
        }
    }

    @Override
    public void atEndOfRound()
    {
        reducePower(1);
        if (amount <= 0) {
            if (owner instanceof AbstractMonster) {
                AbstractMonster m = (AbstractMonster)owner;
                m.setMove(moveByte, moveIntent);
                m.createIntent();
            }
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(owner, owner, ID));
        }
    }
}

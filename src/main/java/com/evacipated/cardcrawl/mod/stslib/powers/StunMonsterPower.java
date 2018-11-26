package com.evacipated.cardcrawl.mod.stslib.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.lang.reflect.Field;

public class StunMonsterPower extends AbstractPower
{
    public static final String POWER_ID = "stslib:Stunned";
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private byte moveByte;
    private AbstractMonster.Intent moveIntent;
    private EnemyMoveInfo move;

    public StunMonsterPower(AbstractMonster owner)
    {
        this(owner, 1);
    }

    public StunMonsterPower(AbstractMonster owner, int amount)
    {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        type = PowerType.DEBUFF;
        updateDescription();
        img = ImageMaster.loadImage("images/stslib/powers/32/stun.png");

        moveByte = owner.nextMove;
        moveIntent = owner.intent;
        try {
            Field f = AbstractMonster.class.getDeclaredField("move");
            f.setAccessible(true);
            move = (EnemyMoveInfo) f.get(owner);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
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
                if (move != null) {
                    m.setMove(moveByte, moveIntent, move.baseDamage, move.multiplier, move.isMultiDamage);
                } else {
                    m.setMove(moveByte, moveIntent);
                }
                m.createIntent();
                m.applyPowers();
            }
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(owner, owner, ID));
        }
    }
}

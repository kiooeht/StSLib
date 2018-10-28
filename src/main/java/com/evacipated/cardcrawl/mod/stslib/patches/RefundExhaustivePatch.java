package com.evacipated.cardcrawl.mod.stslib.patches;

import com.evacipated.cardcrawl.mod.stslib.actions.common.RefundAction;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.ExhaustiveField;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.RefundFields;
import com.evacipated.cardcrawl.mod.stslib.variables.ExhaustiveVariable;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.Corruption;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

@SpirePatch(
        clz=AbstractPlayer.class,
        method="useCard"
)
public class RefundExhaustivePatch
{
    public static void Prefix(AbstractPlayer p, AbstractCard c, AbstractMonster monster, int energyOnUse)
    {
        if (ExhaustiveField.ExhaustiveFields.exhaustive.get(c) > -1) {
            ExhaustiveVariable.increment(c);
        }
    }

    public static void Postfix(AbstractPlayer p, AbstractCard c, AbstractMonster monster, int energyOnUse)
    {
        if (RefundFields.refund.get(c) > 0) {
            if (!c.freeToPlayOnce
                    && ((c.costForTurn == -1 && energyOnUse > 0) || c.costForTurn > 0
                    && (!p.hasPower(Corruption.ID)
                    || c.type != AbstractCard.CardType.SKILL))) {
                AbstractDungeon.actionManager.addToBottom(new RefundAction(c, RefundFields.refund.get(c), energyOnUse));
            }
        }
    }
}

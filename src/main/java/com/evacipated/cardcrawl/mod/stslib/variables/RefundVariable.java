package com.evacipated.cardcrawl.mod.stslib.variables;

import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.ExhaustiveField;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.RefundFields;
import com.evacipated.cardcrawl.mod.stslib.powers.ExhaustiveNegationPower;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import basemod.abstracts.DynamicVariable;

public class RefundVariable extends DynamicVariable
{
    @Override
    public String key()
    {
        return "stslib:refund";
    }

    @Override
    public boolean isModified(AbstractCard card)
    {
        return RefundFields.refund.get(card).intValue() != RefundFields.baseRefund.get(card).intValue();
    }

    @Override
    public int value(AbstractCard card)
    {
        return RefundFields.refund.get(card);
    }

    @Override
    public int baseValue(AbstractCard card)
    {
        return RefundFields.baseRefund.get(card);
    }

    @Override
    public boolean upgraded(AbstractCard card)
    {
        return RefundFields.isRefundUpgraded.get(card);
    }

    public static void setBaseValue(AbstractCard card, int amount)
    {
    	RefundFields.baseRefund.set(card, amount);
        RefundFields.refund.set(card, amount);
        card.initializeDescription();
    }

    public static void upgrade(AbstractCard card, int amount)
    {
    	RefundFields.isRefundUpgraded.set(card, true);
        setBaseValue(card, RefundFields.baseRefund.get(card) + amount);
    }
}

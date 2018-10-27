package com.evacipated.cardcrawl.mod.stslib.variables;

import basemod.abstracts.DynamicVariable;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.ExhaustiveField;
import com.evacipated.cardcrawl.mod.stslib.powers.ExhaustiveNegationPower;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ExhaustiveVariable extends DynamicVariable
{
    @Override
    public String key()
    {
        return "stslib:ex";
    }

    @Override
    public boolean isModified(AbstractCard card)
    {
        return ExhaustiveField.ExhaustiveFields.exhaustive.get(card).intValue() != ExhaustiveField.ExhaustiveFields.baseExhaustive.get(card).intValue();
    }

    @Override
    public int value(AbstractCard card)
    {
        return ExhaustiveField.ExhaustiveFields.exhaustive.get(card);
    }

    @Override
    public int baseValue(AbstractCard card)
    {
        return ExhaustiveField.ExhaustiveFields.baseExhaustive.get(card);
    }

    @Override
    public boolean upgraded(AbstractCard card)
    {
        return ExhaustiveField.ExhaustiveFields.isExhaustiveUpgraded.get(card);
    }

    public static void setBaseValue(AbstractCard card, int amount)
    {
        ExhaustiveField.ExhaustiveFields.baseExhaustive.set(card, amount);
        ExhaustiveField.ExhaustiveFields.exhaustive.set(card, amount);
        card.initializeDescription();
    }

    public static void upgrade(AbstractCard card, int amount)
    {
        ExhaustiveField.ExhaustiveFields.isExhaustiveUpgraded.set(card, true);
        setBaseValue(card, ExhaustiveField.ExhaustiveFields.baseExhaustive.get(card) + amount);
    }

    public static void increment(AbstractCard card)
    {
        if (AbstractDungeon.player.hasPower(ExhaustiveNegationPower.POWER_ID)) {
            AbstractDungeon.player.getPower(ExhaustiveNegationPower.POWER_ID).onSpecificTrigger();
            return;
        }
        ExhaustiveField.ExhaustiveFields.exhaustive.set(card, ExhaustiveField.ExhaustiveFields.exhaustive.get(card) - 1);
        if (ExhaustiveField.ExhaustiveFields.exhaustive.get(card) <= 0) {
            card.exhaust = true;
        }
        card.initializeDescription();
    }
}
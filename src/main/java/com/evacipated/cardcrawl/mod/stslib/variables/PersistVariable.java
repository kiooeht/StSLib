package com.evacipated.cardcrawl.mod.stslib.variables;

import basemod.abstracts.DynamicVariable;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.PersistFields;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class PersistVariable extends DynamicVariable
{
    @Override
    public String key()
    {
        return "stslib:persist";
    }

    @Override
    public boolean isModified(AbstractCard card)
    {
        return PersistFields.persist.get(card).intValue() != PersistFields.basePersist.get(card).intValue();
    }

    @Override
    public int value(AbstractCard card)
    {
        return PersistFields.persist.get(card);
    }

    @Override
    public int baseValue(AbstractCard card)
    {
        return PersistFields.basePersist.get(card);
    }

    @Override
    public boolean upgraded(AbstractCard card)
    {
        return PersistFields.isPersistUpgraded.get(card);
    }
}
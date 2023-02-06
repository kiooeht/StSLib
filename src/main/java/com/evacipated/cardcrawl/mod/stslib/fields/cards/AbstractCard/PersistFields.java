package com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;

@SpirePatch(
        clz=AbstractCard.class,
        method=SpirePatch.CLASS
)
public class PersistFields
{
    public static SpireField<Integer> persist = new SpireField<>(() -> -1);
    public static SpireField<Integer> basePersist = new SpireField<>(() -> -1);
    public static SpireField<Boolean> isPersistUpgraded = new SpireField<>(() -> false);

    public static void setBaseValue(AbstractCard card, int amount)
    {
        basePersist.set(card, amount);
        persist.set(card, amount);
        card.initializeDescription();
    }

    public static void upgrade(AbstractCard card, int amount)
    {
        isPersistUpgraded.set(card, true);
        setBaseValue(card, basePersist.get(card) + amount);
    }

    public static void decrement(AbstractCard card)
    {
        persist.set(card, persist.get(card) - 1);
    }
}

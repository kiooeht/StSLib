package com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class ExhaustiveField
{
    @SpirePatch(
            clz=AbstractCard.class,
            method=SpirePatch.CLASS
    )
    public static class ExhaustiveFields
    {
        public static SpireField<Integer> exhaustive = new SpireField<>(() -> -1);
        public static SpireField<Integer> baseExhaustive = new SpireField<>(() -> -1);
        public static SpireField<Boolean> isExhaustiveUpgraded = new SpireField<>(() -> false);
    }
}

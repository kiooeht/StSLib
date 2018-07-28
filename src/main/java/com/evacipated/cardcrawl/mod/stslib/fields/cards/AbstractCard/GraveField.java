package com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

@SpirePatch(
        cls="com.megacrit.cardcrawl.cards.AbstractCard",
        method=SpirePatch.CLASS
)
public class GraveField
{
    public static SpireField<Boolean> grave = new SpireField<>(() -> false);
}

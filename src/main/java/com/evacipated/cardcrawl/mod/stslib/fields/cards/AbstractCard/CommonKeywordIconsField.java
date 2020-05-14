package com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;

@SpirePatch(
        clz = AbstractCard.class,
        method=SpirePatch.CLASS
)
public class CommonKeywordIconsField {
    public static SpireField<Boolean> useIcons = new SpireField<>(() -> true);
}

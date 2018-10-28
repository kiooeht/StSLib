package com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;

@SpirePatch(
        clz=AbstractCard.class,
        method=SpirePatch.CLASS
)
public class RefundFields
{
    public static SpireField<Integer> refund = new SpireField<>(() -> 0);
    public static SpireField<Integer> baseRefund = new SpireField<>(() -> 0);
    public static SpireField<Boolean> isRefundUpgraded = new SpireField<>(() -> false);
}

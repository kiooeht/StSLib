package com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;

@SpirePatch(
        cls="com.megacrit.cardcrawl.cards.AbstractCard",
        method=SpirePatch.CLASS
)
public class SneckoField
{
    public static SpireField<Boolean> snecko = new SneckoFieldType(() -> false);

    // This is done so card cost is automatically set to -1
    private static class SneckoFieldType extends SpireField<Boolean>
    {
        SneckoFieldType(DefaultValue<Boolean> defaultValue)
        {
            super(defaultValue);
        }

        @Override
        public void set(Object __intance, Boolean value)
        {
            super.set(__intance, value);
            if (value && __intance instanceof AbstractCard) {
                ((AbstractCard)__intance).cost = -1;
            }
        }
    }
}
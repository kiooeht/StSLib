package com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;

@SpirePatch(
        cls="com.megacrit.cardcrawl.cards.AbstractCard",
        method=SpirePatch.CLASS
)
public class AlwaysRetainField
{
    public static SpireField<Boolean> alwaysRetain = new RetainField(false);

    // This is done so `retain` can be set automatically when this field is set in a card's constructor
    private static class RetainField extends SpireField<Boolean>
    {
        RetainField(Boolean defaultValue)
        {
            super(defaultValue);
        }

        @Override
        public void set(Object __intance, Boolean value)
        {
            super.set(__intance, value);
            if (value && __intance instanceof AbstractCard) {
                ((AbstractCard)__intance).retain = true;
            }
        }
    }
}

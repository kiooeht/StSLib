package com.evacipated.cardcrawl.mod.stslib.patches;

import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.AlwaysRetainField;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(
        cls="com.megacrit.cardcrawl.characters.AbstractPlayer",
        method="applyStartOfTurnCards"
)
public class AlwaysRetainPatch
{
    @SpireInsertPatch(
            localvars={"c"}
    )
    public static void Insert(AbstractPlayer __instance, AbstractCard c)
    {
        if (c != null && AlwaysRetainField.alwaysRetain.get(c)) {
            c.retain = true;
        }
    }

    public static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher("com.megacrit.cardcrawl.cards.AbstractCard", "atTurnStart");

            return LineFinder.findAllInOrder(ctBehavior, new ArrayList<>(), finalMatcher);
        }
    }
}

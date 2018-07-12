package com.evacipated.cardcrawl.mod.stslib.patches;

import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.SneckoField;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.ArrayList;

public class SneckoPatch
{
    @SpirePatch(
            cls="com.megacrit.cardcrawl.characters.AbstractPlayer",
            method="draw",
            paramtypes={"int"}
    )
    public static class Draw
    {
        @SpireInsertPatch(
                localvars = {"c"}
        )
        public static void Insert(AbstractPlayer __instance, int numCards, AbstractCard c)
        {
            if (c != null && SneckoField.snecko.get(c)) {
                int newCost = AbstractDungeon.cardRandomRng.random(3);
                if (c.cost != newCost) {
                    c.cost = newCost;
                    c.costForTurn = c.cost;
                    c.isCostModified = true;
                }
            }
        }

        public static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher("com.megacrit.cardcrawl.cards.AbstractCard", "triggerWhenDrawn");

                return LineFinder.findAllInOrder(ctBehavior, new ArrayList<>(), finalMatcher);
            }
        }
    }

    @SpirePatch(
            cls="com.megacrit.cardcrawl.cards.AbstractCard",
            method="getCost"
    )
    public static class GetCost
    {
        public static String Postfix(String __result, AbstractCard __instance)
        {
            if (!__instance.isCostModified && SneckoField.snecko.get(__instance)) {
                return "?";
            }
            return __result;
        }
    }

    @SpirePatch(
            cls="com.megacrit.cardcrawl.screens.SingleCardViewPopup",
            method="renderCost"
    )
    public static class PortraitViewCost
    {
        public static ExprEditor Instrument()
        {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException
                {
                    if (m.getClassName().equals("com.megacrit.cardcrawl.helpers.FontHelper") && m.getMethodName().equals("renderFont")) {
                        m.replace("if (((Boolean) com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.SneckoField.snecko.get(card)).booleanValue()) {" +
                                "$3 = \"?\";" +
                                "$4 = 674.0f * com.megacrit.cardcrawl.core.Settings.scale;" +
                                "}" +
                                "$_ = $proceed($$);");
                    }
                }
            };
        }
    }
}
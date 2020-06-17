package com.evacipated.cardcrawl.mod.stslib.patches;

import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.SoulboundField;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.FountainOfCurseRemoval;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.Astrolabe;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

import java.util.ArrayList;

public class SoulboundPatch
{
    @SpirePatch(
            clz=CardGroup.class,
            method="getPurgeableCards"
    )
    public static class CardGroup_getPurgeableCards
    {
        public static CardGroup Postfix(CardGroup __result, CardGroup __instance)
        {
            __result.group.removeIf(c -> SoulboundField.soulbound.get(c));
            return __result;
        }
    }

    @SpirePatch(
            clz=AbstractPlayer.class,
            method="isCursed"
    )
    public static class AbstractPlayer_isCursed
    {
        public static ExprEditor Instrument()
        {
            return new ExprEditor() {
                @Override
                public void edit(FieldAccess f) throws CannotCompileException
                {
                    if (f.isReader() && f.getClassName().equals(AbstractCard.class.getName()) && f.getFieldName().equals("type")) {
                        f.replace(
                                "if (((" + Boolean.class.getName() + ")" + SoulboundField.class.getName() + ".soulbound.get($0)).booleanValue()) {" +
                                        "$_ = null;" +
                                        "} else {" +
                                        "$_ = $proceed($$);" +
                                        "}"
                        );
                    }
                }
            };
        }
    }

    @SpirePatch(
            clz= FountainOfCurseRemoval.class,
            method="buttonEffect"
    )
    public static class FountainOfCurseRemoval_buttonEffect
    {
        public static ExprEditor Instrument()
        {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException
                {
                    if ((m.getClassName().equals("java.util.ArrayList") && m.getMethodName().equals("add"))
                            || (m.getClassName().equals("com.megacrit.cardcrawl.cards.CardGroup") && m.getMethodName().equals("removeCard"))) {
                        m.replace("if (com.evacipated.cardcrawl.mod.stslib.patches.SoulboundPatch.FountainOfCurseRemoval_buttonEffect.canRemove(i))" +
                                "{ $_ = $proceed($$); }");
                    }
                }
            };
        }

        public static boolean canRemove(int i)
        {
            return !SoulboundField.soulbound.get(AbstractDungeon.player.masterDeck.group.get(i));
        }
    }

    @SpirePatch(
            clz=CardLibrary.class,
            method="getCurse",
            paramtypes={}
    )
    public static class CardLibrary_getCurse1
    {
        @SpireInsertPatch(
                locator=Locator.class,
                localvars={"tmp"}
        )
        public static void Insert(@ByRef ArrayList<String>[] tmp)
        {
            tmp[0].removeIf(id -> CardLibrary.cards.get(id).rarity == AbstractCard.CardRarity.SPECIAL);
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "get");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz=CardLibrary.class,
            method="getCurse",
            paramtypez={
                    AbstractCard.class,
                    Random.class
            }
    )
    public static class CardLibrary_getCurse2
    {
        @SpireInsertPatch(
                locator=Locator.class,
                localvars={"tmp"}
        )
        public static void Insert(AbstractCard prohibitedCard, Random rng, @ByRef ArrayList<String>[] tmp)
        {
            tmp[0].removeIf(id -> CardLibrary.cards.get(id).rarity == AbstractCard.CardRarity.SPECIAL);
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "get");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz=Astrolabe.class,
            method="onEquip"
    )
    public static class Astrolabe_onEquip
    {
        @SpireInsertPatch(
                locator=Locator.class,
                localvars={"tmp"}
        )
        public static void Insert(Astrolabe __instance, @ByRef CardGroup[] tmp)
        {
            tmp[0].group.removeIf(c -> SoulboundField.soulbound.get(c));
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "isEmpty");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}

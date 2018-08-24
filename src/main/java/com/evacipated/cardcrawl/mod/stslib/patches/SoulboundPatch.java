package com.evacipated.cardcrawl.mod.stslib.patches;

import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.SoulboundField;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.cards.curses.Necronomicurse;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.Astrolabe;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.ArrayList;

public class SoulboundPatch
{
    @SpirePatch(
            cls="com.megacrit.cardcrawl.cards.CardGroup",
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
            cls="com.megacrit.cardcrawl.characters.AbstractPlayer",
            method="isCursed"
    )
    public static class AbstractPlayer_isCursed
    {
        // TODO: Make this not a Replace patch
        public static boolean Replace(AbstractPlayer __instance)
        {
            boolean cursed = false;
            for (AbstractCard c : __instance.masterDeck.group) {
                if (c.type == AbstractCard.CardType.CURSE
                        && !c.cardID.equals(Necronomicurse.ID) && !c.cardID.equals(AscendersBane.ID)
                        && !SoulboundField.soulbound.get(c)) {
                    cursed = true;
                    break;
                }
            }
            return cursed;
        }
    }

    @SpirePatch(
            cls="com.megacrit.cardcrawl.events.shrines.FountainOfCurseRemoval",
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
            cls="com.megacrit.cardcrawl.helpers.CardLibrary",
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
                Matcher finalMatcher = new Matcher.MethodCallMatcher("java.util.ArrayList", "get");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }
    }

    @SpirePatch(
            cls="com.megacrit.cardcrawl.helpers.CardLibrary",
            method="getCurse",
            paramtypes={"com.megacrit.cardcrawl.cards.AbstractCard", "com.megacrit.cardcrawl.random.Random"}
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
                Matcher finalMatcher = new Matcher.MethodCallMatcher("java.util.ArrayList", "get");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }
    }

    @SpirePatch(
            cls="com.megacrit.cardcrawl.relics.Astrolabe",
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
                Matcher finalMatcher = new Matcher.MethodCallMatcher("java.util.ArrayList", "isEmpty");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }
    }
}

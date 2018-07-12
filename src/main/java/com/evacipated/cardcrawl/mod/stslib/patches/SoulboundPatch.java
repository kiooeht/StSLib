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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoulboundPatch
{
    @SpirePatch(
            cls="com.megacrit.cardcrawl.cards.CardGroup",
            method="removeRandomCard"
    )
    public static class CardGroup_removeRandomCard
    {
        @SpireInsertPatch(
                localvars={"c"}
        )
        public static void Insert(CardGroup __instance, @ByRef AbstractCard[] c)
        {
            while (c[0].cardID.equals(Necronomicurse.ID) || c[0].cardID.equals(AscendersBane.ID)
                    || SoulboundField.soulbound.get(c[0])) {
                c[0] = __instance.getRandomCard(true);
            }
        }

        public static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher("com.megacrit.cardcrawl.cards.CardGroup", "removeCard");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }
    }

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
        // TODO: Make this not a Replace patch
        public static AbstractCard Replace()
        {
            return CardLibrary.getCurse(null, AbstractDungeon.cardRng);
        }
    }

    @SpirePatch(
            cls="com.megacrit.cardcrawl.helpers.CardLibrary",
            method="getCurse",
            paramtypes={"com.megacrit.cardcrawl.cards.AbstractCard", "com.megacrit.cardcrawl.random.Random"}
    )
    public static class CardLibrary_getCurse2
    {
        /*
        // TODO: Make this not a Replace patch
        public static AbstractCard Replace(AbstractCard prohibitedCard, Random rng)
        {
            // Must be done as an extra method call or we get a
            // Missing BootstrapMethods attribute error
            return call(prohibitedCard, rng);
        }
        */
        @SpireInsertPatch(
                localvars={"tmp"}
        )
        public static void Insert(AbstractCard prohibitedCard, Random rng, @ByRef ArrayList<String>[] tmp)
        {
            tmp[0].removeIf(id -> CardLibrary.cards.get(id).rarity == AbstractCard.CardRarity.SPECIAL);
        }

        public static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher("java.util.ArrayList", "get");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }

        private static AbstractCard call(AbstractCard prohibitedCard, Random rng)
        {
            List<String> tmp = acceptableCurses();
            if (prohibitedCard != null) {
                tmp.removeIf(id -> id.equals(prohibitedCard.cardID));
            }
            return CardLibrary.cards.get(tmp.get(rng.random(0, tmp.size() - 1)));
        }

        private static List<String> acceptableCurses()
        {
            HashMap<String, AbstractCard> curses = null;
            try {
                Field f = CardLibrary.class.getDeclaredField("curses");
                f.setAccessible(true);
                curses = (HashMap<String, AbstractCard>) f.get(null);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }

            ArrayList<String> tmp = new ArrayList<>();
            if (curses != null) {
                for (Map.Entry<String, AbstractCard> c : curses.entrySet()) {
                    if (c.getValue().rarity != AbstractCard.CardRarity.SPECIAL) {
                        tmp.add(c.getKey());
                    }
                }
            }
            return tmp;
        }
    }

    @SpirePatch(
            cls="com.megacrit.cardcrawl.relics.Astrolabe",
            method="onEquip"
    )
    public static class Astrolabe_onEquip
    {
        @SpireInsertPatch(
                localvars={"tmp"}
        )
        public static void Insert(Astrolabe __instance, @ByRef CardGroup[] tmp)
        {
            tmp[0].group.removeIf(c -> SoulboundField.soulbound.get(c));
        }

        public static class Locator extends SpireInsertLocator
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

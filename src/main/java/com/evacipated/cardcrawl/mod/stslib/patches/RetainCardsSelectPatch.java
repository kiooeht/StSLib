package com.evacipated.cardcrawl.mod.stslib.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.unique.RetainCardsAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RetainCardsSelectPatch
{
    private static List<AbstractCard> savedCards = new ArrayList<>();

    @SpirePatch(
            cls="com.megacrit.cardcrawl.actions.unique.RetainCardsAction",
            method="update"
    )
    public static class Before
    {
        @SpireInsertPatch(
                locator=Locator.class
        )
        public static void Insert(RetainCardsAction __instance)
        {
            Iterator<AbstractCard> it = AbstractDungeon.player.hand.group.iterator();
            while (it.hasNext()) {
                AbstractCard card = it.next();
                if (card.retain) {
                    it.remove();
                    savedCards.add(card);
                }
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher("com.megacrit.cardcrawl.screens.select.HandCardSelectScreen", "open");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }
    }

    @SpirePatch(
            cls="com.megacrit.cardcrawl.actions.unique.RetainCardsAction",
            method="update"
    )
    public static class After
    {
        @SpireInsertPatch(
                locator=Locator.class
        )
        public static void Insert(RetainCardsAction __instance)
        {
            for (AbstractCard card : savedCards) {
                AbstractDungeon.player.hand.addToTop(card);
            }
            savedCards.clear();
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher("com.megacrit.cardcrawl.screens.select.HandCardSelectScreen", "selectedCards");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }
    }
}

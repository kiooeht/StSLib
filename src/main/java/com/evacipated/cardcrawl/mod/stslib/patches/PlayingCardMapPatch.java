package com.evacipated.cardcrawl.mod.stslib.patches;

import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.CardGroup;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(
        cls="com.megacrit.cardcrawl.cards.CardGroup",
        method="initializeDeck"
)
public class PlayingCardMapPatch
{
    @SpireInsertPatch(
            localvars={"copy"}
    )
    public static void Insert(CardGroup __instance, CardGroup masterDeck, CardGroup copy)
    {
        assert copy.size() == masterDeck.size();

        for (int i=0; i<copy.size(); ++i) {
            StSLib.mapPlayingCardToMasterDeck(copy.group.get(i), masterDeck.group.get(i));
        }
    }

    public static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher("com.megacrit.cardcrawl.cards.CardGroup", "shuffle");

            return LineFinder.findInOrder(ctBehavior, new ArrayList<>(), finalMatcher);
        }
    }
}

package com.evacipated.cardcrawl.mod.stslib.patches.relicInterfaces;

import com.evacipated.cardcrawl.mod.stslib.relics.OnSkipCardRelic;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.SingingBowl;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import com.megacrit.cardcrawl.ui.buttons.SingingBowlButton;
import javassist.CtBehavior;

public class OnSkipCardRelicPatch
{
    @SpirePatch(
            clz=SingingBowlButton.class,
            method="onClick"
    )
    public static class SingingBowlSkipPatch
    {
        public static void Prefix(SingingBowlButton __instance)
        {
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (r instanceof OnSkipCardRelic) {
                    if (AbstractDungeon.player.hasRelic(SingingBowl.ID)) {
                        ((OnSkipCardRelic)r).onSkipSingingBowl();
                    }
                }
            }
        }
    }

    @SpirePatch(
            clz=ProceedButton.class,
            method="update"
    )
    public static class OnSkipCardPatch
    {
        @SpireInsertPatch(
                locator=Locator.class
        )
        public static void Insert(ProceedButton __instance)
        {
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (r instanceof OnSkipCardRelic) {
                    ((OnSkipCardRelic)r).onSkipCard();
                }
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(RewardItem.class, "recordCardSkipMetrics");
                return LineFinder.findAllInOrder(ctBehavior, finalMatcher);
            }
        }
    }
}

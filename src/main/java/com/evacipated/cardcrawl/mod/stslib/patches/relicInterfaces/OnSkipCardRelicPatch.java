package com.evacipated.cardcrawl.mod.stslib.patches.relicInterfaces;

import com.evacipated.cardcrawl.mod.stslib.relics.OnSkipCardRelic;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.SingingBowl;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import com.megacrit.cardcrawl.ui.buttons.SingingBowlButton;

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
                rloc=102
        )
        public static void Insert(ProceedButton __instance)
        {
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (r instanceof OnSkipCardRelic) {
                    ((OnSkipCardRelic)r).onSkipCard();
                }
            }
        }
    }
}

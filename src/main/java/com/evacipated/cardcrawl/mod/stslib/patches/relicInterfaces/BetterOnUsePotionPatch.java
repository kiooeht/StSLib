package com.evacipated.cardcrawl.mod.stslib.patches.relicInterfaces;

import com.evacipated.cardcrawl.mod.stslib.relics.BetterOnUsePotionRelic;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.panels.PotionPopUp;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import javassist.CtBehavior;

public class BetterOnUsePotionPatch
{
    @SpirePatch(
            clz= com.megacrit.cardcrawl.potions.FairyPotion.class,
            method="onPlayerDeath"
    )
    public static class FairyPotion
    {
        @SpireInsertPatch(
                locator=Locator.class
        )
        public static void Insert(com.megacrit.cardcrawl.potions.FairyPotion __instance)
        {
            Do(__instance);
        }
    }

    @SpirePatch(
            clz=PotionPopUp.class,
            method="updateInput"
    )
    @SpirePatch(
            clz=PotionPopUp.class,
            method="updateTargetMode"
    )
    public static class NormalPotions
    {
        @SpireInsertPatch(
                locator=Locator.class,
                localvars={"potion"}
        )
        public static void Insert(PotionPopUp __instance, AbstractPotion potion)
        {
            Do(potion);
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(TopPanel.class, "destroyPotion");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }

    private static void Do(AbstractPotion potion)
    {
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic instanceof BetterOnUsePotionRelic) {
                ((BetterOnUsePotionRelic)relic).betterOnUsePotion(potion);
            }
        }
    }
}

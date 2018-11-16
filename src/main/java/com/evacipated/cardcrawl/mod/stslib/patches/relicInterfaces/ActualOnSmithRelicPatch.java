package com.evacipated.cardcrawl.mod.stslib.patches.relicInterfaces;

import com.evacipated.cardcrawl.mod.stslib.relics.ActualOnSmithRelic;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.campfire.CampfireSmithEffect;
import javassist.CtBehavior;

@SpirePatch(
        clz=CampfireSmithEffect.class,
        method="update"
)
public class ActualOnSmithRelicPatch
{
    @SpireInsertPatch(
            locator=Locator.class
    )
    public static void Insert(CampfireSmithEffect __instance)
    {
        for (AbstractRelic r : AbstractDungeon.player.relics) {
            if (r instanceof ActualOnSmithRelic) {
                ((ActualOnSmithRelic)r).actualOnSmith();
            }
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "bottledCardUpgradeCheck");
            return LineFinder.findAllInOrder(ctBehavior, finalMatcher);
        }
    }
}

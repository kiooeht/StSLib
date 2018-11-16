package com.evacipated.cardcrawl.mod.stslib.patches.relicInterfaces;

import com.evacipated.cardcrawl.mod.stslib.relics.BetterOnSmithRelic;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.campfire.CampfireSmithEffect;
import javassist.CtBehavior;

@SpirePatch(
        clz=CampfireSmithEffect.class,
        method="update"
)
public class BetterOnSmithRelicPatch
{
    @SpireInsertPatch(
            locator=Locator.class,
            localvars={"c"}
    )
    public static void Insert(CampfireSmithEffect __instance, AbstractCard c)
    {
        for (AbstractRelic r : AbstractDungeon.player.relics) {
            if (r instanceof BetterOnSmithRelic) {
                ((BetterOnSmithRelic)r).betterOnSmith(c);
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

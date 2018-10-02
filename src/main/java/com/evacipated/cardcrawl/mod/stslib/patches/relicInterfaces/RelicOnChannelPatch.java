package com.evacipated.cardcrawl.mod.stslib.patches.relicInterfaces;

import com.evacipated.cardcrawl.mod.stslib.relics.OnChannelRelic;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CtBehavior;

@SpirePatch(
        clz=AbstractPlayer.class,
        method="channelOrb"
)
public class RelicOnChannelPatch
{
    @SpireInsertPatch(
            locator=Locator.class
    )
    public static void Insert(AbstractPlayer __intance, AbstractOrb orbToSet)
    {
        for (AbstractRelic relic : __intance.relics) {
            if (relic instanceof OnChannelRelic) {
                ((OnChannelRelic)relic).onChannel(orbToSet);
            }
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(GameActionManager.class, "orbsChanneledThisCombat");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}

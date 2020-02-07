package com.evacipated.cardcrawl.mod.stslib.patches.relicInterfaces;

import com.evacipated.cardcrawl.mod.stslib.relics.OnApplyPowerRelic;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CtBehavior;

@SpirePatch(clz = ApplyPowerAction.class, method = "update")
public class OnApplyPowerRelicPatch {
    @SpireInsertPatch(locator = Locator.class)
    public static void Insert(ApplyPowerAction __instance, AbstractPower ___powerToApply) {
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic instanceof OnApplyPowerRelic) {
                ((OnApplyPowerRelic) relic).onApplyPower(__instance.source, __instance.target, ___powerToApply);
            }
        }
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasRelic");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}

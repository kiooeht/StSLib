package com.evacipated.cardcrawl.mod.stslib.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.PowerDebuffEffect;
import javassist.CtBehavior;

public class NeutralPowertypePatch {
    @SpireEnum
    public static AbstractPower.PowerType NEUTRAL;

    @SpirePatch(clz = ApplyPowerAction.class, method = "update")
    public static class NoApplicationEffect {
        @SpireInsertPatch(locator = Locator.class)
        public static void patch(ApplyPowerAction __instance, AbstractPower ___powerToApply) {
            if(___powerToApply.type == NEUTRAL) {
                for (int i = AbstractDungeon.effectList.size() - 1; i > -1 ; i--) {
                    if(AbstractDungeon.effectList.get(i) instanceof PowerDebuffEffect) {
                        AbstractDungeon.effectList.remove(i);
                        break;
                    }
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "onModifyPower");
                return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}

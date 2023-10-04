package com.evacipated.cardcrawl.mod.stslib.patches.tempHp;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CtBehavior;

@SpirePatch(
        clz=AbstractMonster.class,
        method="damage"
)
public class MonsterDamage
{
    @SpireInsertPatch(
            locator=Locator.class,
            localvars={"damageAmount", "hadBlock"}
    )
    public static void Insert(AbstractMonster __instance, DamageInfo info, @ByRef int[] damageAmount, @ByRef boolean[] hadBlock)
    {
        PlayerDamage.Insert(__instance, info, damageAmount, hadBlock);
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractMonster.class, "decrementBlock");
            return offset(LineFinder.findInOrder(ctMethodToPatch, finalMatcher));
        }

        private static int[] offset(int[] originalArr) {
            for (int i = 0; i < originalArr.length; i++) {
                originalArr[i] += 1;
            }
            return originalArr;
        }
    }

    @SpireInsertPatch(
            locator=Locator2.class,
            localvars={"damageAmount", "hadBlock"}
    )
    public static void Insert2(AbstractMonster __instance, DamageInfo info, @ByRef int[] damageAmount, @ByRef boolean[] hadBlock)
    {
        PlayerDamage.Insert2(__instance, info, damageAmount, hadBlock);
    }

    private static class Locator2 extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractRelic.class, "onAttack");
            return offset(LineFinder.findInOrder(ctMethodToPatch, finalMatcher));
        }

        private static int[] offset(int[] originalArr) {
            for (int i = 0; i < originalArr.length; i++) {
                originalArr[i] += 3;
            }
            return originalArr;
        }
    }
}

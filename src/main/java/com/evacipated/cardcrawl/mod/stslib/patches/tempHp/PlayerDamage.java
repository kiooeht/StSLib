package com.evacipated.cardcrawl.mod.stslib.patches.tempHp;

import com.evacipated.cardcrawl.mod.stslib.patches.core.AbstractCreature.TempHPField;
import com.evacipated.cardcrawl.mod.stslib.vfx.combat.TempDamageNumberEffect;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(
        cls="com.megacrit.cardcrawl.characters.AbstractPlayer",
        method="damage"
)
public class PlayerDamage
{
    @SpireInsertPatch(
            localvars={"damageAmount"}
    )
    public static void Insert(AbstractPlayer __instance, DamageInfo info, @ByRef int[] damageAmount)
    {
        if (damageAmount[0] <= 0) {
            return;
        }

        int temporaryHealth = TempHPField.tempHp.get(__instance);
        if (temporaryHealth > 0) {
            if (temporaryHealth >= damageAmount[0]) {
                temporaryHealth -= damageAmount[0];
                AbstractDungeon.effectsQueue.add(new TempDamageNumberEffect(__instance, __instance.hb.cX, __instance.hb.cY, damageAmount[0]));
                //AbstractDungeon.effectList.add(new BlockedWordEffect(__instance, __instance.hb.cX, __instance.hb.cY, "Absorbed"));
                damageAmount[0] = 0;
            } else {
                damageAmount[0] -= temporaryHealth;
                AbstractDungeon.effectsQueue.add(new TempDamageNumberEffect(__instance, __instance.hb.cX, __instance.hb.cY, temporaryHealth));
                temporaryHealth = 0;
            }

            TempHPField.tempHp.set(__instance, temporaryHealth);
        }

        System.out.println("Final damage: " + damageAmount[0]);
    }

    public static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher("com.megacrit.cardcrawl.characters.AbstractPlayer", "decrementBlock");
            return offset(LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher), 1);
        }

        private static int[] offset(int[] originalArr, int offset) {
            for (int i = 0; i < originalArr.length; i++) {
                originalArr[i] += offset;
            }
            return originalArr;
        }
    }
}

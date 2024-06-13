package com.evacipated.cardcrawl.mod.stslib.patches.tempHp;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.evacipated.cardcrawl.mod.stslib.patches.core.AbstractCreature.TempHPField;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnLoseTempHpPower;
import com.evacipated.cardcrawl.mod.stslib.relics.OnLoseTempHpRelic;
import com.evacipated.cardcrawl.mod.stslib.vfx.combat.TempDamageNumberEffect;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.combat.DamageImpactLineEffect;
import com.megacrit.cardcrawl.vfx.combat.StrikeEffect;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(
        clz=AbstractPlayer.class,
        method="damage"
)
public class PlayerDamage
{
    static boolean hadTempHP;
    static boolean keepCheckingTempHp;

    @SpireInsertPatch(
            locator=Locator.class,
            localvars={"damageAmount", "hadBlock"}
    )
    public static void Insert(AbstractCreature __instance, DamageInfo info, @ByRef int[] damageAmount, @ByRef boolean[] hadBlock)
    {
        hadTempHP = false;
        keepCheckingTempHp = damageAmount[0] > 0;
        if (damageAmount[0] <= 0) {
            return;
        }

        for (AbstractDamageModifier mod : DamageModifierManager.getDamageMods(info)) {
            if (mod.ignoresTempHP(__instance)) {
                keepCheckingTempHp = false;
                return;
            }
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "decrementBlock");
            return offset(LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher));
        }

        private static int[] offset(int[] originalArr) {
            for (int i = 0; i < originalArr.length; i++) {
                originalArr[i] += 1;
            }
            return originalArr;
        }
    }

    @SpireInsertPatch(
            locator= Locator2.class,
            localvars={"damageAmount", "hadBlock"}
    )
    public static void Insert2(AbstractCreature __instance, DamageInfo info, @ByRef int[] damageAmount, @ByRef boolean[] hadBlock)
    {
        keepCheckingTempHp = keepCheckingTempHp && damageAmount[0] > 0;
        if (keepCheckingTempHp) {
            int temporaryHealth = TempHPField.tempHp.get(__instance);
            if (temporaryHealth > 0) {
                for (AbstractPower power : __instance.powers) {
                    if (power instanceof OnLoseTempHpPower) {
                        damageAmount[0] = ((OnLoseTempHpPower) power).onLoseTempHp(info, damageAmount[0]);
                    }
                }
                if (__instance instanceof AbstractPlayer) {
                    for (AbstractRelic relic : ((AbstractPlayer) __instance).relics) {
                        if (relic instanceof OnLoseTempHpRelic) {
                            damageAmount[0] = ((OnLoseTempHpRelic) relic).onLoseTempHp(info, damageAmount[0]);
                        }
                    }
                }

                hadTempHP = true;
                for (int i = 0; i < 18; ++i) {
                    AbstractDungeon.effectsQueue.add(new DamageImpactLineEffect(__instance.hb.cX, __instance.hb.cY));
                }
                CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, false);
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
        }
    }

    private static class Locator2 extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractRelic.class, "onLoseHpLast");
            return offset(LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher));
        }

        private static int[] offset(int[] originalArr) {
            for (int i = 0; i < originalArr.length; i++) {
                originalArr[i] += 3;
            }
            return originalArr;
        }
    }

    // Disables strike effect
    @SpireInsertPatch(
            locator=StrikeEffectLocator.class
    )
    public static SpireReturn<Void> Insert(AbstractCreature __instance, DamageInfo info)
    {
        if (hadTempHP) {
            return SpireReturn.Return(null);
        } else {
            return SpireReturn.Continue();
        }
    }

    private static class StrikeEffectLocator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.NewExprMatcher(StrikeEffect.class);
            int[] all = LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            return new int[] {all[all.length - 1]};
        }
    }
}

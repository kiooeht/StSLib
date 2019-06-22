package com.evacipated.cardcrawl.mod.stslib.patches.tempHp;

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
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(
        clz=AbstractPlayer.class,
        method="damage"
)
public class PlayerDamage
{
    @SpireInsertPatch(
            locator=Locator.class,
            localvars={"damageAmount", "hadBlock"}
    )
    public static void Insert(AbstractCreature __instance, DamageInfo info, @ByRef int[] damageAmount, @ByRef boolean[] hadBlock)
    {
        if (damageAmount[0] <= 0) {
            return;
        }

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

            hadBlock[0] = true;
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

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "decrementBlock");
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

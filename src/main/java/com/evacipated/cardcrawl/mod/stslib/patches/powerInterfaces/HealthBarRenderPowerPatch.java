package com.evacipated.cardcrawl.mod.stslib.patches.powerInterfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.HealthBarRenderPower;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.IntangiblePower;
import com.megacrit.cardcrawl.powers.PoisonPower;
import javassist.CtBehavior;

import java.lang.reflect.Field;

public class HealthBarRenderPowerPatch
{
    private static int allAmtSum = 0;
    private static float nonPoisonWidthSum = 0;

    @SpirePatch(
            clz= AbstractCreature.class,
            method="renderHealth"
    )
    public static class RenderPowerHealthBar
    {
        @SpireInsertPatch(
                locator=Locator.class,
                localvars={"x", "y", "targetHealthBarWidth", "HEALTH_BAR_HEIGHT", "HEALTH_BAR_OFFSET_Y"}
        )
        public static void Insert(AbstractCreature __instance, SpriteBatch sb, float x, float y,
                                  float targetHealthBarWidth, float HEALTH_BAR_HEIGHT, float HEALTH_BAR_OFFSET_Y)
        {
            int poisonAmt = 0;
            if (__instance.hasPower(PoisonPower.POWER_ID)) {
                poisonAmt = __instance.getPower(PoisonPower.POWER_ID).amount;
                if (poisonAmt > 0 && __instance.hasPower(IntangiblePower.POWER_ID)) {
                    poisonAmt = 1;
                }
            }

            int prevPowerAmtSum = poisonAmt;

            for (AbstractPower power : __instance.powers) {
                if (power instanceof HealthBarRenderPower) {
                    sb.setColor(((HealthBarRenderPower) power).getColor());

                    int amt = ((HealthBarRenderPower) power).getHealthBarAmount();
                    if (amt > 0 && __instance.hasPower(IntangiblePower.POWER_ID)) {
                        amt = 1;
                    }

                    if (__instance.currentHealth > prevPowerAmtSum) {
                        float w = 1.0f - (__instance.currentHealth - prevPowerAmtSum) / (float) __instance.currentHealth;
                        w *= targetHealthBarWidth;
                        if (__instance.currentHealth > 0) {
                            sb.draw(ImageMaster.HEALTH_BAR_L, x - HEALTH_BAR_HEIGHT, y + HEALTH_BAR_OFFSET_Y, HEALTH_BAR_HEIGHT, HEALTH_BAR_HEIGHT);
                        }
                        sb.draw(ImageMaster.HEALTH_BAR_B, x, y + HEALTH_BAR_OFFSET_Y, targetHealthBarWidth - w, HEALTH_BAR_HEIGHT);
                        sb.draw(ImageMaster.HEALTH_BAR_R, x + targetHealthBarWidth - w, y + HEALTH_BAR_OFFSET_Y, HEALTH_BAR_HEIGHT, HEALTH_BAR_HEIGHT);
                    }

                    prevPowerAmtSum += amt;
                }
            }

            allAmtSum = prevPowerAmtSum;
            prevPowerAmtSum -= poisonAmt;
            nonPoisonWidthSum = 1.0f - (__instance.currentHealth - prevPowerAmtSum) / (float) __instance.currentHealth;
            nonPoisonWidthSum *= targetHealthBarWidth;
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCreature.class, "renderRedHealthBar");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz=AbstractCreature.class,
            method="renderRedHealthBar"
    )
    public static class FixRedHealthBar
    {
        @SpireInsertPatch(
                locator=Locator.class,
                localvars={"targetHealthBarWidth"}
        )
        public static void Insert(AbstractCreature __instance, SpriteBatch sb, float x, float y, @ByRef float[] targetHealthBarWidth)
        {
            targetHealthBarWidth[0] -= nonPoisonWidthSum;
        }

        public static void Postfix(AbstractCreature __instance, SpriteBatch sb, float x, float y)
        {
            try {
                Field f = AbstractCreature.class.getDeclaredField("targetHealthBarWidth");
                f.setAccessible(true);

                float targetHealthBarWidth = f.getFloat(__instance);
                targetHealthBarWidth += nonPoisonWidthSum;
                nonPoisonWidthSum = 0;
                f.setFloat(__instance, targetHealthBarWidth);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        //*
        public static SpireReturn<Void> Prefix(AbstractCreature __instance, SpriteBatch sb, float x, float y)
        {
            if (__instance.currentHealth <= allAmtSum) {
                nonPoisonWidthSum = 0;
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
        //*/

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(ImageMaster.class, "HEALTH_BAR_B");
                return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}

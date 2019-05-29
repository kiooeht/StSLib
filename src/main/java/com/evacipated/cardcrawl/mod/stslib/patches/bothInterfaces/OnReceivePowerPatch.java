package com.evacipated.cardcrawl.mod.stslib.patches.bothInterfaces;

import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.BetterOnApplyPowerPower;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.evacipated.cardcrawl.mod.stslib.relics.OnReceivePowerRelic;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CtBehavior;

public class OnReceivePowerPatch
{

    public static class OnReceivePowerPowerPatch {

        @SpirePatch(
                clz = ApplyPowerAction.class,
                method = "update"
        )
        public static class ChangeStack {

            @SpireInsertPatch(
                    locator = Locator.class,
                    localvars = {"p", "powerToApply"}
            )
            public static void Insert(ApplyPowerAction __instance, AbstractPower power, AbstractPower powerToApply) {
                // Allow changing the stackAmount. We are not using changeAmount(), that will be deleted.
                if (power instanceof OnReceivePowerPower) {
                    __instance.amount = ((OnReceivePowerPower) power).onReceivePowerStacks(powerToApply, __instance.target, __instance.source, __instance.amount);
                }
            }

            private static class Locator extends SpireInsertLocator {
                @Override
                public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                    // We are not using the onReceivingPowerStack() hook, that will be deleted.
                    // Warning: onReceivingPower() hook name will change soon.
                    Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPower.class, "onReceivingPower");
                    return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
                }
            }
        }

        // Warning: onReceivingPower() hook name will change soon.
        @SpirePatch(
                clz = AbstractPower.class,
                method = "onReceivingPower"
        )
        public static class NegatePower {

            public static boolean Postfix(AbstractPower __instance, AbstractPower power, AbstractCreature target, AbstractCreature source) {
                if (__instance instanceof OnReceivePowerPower) {
                    boolean apply = ((OnReceivePowerPower) __instance).onReceivePower(power, target, source);
                    if (apply) {
                        return true;
                    }
                    else {
                        return false;
                    }
                }
                else {
                    return true;
                }
            }
        }
    }

    public static class OnReceivePowerRelicPatch {

        @SpirePatch(
                clz = ApplyPowerAction.class,
                method = "update"
        )
        public static class ChangeStack {

            @SpireInsertPatch(
                    locator = Locator.class,
                    localvars = {"r", "powerToApply"}
            )
            public static void Insert(ApplyPowerAction __instance, AbstractRelic relic, AbstractPower powerToApply) {
                // Allow changing the stackAmount. We are not using changeAmount(), that will be deleted.
                if (relic instanceof OnReceivePowerRelic) {
                    __instance.amount = ((OnReceivePowerRelic) relic).onReceivePowerStacks(powerToApply, __instance.source, __instance.amount);
                }
            }

            private static class Locator extends SpireInsertLocator {
                @Override
                public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                    // We are not using the onReceivingPowerStack() hook, that will be deleted.
                    // Warning: onReceivingPower() hook name will change soon.
                    Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractRelic.class, "onReceivingPower");
                    return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
                }
            }
        }

        // Warning: onReceivingPower() hook name will change soon.
        @SpirePatch(
                clz = AbstractRelic.class,
                method = "onReceivingPower"
        )
        public static class NegatePower {

            public static boolean Postfix(AbstractRelic __instance, AbstractPower power, AbstractCreature target, AbstractCreature source) {
                if (__instance instanceof OnReceivePowerRelic) {
                    boolean apply = ((OnReceivePowerRelic) __instance).onReceivePower(power, source);
                    if (apply) {
                        return true;
                    }
                    else {
                        return false;
                    }
                }
                else {
                    return true;
                }
            }
        }
    }

    // We are not using the existing version in the base game, the related hooks will be deleted soon.
    @SpirePatch(
            clz=ApplyPowerAction.class,
            method="update"
    )
    public static class OnReceivePowerBetterOnApplyPower {

        static SpireReturn<Void> CheckPower(AbstractGameAction action, AbstractCreature target, AbstractCreature source, float[] duration, AbstractPower powerToApply)
        {
            if (source != null) {
                for (AbstractPower power : source.powers) {
                    if (power instanceof BetterOnApplyPowerPower) {
                        // Allows changing the stackAmount
                        action.amount = ((BetterOnApplyPowerPower) power).betterOnApplyPowerStacks(powerToApply, target, source, action.amount);
                        // Allows negating the power
                        boolean apply = ((BetterOnApplyPowerPower) power).betterOnApplyPower(powerToApply, target, source);
                        if (!apply) {
                            AbstractDungeon.actionManager.addToTop(new TextAboveCreatureAction(target, ApplyPowerAction.TEXT[0]));
                            duration[0] -= Gdx.graphics.getDeltaTime();
                            CardCrawlGame.sound.play("NULLIFY_SFX");
                            return SpireReturn.Return(null);
                        }
                    }
                }
            }
            return SpireReturn.Continue();
        }

        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"duration", "powerToApply"}
        )
        public static SpireReturn<Void> Insert(ApplyPowerAction __instance, @ByRef float[] duration, AbstractPower powerToApply)
        {
            return CheckPower(__instance, __instance.target, __instance.source, duration, powerToApply);
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                // Warning: This will definitely need a new matcher soon.
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCreature.class, "powers");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}

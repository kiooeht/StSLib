package com.evacipated.cardcrawl.mod.stslib.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.DamageNumberEffect;
import javassist.CtBehavior;

public class ColoredDamagePatch {
    public static Color currentColor = null;
    public static FadeSpeed currentSpeed = FadeSpeed.NONE;
    public static boolean rainbow = false;
    public static AbstractGameAction action = null;

    public enum FadeSpeed {
        NONE,
        SLOW,
        SLOWISH,
        MODERATE,
        FAST
    }

    public static float enumToSpeed(FadeSpeed speed) {
        if (speed == FadeSpeed.NONE)
            return 0f;
        if (speed == FadeSpeed.SLOW)
            return 1f;
        if (speed == FadeSpeed.SLOWISH)
            return 1.5f;
        if (speed == FadeSpeed.MODERATE)
            return 2f;
        if (speed == FadeSpeed.FAST)
            return 4f;
        return 0f;
    }

    @SpirePatch2 (
            clz = AbstractGameAction.class,
            method = SpirePatch.CLASS
    )
    public static class DamageActionColorField {
        public static SpireField<Color> damageColor = new SpireField<>(() -> null);
        public static SpireField<FadeSpeed> fadeSpeed = new SpireField<>(() -> null);
        public static SpireField<Boolean> rainbow = new SpireField<>(() -> false);
        public static SpireField<AbstractGameAction> action = new SpireField<>(() -> null);
    }

    @SpirePatch2( clz = DamageAction.class, method = "update" )
    @SpirePatch2( clz = LoseHPAction.class, method = "update" )
    @SpirePatch2( clz = DarkOrbEvokeAction.class, method = "update" )
    @SpirePatch2( clz = PummelDamageAction.class, method = "update" )
    @SpirePatch2( clz = InstantKillAction.class, method = "update" )
    public static class AbstractCreatureUpdate {
        @SpireInsertPatch (
                locator = Locator.class
        )
        public static void Insert(AbstractGameAction __instance) {
            ColoredDamagePatch.currentColor = DamageActionColorField.damageColor.get(__instance);
            ColoredDamagePatch.currentSpeed = DamageActionColorField.fadeSpeed.get(__instance);
            ColoredDamagePatch.rainbow = DamageActionColorField.rainbow.get(__instance);
            DamageActionColorField.action.get(__instance);
        }
        private static class Locator extends SpireInsertLocator {
            private Locator() {}

            @Override
            public int[] Locate(CtBehavior behavior) throws Exception {
                Matcher matcher = new Matcher.MethodCallMatcher(AbstractCreature.class, "damage");
                return LineFinder.findAllInOrder(behavior, matcher);
            }
        }
    }

    @SpirePatch2( clz = DamageAllEnemiesAction.class, method = "update" )
    public static class AbstractMonsterUpdate {
        @SpireInsertPatch (
                locator = Locator.class
        )
        public static void Insert(DamageAllEnemiesAction __instance) {
            ColoredDamagePatch.currentColor = DamageActionColorField.damageColor.get(__instance);
            ColoredDamagePatch.currentSpeed = DamageActionColorField.fadeSpeed.get(__instance);
            ColoredDamagePatch.rainbow = DamageActionColorField.rainbow.get(__instance);
            ColoredDamagePatch.action = DamageActionColorField.action.get(__instance);
        }
        private static class Locator extends SpireInsertLocator {
            private Locator() {}

            @Override
            public int[] Locate(CtBehavior behavior) throws Exception {
                Matcher matcher = new Matcher.MethodCallMatcher(AbstractMonster.class, "damage");
                return LineFinder.findAllInOrder(behavior, matcher);
            }
        }
    }

    @SpirePatch2(
            clz = DamageNumberEffect.class,
            method = SpirePatch.CLASS
    )
    public static class DamageNumberColorField {
        public static SpireField<Color> damageColor = new SpireField<>(() -> null);
        public static SpireField<FadeSpeed> fadeSpeed = new SpireField<>(() -> null);
        public static SpireField<Boolean> rainbow = new SpireField<>(() -> false);
        public static SpireField<Integer> timerOffset = new SpireField<>(() -> 0);
    }

    @SpirePatch2(
            clz = DamageNumberEffect.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class MakeColor {
        @SpirePostfixPatch
        public static void Postfix(DamageNumberEffect __instance) {
            if (currentColor != null && AbstractDungeon.actionManager.currentAction != action) {
                ReflectionHacks.setPrivate(__instance, AbstractGameEffect.class, "color", currentColor.cpy());
                DamageNumberColorField.damageColor.set(__instance, currentColor.cpy());
            }
            else {
                Color color = ReflectionHacks.getPrivate(__instance, AbstractGameEffect.class, "color");
                DamageNumberColorField.damageColor.set(__instance, color.cpy());
            }

            if (currentSpeed != null && AbstractDungeon.actionManager.currentAction != action)
                DamageNumberColorField.fadeSpeed.set(__instance, currentSpeed);
            else
                DamageNumberColorField.fadeSpeed.set(__instance, FadeSpeed.FAST);

            DamageNumberColorField.timerOffset.set(__instance, AbstractDungeon.miscRng.random(0, 5000));

            if (AbstractDungeon.actionManager.currentAction != action)
                DamageNumberColorField.rainbow.set(__instance, rainbow);
        }
    }

    @SpirePatch2(
            clz = DamageNumberEffect.class,
            method = "update"
    )
    public static class MakeColor2 {
        @SpirePostfixPatch
        public static void Postfix(DamageNumberEffect __instance) {
            Color color = DamageNumberColorField.damageColor.get(__instance);
            Color color2 = ReflectionHacks.getPrivate(__instance, AbstractGameEffect.class, "color");

            if (DamageNumberColorField.rainbow.get(__instance)) {
                int timerOffset = DamageNumberColorField.timerOffset.get(__instance);
                color.set(
                        (MathUtils.cosDeg((float) ((System.currentTimeMillis()*3 + timerOffset) / 10L % 360L)) + 1.25F) / 2.3F,
                        (MathUtils.cosDeg((float) ((System.currentTimeMillis()*3 + 1000L + timerOffset) / 10L % 360L)) + 1.25F) / 2.3F,
                        (MathUtils.cosDeg((float) ((System.currentTimeMillis()*3 + 2000L + timerOffset) / 10L % 360L)) + 1.25F) / 2.3F,
                        color2.a);

                color2.r = color.r;
                color2.g = color.g;
                color2.b = color.b;

                return;
            }

            FadeSpeed speed = DamageNumberColorField.fadeSpeed.get(__instance);
            float timeMult = enumToSpeed(speed);

            float delta = Gdx.graphics.getDeltaTime() * timeMult;
            if (speed != FadeSpeed.NONE) {
                if (color.r < 1.0F)
                    color.r += delta;
                if (color.g < 1.0F)
                    color.g += delta;
                if (color.b < 1.0F)
                    color.b += delta;
                color.clamp();
            }

            // Don't copy completely, don't overwrite alpha
            color2.r = color.r;
            color2.g = color.g;
            color2.b = color.b;
        }
    }
}

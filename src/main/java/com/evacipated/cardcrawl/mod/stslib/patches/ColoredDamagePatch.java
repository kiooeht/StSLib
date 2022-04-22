package com.evacipated.cardcrawl.mod.stslib.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.DamageNumberEffect;

public class ColoredDamagePatch {
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
            AbstractGameAction action = AbstractDungeon.actionManager.currentAction;
            if (action == null)
                return;
            Color actionColor = DamageActionColorField.damageColor.get(action);
            FadeSpeed actionSpeed = DamageActionColorField.fadeSpeed.get(action);
            boolean actionRainbow = DamageActionColorField.rainbow.get(action);
            if (actionColor != null) {
                ReflectionHacks.setPrivate(__instance, AbstractGameEffect.class, "color", actionColor.cpy());
                DamageNumberColorField.damageColor.set(__instance, actionColor.cpy());
            }
            else {
                Color color = ReflectionHacks.getPrivate(__instance, AbstractGameEffect.class, "color");
                DamageNumberColorField.damageColor.set(__instance, color.cpy());
            }

            if (actionSpeed != null)
                DamageNumberColorField.fadeSpeed.set(__instance, actionSpeed);
            else
                DamageNumberColorField.fadeSpeed.set(__instance, FadeSpeed.FAST);

            DamageNumberColorField.timerOffset.set(__instance, AbstractDungeon.miscRng.random(0, 5000));
            DamageNumberColorField.rainbow.set(__instance, actionRainbow);
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
            FadeSpeed speed = DamageNumberColorField.fadeSpeed.get(__instance);

            if (color == null || speed == null)
                return;

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

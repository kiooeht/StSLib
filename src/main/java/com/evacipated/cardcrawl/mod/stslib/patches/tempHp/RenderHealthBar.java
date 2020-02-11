package com.evacipated.cardcrawl.mod.stslib.patches.tempHp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.patches.core.AbstractCreature.TempHPField;
import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import javassist.CtBehavior;

import java.lang.reflect.Field;
import java.util.ArrayList;

@SpirePatch(
        clz=AbstractCreature.class,
        method="renderHealth"
)
public class RenderHealthBar
{
    private static float HEALTH_BAR_HEIGHT = -1;
    private static float HEALTH_BAR_OFFSET_Y = -1;

    @SpireInsertPatch(
            locator=Locator.class,
            localvars={"x", "y"}
    )
    public static void Insert(AbstractCreature __instance, SpriteBatch sb, float x, float y)
    {
        if (HEALTH_BAR_HEIGHT == -1) {
            HEALTH_BAR_HEIGHT = 20.0f * Settings.scale;
            HEALTH_BAR_OFFSET_Y = -28.0f * Settings.scale;
        }

        if (!Gdx.input.isKeyPressed(Input.Keys.H)) {
            if (TempHPField.tempHp.get(__instance) > 0 && __instance.hbAlpha > 0) {
                renderTempHPIconAndValue(__instance, sb, x, y);
            }
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception
        {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCreature.class, "currentBlock");
            ArrayList<Matcher> matchers = new ArrayList<>();
            matchers.add(finalMatcher);
            return LineFinder.findInOrder(ctBehavior, matchers, finalMatcher);
        }
    }

    private static <O, T> T getPrivate(Class<O> cls, Object obj, String varName, Class<T> type)
    {
        try {
            Field f = cls.getDeclaredField(varName);
            f.setAccessible(true);
            return type.cast(f.get(obj));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static <O, T> T getPrivate(Class<O> obj, String varName, Class<T> type)
    {
        return getPrivate(obj, null, varName, type);
    }

    private static void renderTempHPIconAndValue(AbstractCreature creature, SpriteBatch sb, float x, float y)
    {
        sb.setColor(Settings.GOLD_COLOR);
        sb.draw(StSLib.TEMP_HP_ICON,
                x + getPrivate(AbstractCreature.class, "BLOCK_ICON_X", Float.class) - 16.0f + creature.hb.width,
                y + getPrivate(AbstractCreature.class, "BLOCK_ICON_Y", Float.class) - 32.0f,
                32.0f, 32.0f, 64.0f, 64.0f, Settings.scale, Settings.scale,
                0.0f, 0, 0, 64, 64,
                false, false);
        FontHelper.renderFontCentered(sb, FontHelper.blockInfoFont,
                Integer.toString(TempHPField.tempHp.get(creature)),
                x + getPrivate(AbstractCreature.class, "BLOCK_ICON_X", Float.class) + 16.0f + creature.hb.width,
                y - 16.0f * Settings.scale,
                Settings.CREAM_COLOR,
                1.0f);
    }
}

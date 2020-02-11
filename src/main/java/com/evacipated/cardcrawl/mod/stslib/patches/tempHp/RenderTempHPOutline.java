package com.evacipated.cardcrawl.mod.stslib.patches.tempHp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.patches.core.AbstractCreature.TempHPField;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(
        clz=AbstractCreature.class,
        method="renderHealth"
)
public class RenderTempHPOutline
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
                renderTempHPOutline(__instance, sb, x, y);
            }
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception
        {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCreature.class, "currentBlock");
            return LineFinder.findInOrder(ctBehavior, finalMatcher);
        }
    }

    private static void renderTempHPOutline(AbstractCreature creature, SpriteBatch sb, float x, float y)
    {
        sb.setColor(Settings.GOLD_COLOR);
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

        sb.draw(ImageMaster.BLOCK_BAR_L, x - HEALTH_BAR_HEIGHT, y + HEALTH_BAR_OFFSET_Y, HEALTH_BAR_HEIGHT, HEALTH_BAR_HEIGHT);

        sb.draw(ImageMaster.BLOCK_BAR_B, x, y + HEALTH_BAR_OFFSET_Y, creature.hb.width, HEALTH_BAR_HEIGHT);

        sb.draw(ImageMaster.BLOCK_BAR_R, x + creature.hb.width, y + HEALTH_BAR_OFFSET_Y, HEALTH_BAR_HEIGHT, HEALTH_BAR_HEIGHT);
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
}

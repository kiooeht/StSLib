package com.evacipated.cardcrawl.mod.stslib.vfx.combat;

import basemod.ClickableUIElement;
import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelicWithUI;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class FlashClickRelicEffect extends AbstractGameEffect {
    private Texture img;
    private TextureAtlas.AtlasRegion region;
    private float scale;
    private ClickableRelicWithUI relic;
    private static final float DURATION = 2.5f;

    public FlashClickRelicEffect(ClickableRelicWithUI relic) {
        img = ReflectionHacks.getPrivate(relic.getElement(), ClickableUIElement.class, "image");
        region = ReflectionHacks.getPrivate(relic.getElement(), ClickableUIElement.class, "region");

        startingDuration = DURATION;
        duration = startingDuration;
        renderBehind = false;

        this.relic = relic;
        scale = Settings.scale;
        color = new Color(1.0F, 1.0F, 1.0F, 0.5F);
    }

    @Override
    public void update() {
        if (duration == startingDuration)
            relic.flash();
        duration -= Gdx.graphics.getDeltaTime();
        scale = Interpolation.exp5Out.apply(5.0F * Settings.scale, Settings.scale, (startingDuration - duration) / 2.5F);

        if (duration < 0.0F) {
            isDone = true;
            color.a = 0.0F;
        }
    }

    public void render(SpriteBatch sb) {
        float x = ReflectionHacks.getPrivate(relic.getElement(), ClickableUIElement.class, "x");
        float y = ReflectionHacks.getPrivate(relic.getElement(), ClickableUIElement.class, "y");
        sb.setColor(color);
        sb.setBlendFunction(770, 1);
        if (region != null) {
            sb.draw(region, x - (float)region.packedWidth / 2.0F, y - (float)region.packedHeight / 2.0F, 
                    (float)region.packedWidth / 2.0F, (float)region.packedHeight / 2.0F, 
                    (float)region.packedWidth, (float)region.packedHeight, scale, scale, 0.0F);
        }
        else if (img != null) {
            float halfWidth = img.getWidth()/2.0f;
            float halfHeight = img.getHeight()/2.0f;
            sb.draw(img, x - halfWidth + halfWidth*Settings.scale, y - halfHeight + halfHeight*Settings.scale,
                    halfWidth, halfHeight, (float) img.getWidth(), (float) img.getHeight(),
                    scale, scale, 0, 0, 0,
                    img.getWidth(), img.getHeight(), false, false);
        }

        sb.setBlendFunction(770, 771);
    }

    public void dispose() {
    }
}

package com.evacipated.cardcrawl.mod.stslib.icons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

public abstract class AbstractCustomIcon {
    public static final int RENDER_CONSTANT = 24;
    public static final String CODE_ENDING = "Icon]";
    public final String name;
    public final AtlasRegion region;

    public AbstractCustomIcon(String name, AtlasRegion region) {
        this.name = name;
        this.region = region;
    }

    public AbstractCustomIcon(String name, Texture texture) {
        this(name, new AtlasRegion(texture, 0, 0, texture.getWidth(), texture.getHeight()));
    }

    public final String cardCode() {
        return "[" + name + CODE_ENDING;
    }

    public int getImgSize() {
        return Math.max(region.getRegionWidth(), region.getRegionHeight());
    }

    public float getRenderScale() {
        return (float) RENDER_CONSTANT / getImgSize();
    }

    public void render(SpriteBatch sb, float drawX, float drawY, float offsetX, float offsetY, float scale, float angle) {
        Color backup = sb.getColor();
        sb.setColor(new Color(1.0F, 1.0F, 1.0F, backup.a));
        sb.draw(region, drawX + offsetX + region.offsetX - (float) region.originalWidth / 2.0F, drawY + offsetY + region.offsetY - (float) region.originalHeight / 2.0F,
                (float) region.originalWidth / 2.0F - region.offsetX - offsetX, (float) region.originalHeight / 2.0F - region.offsetY - offsetY,
                (float) region.packedWidth, (float) region.packedHeight,
                scale * getRenderScale(), scale * getRenderScale(), angle);
        sb.setColor(backup);
    }
}

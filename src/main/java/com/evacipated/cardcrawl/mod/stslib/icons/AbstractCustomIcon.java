package com.evacipated.cardcrawl.mod.stslib.icons;

import basemod.helpers.TooltipInfo;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.ShrinkLongDescription;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;

import java.util.ArrayList;
import java.util.List;

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

    public float getCardRenderScale(AbstractCard card) {
        return getRenderScale() * ShrinkLongDescription.Scale.descriptionScale.get(card);
    }

    public List<TooltipInfo> getCustomTooltips() {
        return new ArrayList<>();
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

    public void renderOnCard(SpriteBatch sb, AbstractCard card, float x, float y) {
        x /= getRenderScale();
        y /= getRenderScale();
        Color backup = sb.getColor();
        sb.setColor(new Color(1.0F, 1.0F, 1.0F, backup.a));
        sb.draw(region.getTexture(), card.current_x + x + region.offsetX, card.current_y + y + region.offsetY,
                -x - region.offsetX, -y - region.offsetY,
                (float)region.packedWidth, (float)region.packedHeight,
                card.drawScale * Settings.scale * getRenderScale() * ShrinkLongDescription.Scale.descriptionScale.get(card), card.drawScale * Settings.scale * getRenderScale() * ShrinkLongDescription.Scale.descriptionScale.get(card),
                card.angle, region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight(), false, false);
        sb.setColor(backup);

    }

    public void renderOnSCV(SpriteBatch sb, AbstractCard card, float x, float y, float current_x, float current_y, float scale) {
        x /= getRenderScale();
        y /= getRenderScale();
        Color backup = sb.getColor();
        sb.setColor(new Color(1.0F, 1.0F, 1.0F, backup.a));
        sb.draw(region.getTexture(), current_x + x + region.offsetX, current_y + y + region.offsetY,
                -x - region.offsetX, -y - region.offsetY,
                (float)region.packedWidth, (float)region.packedHeight,
                scale * Settings.scale * getRenderScale() * ShrinkLongDescription.Scale.descriptionScale.get(card), scale * Settings.scale * getRenderScale() * ShrinkLongDescription.Scale.descriptionScale.get(card),
                card.angle, region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight(), false, false);
        sb.setColor(backup);

    }
}

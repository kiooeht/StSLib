package com.evacipated.cardcrawl.mod.stslib.util.extraicons;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.evacipated.cardcrawl.mod.stslib.patches.ExtraIconsPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.Bash;

public class IconPayload {

    private static final BitmapFont DEFAULT_FONT = ReflectionHacks.privateMethod(AbstractCard.class, "getEnergyFont").invoke(new Bash());

    private final Texture texture;
    private final Color drawColor;
    private final float offsetX;
    private final float offsetY;
    private final BitmapFont font;
    private final String text;
    private final float textOffsetX;
    private final float textOffsetY;
    private final Color textColor;

    public IconPayload(Builder builder) {
        texture = builder.texture;
        drawColor = builder.drawColor;
        offsetX = builder.offsetX;
        offsetY = builder.offsetY;
        font = builder.font;
        text = builder.text;
        textOffsetX = builder.textOffsetX;
        textOffsetY = builder.textOffsetY;
        textColor = builder.textColor;
    }

    public Texture getTexture() {
        return texture;
    }

    public Color getDrawColor() {
        return drawColor;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public boolean shouldRenderText() {
        return getText() != null;
    }

    public String getText() {
        return text;
    }

    public BitmapFont getFont() {
        return font;
    }

    public float getTextOffsetX() {
        return textOffsetX;
    }

    public float getTextOffsetY() {
        return textOffsetY;
    }

    public Color getTextColor() {
        return textColor;
    }

    public static class Builder {
        private final Texture texture;
        private Color drawColor = Color.WHITE.cpy();
        private float offsetX;
        private float offsetY;
        private String text;
        private BitmapFont font = DEFAULT_FONT;
        private float textOffsetX;
        private float textOffsetY;
        private Color textColor = Color.WHITE.cpy();

        public Builder(Texture texture) {
            this.texture = texture;
        }

        public Builder drawColor(Color color) {
            drawColor = color;
            return this;
        }

        public Builder offsetX(float x) {
            offsetX = x;
            return this;
        }

        public Builder offsetY(float y) {
            offsetY = y;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder font(BitmapFont font) {
            this.font = font;
            return this;
        }

        public Builder textOffsetX(float x) {
            textOffsetX = x;
            return this;
        }

        public Builder textOffsetY(float y) {
            textOffsetY = y;
            return this;
        }

        public Builder textColor(Color color) {
            textColor = color;
            return this;
        }

        public void render(AbstractCard card) {
            ExtraIconsPatch.ExtraIconsField.extraIcons.get(card).add(new IconPayload(this));
        }
    }
}

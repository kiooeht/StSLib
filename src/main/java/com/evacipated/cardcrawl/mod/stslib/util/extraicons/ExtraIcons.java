package com.evacipated.cardcrawl.mod.stslib.util.extraicons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class ExtraIcons {

    public static void renderIcon(AbstractCard card, Texture texture, float offsetX, float offsetY, Color drawColor) {
        renderIcon(card, texture, offsetX, offsetY, drawColor, null, null, 0, 0, null);
    }

    public static void renderIcon(AbstractCard card, Texture texture, String text, BitmapFont font, float textOffsetX, float textOffsetY, Color textColor) {
        renderIcon(card, texture, 0, 0, Color.WHITE.cpy(), text, font, textOffsetX, textOffsetY, textColor);
    }

    public static void renderIcon(AbstractCard card, Texture texture, float offsetX, float offsetY, Color drawColor, String text, BitmapFont font, float textOffsetX, float textOffsetY, Color textColor) {
        icon(texture).offsetX(offsetX).offsetY(offsetY).drawColor(drawColor).text(text).font(font).textOffsetX(textOffsetX).textOffsetY(textOffsetY).textColor(textColor).render(card);
    }

    public static IconPayload.Builder icon(Texture texture) {
        return new IconPayload.Builder(texture);
    }

}

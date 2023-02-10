package com.evacipated.cardcrawl.mod.stslib.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.evacipated.cardcrawl.mod.stslib.util.extraicons.IconPayload;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import java.util.ArrayList;

public class ExtraIconsPatch {
    public static final int ENERGY_POSITION_X = -137;
    public static final int ENERGY_POSITION_Y = 159;

    @SpirePatch(
            clz = AbstractCard.class,
            method = SpirePatch.CLASS
    )
    public static class ExtraIconsField {
        public static SpireField<ArrayList<IconPayload>> extraIcons = new SpireField<>(ArrayList::new);
    }

    @SpirePatch2(
            clz = AbstractCard.class,
            method = "render",
            paramtypez = {SpriteBatch.class, boolean.class}
    )
    @SpirePatch2(
            clz = AbstractCard.class,
            method = "renderInLibrary"
    )
    public static class RenderPatch {

        @SpirePostfixPatch
        public static void afterRender(AbstractCard __instance, SpriteBatch sb) {
            ArrayList<IconPayload> icons = ExtraIconsPatch.ExtraIconsField.extraIcons.get(__instance);
            if (!icons.isEmpty()) {
                if (!__instance.isFlipped) {
                    float x = ENERGY_POSITION_X;
                    float y = ENERGY_POSITION_Y;
                    for (IconPayload icon : icons) {
                        TextureRegion region = new TextureRegion(icon.getTexture());
                        y -= region.getRegionHeight();
                        y -= icon.getMargin();
                        x += icon.getOffsetX();
                        y += icon.getOffsetY();
                        x -= icon.getWidth() / 2.0f;
                        Color color = icon.getDrawColor().cpy();
                        if (color.a == 1.0f) {
                            color.a = __instance.transparency;
                        }
                        sb.setColor(color);
                        sb.draw(region,
                                __instance.current_x + x,
                                __instance.current_y + y,
                                -x, -y, icon.getWidth(), icon.getHeight(),
                                __instance.drawScale * Settings.scale, __instance.drawScale * Settings.scale, __instance.angle);
                        if (icon.shouldRenderText()) {
                            FontHelper.renderRotatedText(sb,
                                    icon.getFont(), icon.getText(),
                                    __instance.current_x, __instance.current_y,
                                    (x + icon.getTextOffsetX() + (icon.getWidth() / 2f)) * __instance.drawScale * Settings.scale,
                                    (y + icon.getTextOffsetY() + (icon.getHeight() / 2f)) * __instance.drawScale * Settings.scale,
                                    __instance.angle, false, icon.getTextColor());
                        }
                        x -= icon.getOffsetX();
                        y -= icon.getMargin();
                        x += icon.getWidth() / 2.0f;
                    }
                }
                icons.clear();
            }
        }

    }


    @SpirePatch2(
            clz = SingleCardViewPopup.class,
            method = "render"
    )
    public static class SingleCardViewRenderPatch {

        @SpirePostfixPatch
        public static void afterRender(SingleCardViewPopup __instance, SpriteBatch sb) {
            AbstractCard card = ReflectionHacks.getPrivate(__instance, SingleCardViewPopup.class, "card");
            ArrayList<IconPayload> icons = ExtraIconsPatch.ExtraIconsField.extraIcons.get(card);
            if (!icons.isEmpty()) {
                if (!card.isFlipped) {
                    float x = ENERGY_POSITION_X;
                    float y = ENERGY_POSITION_Y;
                    for (IconPayload icon : icons) {
                        TextureRegion region = new TextureRegion(icon.getTexture());
                        y -= region.getRegionHeight();
                        y -= icon.getMargin();
                        x += icon.getOffsetX();
                        y += icon.getOffsetY();
                        x -= icon.getWidth() / 2.0f;
                        Color color = icon.getDrawColor().cpy();
                        if (color.a == 1.0f) {
                            color.a = card.transparency;
                        }
                        sb.setColor(color);
                        sb.draw(region,
                                (Settings.WIDTH / 2.0f) + x,
                                (Settings.HEIGHT / 2.0f) + y,
                                -x, -y, icon.getWidth(), icon.getHeight(),
                                2 * Settings.scale, 2 * Settings.scale, 0);
                        if (icon.shouldRenderText()) {
                            String text = icon.getText();
                            float textWidth = FontHelper.getWidth(icon.getSingleCardViewFont(), text, Settings.scale);
                            float textHeight = FontHelper.getHeight(icon.getSingleCardViewFont(), text, Settings.scale);
                            FontHelper.renderFont(
                                    sb,
                                    icon.getSingleCardViewFont(),
                                    icon.getText(),
                                    (Settings.WIDTH / 2.0f) + ((x + icon.getTextOffsetX() + (icon.getWidth() / 2f)) * 2 * Settings.scale) - (textWidth / 2f),
                                    (Settings.HEIGHT / 2.0f) + ((y + icon.getTextOffsetY() + (icon.getHeight() / 2f)) * 2 * Settings.scale) + (textHeight / 2f),
                                    icon.getTextColor());
                        }
                        x -= icon.getOffsetX();
                        y -= icon.getMargin();
                        x += icon.getWidth() / 2.0f;
                    }
                }
                icons.clear();
            }
        }

    }

}

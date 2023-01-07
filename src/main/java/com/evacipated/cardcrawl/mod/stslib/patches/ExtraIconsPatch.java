package com.evacipated.cardcrawl.mod.stslib.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.evacipated.cardcrawl.mod.stslib.util.extraicons.ExtraIcons;
import com.evacipated.cardcrawl.mod.stslib.util.extraicons.IconPayload;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

import java.util.ArrayList;

public class ExtraIconsPatch {

    @SpirePatch(
            clz = AbstractCard.class,
            method = SpirePatch.CLASS
    )
    public static class ExtraIconsField {
        public static SpireField<ArrayList<IconPayload>> extraIcons = new SpireField<>(ArrayList::new);
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "render",
            paramtypez = {SpriteBatch.class, boolean.class}
    )
    public static class RenderPatch {
        public static final int ENERGY_POSITION_X = -161;
        public static final int ENERGY_POSITION_Y = 159;

        @SpirePostfixPatch
        public static void afterRender(AbstractCard __instance, SpriteBatch sb, boolean selected) {
            ArrayList<IconPayload> icons = ExtraIconsPatch.ExtraIconsField.extraIcons.get(__instance);
            if (!icons.isEmpty()) {
                float x = ENERGY_POSITION_X;
                float y = ENERGY_POSITION_Y;
                for (IconPayload icon : icons) {
                    TextureRegion region = new TextureRegion(icon.getTexture());
                    y -= region.getRegionHeight();
                    sb.setColor(icon.getDrawColor());
                    sb.draw(region,
                            __instance.current_x + ((x + icon.getOffsetX())),
                            __instance.current_y + ((y + icon.getOffsetY())),
                            -x, -y, region.getRegionWidth(), region.getRegionHeight(),
                            __instance.drawScale * Settings.scale, __instance.drawScale * Settings.scale, __instance.angle);
                    if (icon.shouldRenderText()) {
                        FontHelper.renderRotatedText(sb,
                                icon.getFont(), icon.getText(),
                                __instance.current_x, __instance.current_y,
                                (x + icon.getOffsetX() + icon.getTextOffsetX() + (region.getRegionWidth() / 2f)) * __instance.drawScale * Settings.scale,
                                (y + icon.getOffsetY() + icon.getTextOffsetY() + (region.getRegionHeight() / 2f)) * __instance.drawScale * Settings.scale,
                                __instance.angle, false, icon.getTextColor());
                    }
                }
                icons.clear();
            }
        }

    }

}

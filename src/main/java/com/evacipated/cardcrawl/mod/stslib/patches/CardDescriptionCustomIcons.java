package com.evacipated.cardcrawl.mod.stslib.patches;

import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.ShrinkLongDescription;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;
import com.evacipated.cardcrawl.mod.stslib.icons.CustomIconHelper;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import javassist.CtBehavior;

import java.util.HashSet;

public class CardDescriptionCustomIcons {

    @SpirePatch(clz=AbstractCard.class, method="renderDescription")
    @SpirePatch(clz=AbstractCard.class, method="renderDescriptionCN")
    public static class RenderSmallIcon {
        private static final float CARD_ENERGY_IMG_WIDTH = 24.0f * Settings.scale;
        private static final HashSet<AbstractCard> reinit = new HashSet<>();

        @SpireInsertPatch(locator=Locator.class, localvars={"spacing", "i", "start_x", "draw_y", "font", "textColor", "tmp", "gl"})
        public static void Insert(AbstractCard __instance, SpriteBatch sb, float spacing, int i, @ByRef float[] start_x, float draw_y, BitmapFont font, Color textColor, @ByRef String[] tmp, GlyphLayout gl) {
            if (tmp[0].length() > 0 && tmp[0].charAt(0) == '[') {
                String key = tmp[0].trim();
                if (key.endsWith(AbstractCustomIcon.CODE_ENDING)) {
                    key = key.replace("*d", "D").replace("*b", "B").replace("*m", "M");
                }
                AbstractCustomIcon icon = CustomIconHelper.getIcon(key);
                if (icon != null) {
                    gl.width = CARD_ENERGY_IMG_WIDTH * __instance.drawScale * ShrinkLongDescription.Scale.descriptionScale.get(__instance);
                    renderSmallIcon(__instance, sb, icon,
                            (start_x[0] - __instance.current_x) / Settings.scale / __instance.drawScale / ShrinkLongDescription.Scale.descriptionScale.get(__instance),
                            (draw_y + i * 1.45f * -font.getCapHeight() - 6f - __instance.current_y + font.getCapHeight()) / Settings.scale / __instance.drawScale / ShrinkLongDescription.Scale.descriptionScale.get(__instance) - 26);
                    start_x[0] += gl.width;
                    tmp[0] = "";
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception
            {
                Matcher matcher = new Matcher.MethodCallMatcher(GlyphLayout.class, "setText");
                int[] lines = LineFinder.findAllInOrder(ctBehavior, matcher);
                return new int[]{lines[lines.length-1]}; // Only last occurrence
            }
        }
    }

    public static void renderSmallIcon(AbstractCard card, SpriteBatch sb, AbstractCustomIcon icon, float offsetX, float offsetY) {
        icon.renderOnCard(sb, card, offsetX, offsetY);
    }

    @SpirePatch(clz=SingleCardViewPopup.class, method="renderDescription")
    @SpirePatch(clz=SingleCardViewPopup.class, method="renderDescriptionCN")
    public static class RenderSmallIconSingleCardView {
        @SpireInsertPatch(locator=Locator.class, localvars={"spacing", "i", "start_x", "draw_y", "tmp", "gl", "card_energy_w", "drawScale", "current_x", "current_y", "card", "font"})
        public static void Insert(SingleCardViewPopup __instance, SpriteBatch sb, float spacing, int i, @ByRef float[] start_x, float draw_y, @ByRef String[] tmp, GlyphLayout gl, float card_energy_w, float drawScale, float current_x, float current_y, AbstractCard card, BitmapFont font) {
            if (tmp[0].length() > 0 && tmp[0].charAt(0) == '[') {
                String key = tmp[0].trim();
                if (key.endsWith(AbstractCustomIcon.CODE_ENDING)) {
                    key = key.replace("*d", "D").replace("*b", "B").replace("*m", "M");
                }
                AbstractCustomIcon icon = CustomIconHelper.getIcon(key);
                if (icon != null) {
                    gl.width = card_energy_w * drawScale * ShrinkLongDescription.Scale.descriptionScale.get(card);
                    renderSCVIcon(card, sb, icon,
                            (start_x[0] - current_x) / Settings.scale / drawScale / ShrinkLongDescription.Scale.descriptionScale.get(card),
                            (draw_y + (float)i * 1.53F * -font.getCapHeight() - 12f - current_y - font.getCapHeight()/2f) / Settings.scale / drawScale / ShrinkLongDescription.Scale.descriptionScale.get(card),
                            drawScale, current_x, current_y);
                    start_x[0] += gl.width;
                    tmp[0] = "";
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher matcher = new Matcher.MethodCallMatcher(GlyphLayout.class, "setText");
                int[] lines = LineFinder.findAllInOrder(ctBehavior, matcher);
                return new int[]{lines[lines.length-1]}; // Only last occurrence
            }
        }
    }

    public static void renderSCVIcon(AbstractCard card, SpriteBatch sb, AbstractCustomIcon icon, float x, float y, float scale, float cx, float cy) {
        //icon.render(sb, x, y, cx, cy, scale * Settings.scale * ShrinkLongDescription.Scale.descriptionScale.get(card), 0);
        icon.renderOnSCV(sb, card, x, y, cx, cy, scale);
        //icon.render(sb, cx + x * Settings.scale * scale + icon.region.offsetX * Settings.scale - 4.0F * Settings.scale, cy + y * Settings.scale * scale + 280.0F * Settings.scale, icon.region.getRegionWidth()/2F, icon.region.getRegionWidth()/2F, scale * Settings.scale, 0);
    }

    @SpirePatch(clz=AbstractCard.class, method="initializeDescription")
    public static class AlterIconDescriptionSize {
        private static final float CARD_ENERGY_IMG_WIDTH = 24.0f * Settings.scale;
        @SpireInsertPatch(locator=Locator.class, localvars={"gl", "word"})
        public static void Insert(AbstractCard __instance,  @ByRef GlyphLayout[] gl, String word) {
            if (word.length() > 0 && word.charAt(0) == '[') {
                AbstractCustomIcon icon = CustomIconHelper.getIcon(word.trim());
                if (icon != null) {
                    gl[0].setText(FontHelper.cardDescFont_N, " ");
                    gl[0].width = CARD_ENERGY_IMG_WIDTH;
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher matcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "DESC_BOX_WIDTH");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }
    }
}

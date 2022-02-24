package com.evacipated.cardcrawl.mod.stslib.patches;

import basemod.patches.com.megacrit.cardcrawl.helpers.TipHelper.FakeKeywords;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.google.gson.annotations.SerializedName;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import javassist.*;

import java.util.ArrayList;

import static basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup.TitleFontSize.fontFile;

public class FlavorText {
    private static final BitmapFont flavorFont = FlavorText.prepFont(22.0f, false);

    private static final String TIP_TOP_STRING = "images/stslib/ui/tipTop.png";
    private static final String TIP_MID_STRING = "images/stslib/ui/tipMid.png";
    private static final String TIP_BOT_STRING = "images/stslib/ui/tipBot.png";

    public static Texture TIP_TOP;
    public static Texture TIP_MID;
    public static Texture TIP_BOT;

    @SpirePatch2(
            clz = TipHelper.class,
            method = "renderKeywords"
    )
    public static class TipHelperRenderLorePatch{
        @SpirePostfixPatch
        public static void TipHelperRenderLore(float x, @ByRef float[] y, SpriteBatch sb,
                                               ArrayList<String> keywords,
                                               float ___TIP_DESC_LINE_SPACING, float ___BODY_TEXT_WIDTH,
                                               float ___BOX_EDGE_H, float ___SHADOW_DIST_X, float ___SHADOW_DIST_Y,
                                               float ___BOX_W, float ___BOX_BODY_H, float ___TEXT_OFFSET_X,
                                               AbstractCard ___card) {
            Color boxColor;
            Color textColor;
            String s;

            s = AbstractCardFlavorFields.flavor.get(___card);
            boxColor = AbstractCardFlavorFields.boxColor.get(___card);
            textColor = AbstractCardFlavorFields.textColor.get(___card);
            if (boxColor == null || s == null || s.equals("") || textColor == null)
                return;

            float h = -FontHelper.getSmartHeight(flavorFont, s, ___BODY_TEXT_WIDTH, ___TIP_DESC_LINE_SPACING)
                    - 40.0F * Settings.scale;

            if (TIP_BOT == null || TIP_MID == null || TIP_TOP == null)
                setTextures();

            sb.setColor(Settings.TOP_PANEL_SHADOW_COLOR);
            sb.draw(ImageMaster.KEYWORD_TOP, x + ___SHADOW_DIST_X, y[0] - ___SHADOW_DIST_Y, ___BOX_W, ___BOX_EDGE_H);
            sb.draw(ImageMaster.KEYWORD_BODY, x + ___SHADOW_DIST_X, y[0] - h - ___BOX_EDGE_H - ___SHADOW_DIST_Y,
                    ___BOX_W, h + ___BOX_EDGE_H);
            sb.draw(ImageMaster.KEYWORD_BOT, x + ___SHADOW_DIST_X, y[0] - h - ___BOX_BODY_H - ___SHADOW_DIST_Y,
                    ___BOX_W, ___BOX_EDGE_H);
            sb.setColor(boxColor.cpy());
            sb.draw(TIP_TOP, x, y[0], ___BOX_W, ___BOX_EDGE_H);
            sb.draw(TIP_MID, x, y[0] - h - ___BOX_EDGE_H, ___BOX_W, h + ___BOX_EDGE_H);
            sb.draw(TIP_BOT, x, y[0] - h - ___BOX_BODY_H, ___BOX_W, ___BOX_EDGE_H);

            FontHelper.renderSmartText(sb, flavorFont, s,x + ___TEXT_OFFSET_X,
                    y[0] + 13.0F * Settings.scale, ___BODY_TEXT_WIDTH, ___TIP_DESC_LINE_SPACING,
                    textColor);

            y[0] -= h + ___BOX_EDGE_H * 3.15F;
        }
    }

    @SpirePatch2(
            clz = FakeKeywords.class,
            method = "Prefix"
    )
    public static class IHeardYouLikePatchesSoIPutAPatchInYourPatch {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"sumTooltipHeight"}
        )
        public static void insertPatch(float x, SpriteBatch sb, AbstractCard ___card,
                                       float ___BODY_TEXT_WIDTH, float ___TIP_DESC_LINE_SPACING,
                                       float ___BOX_EDGE_H, @ByRef float[] sumTooltipHeight) {
            String s = AbstractCardFlavorFields.flavor.get(___card);
            if (s == null || s.equals(""))
                return;

            float textHeight = -FontHelper.getSmartHeight(flavorFont, s, ___BODY_TEXT_WIDTH, ___TIP_DESC_LINE_SPACING)
                    - 40.0F * Settings.scale;
            sumTooltipHeight[0] += textHeight + ___BOX_EDGE_H * 3.15F;
        }
        private static class Locator extends SpireInsertLocator {
            private Locator() {}
            public int[] Locate(CtBehavior behavior) throws Exception {
                Matcher matcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "IMG_HEIGHT");
                return LineFinder.findInOrder(behavior, matcher);
            }
        }
    }

    @SpirePatch(
            clz = CardStrings.class,
            method = SpirePatch.CLASS
    )
    public static class CardStringsFlavorField {
        @SerializedName("FLAVOR")
        public static SpireField<String> FLAVOR = new SpireField<>(() -> "");
    }

    @SpirePatch2(
            clz = AbstractCard.class,
            method = SpirePatch.CLASS
    )
    public static class AbstractCardFlavorFields {
        public static SpireField<String> flavor = new SpireField<>(() -> null);
        public static SpireField<Color> boxColor = new SpireField<>(Color.WHITE::cpy);
        public static SpireField<Color> textColor = new SpireField<>(Color.BLACK::cpy);
    }

    @SpirePatch2(
            clz = AbstractCard.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {String.class, String.class, String.class, int.class, String.class,
                    AbstractCard.CardType.class, AbstractCard.CardColor.class, AbstractCard.CardRarity.class,
                    AbstractCard.CardTarget.class, DamageInfo.DamageType.class}
    )
    public static class FlavorIntoCardStrings {
        @SpirePostfixPatch
        public static void postfix(AbstractCard __instance) {
            CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(__instance.cardID);
            if (cardStrings == null || CardStringsFlavorField.FLAVOR.get(cardStrings) == null)
                return;

            AbstractCardFlavorFields.flavor.set(__instance, CardStringsFlavorField.FLAVOR.get(cardStrings));
        }
    }

    // We want tipBodyFont but without shadows basically
    public static BitmapFont prepFont(float size, boolean isLinearFiltering) {
        FreeTypeFontGenerator g = new FreeTypeFontGenerator(fontFile);

        if (Settings.BIG_TEXT_MODE) {
            size *= 1.2F;
        }

        float fontScale = 1.0f;

        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.hinting = FreeTypeFontGenerator.Hinting.Slight;
        p.characters = "";
        p.incremental = true;
        p.size = Math.round(size * fontScale * Settings.scale);
        p.gamma = 0.9f;
        p.spaceX = 0;
        p.spaceY = 0;
        p.borderStraight = false;
        p.borderGamma = 0.9F;
        p.borderColor = new Color(0, 0, 0, 1);
        p.borderWidth = 0.0F;
        p.shadowColor = new Color(0, 0, 0, 0);
        p.shadowOffsetX = 0;
        p.shadowOffsetY = 0;
        if (isLinearFiltering) {
            p.minFilter = Texture.TextureFilter.Linear;
            p.magFilter = Texture.TextureFilter.Linear;
        } else {
            p.minFilter = Texture.TextureFilter.Nearest;
            p.magFilter = Texture.TextureFilter.MipMapLinearNearest;
        }

        g.scaleForPixelHeight(p.size);
        BitmapFont font = g.generateFont(p);
        font.setUseIntegerPositions(!isLinearFiltering);
        font.getData().markupEnabled = true;
        if (LocalizedStrings.break_chars != null) {
            font.getData().breakChars = LocalizedStrings.break_chars.toCharArray();
        }

        font.getData().fontFile = fontFile;
        return font;
    }

    private static void setTextures() {
        TIP_TOP = new Texture(TIP_TOP_STRING);
        TIP_MID = new Texture(TIP_MID_STRING);
        TIP_BOT = new Texture(TIP_BOT_STRING);
    }
}
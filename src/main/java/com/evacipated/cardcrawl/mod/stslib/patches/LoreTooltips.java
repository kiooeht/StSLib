package com.evacipated.cardcrawl.mod.stslib.patches;

import basemod.patches.com.megacrit.cardcrawl.helpers.TipHelper.FakeKeywords;
import basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup.TitleFontSize;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.evacipated.cardcrawl.modthespire.lib.*;
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

import java.lang.reflect.Field;
import java.util.ArrayList;

import static basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup.TitleFontSize.fontFile;

public class LoreTooltips {
    private static BitmapFont loreFont = LoreTooltips.prepFont(22.0f, false);

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
                                               Color ___BASE_COLOR, AbstractCard ___card) {
            Color loreColor;
            Color textColor;
            String s;
            try {
                Field field1 = AbstractCard.class.getField("lore");
                Field field2 = AbstractCard.class.getField("loreColor");
                Field field3 = AbstractCard.class.getField("loreTextColor");
                s = (String) field1.get(___card);
                loreColor = (Color) field2.get(___card);
                textColor = (Color) field3.get(___card);
                if (loreColor == null || s == null)
                    return;
                // While this tries to make the color visible the best practice is to just define the textColor yourself.
                if (textColor == null) {
                    if (loreColor.g + loreColor.b + loreColor.r < 1.5f)
                        textColor = ___BASE_COLOR;
                    else
                        textColor = Color.BLACK.cpy();
                }
            }
            catch (Exception e) {
                return;
            }

            float h = -FontHelper.getSmartHeight(loreFont, s, ___BODY_TEXT_WIDTH, ___TIP_DESC_LINE_SPACING)
                    - 40.0F * Settings.scale;

            if (TIP_BOT == null || TIP_MID == null || TIP_TOP == null)
                setTextures();

            sb.setColor(Settings.TOP_PANEL_SHADOW_COLOR);
            sb.draw(ImageMaster.KEYWORD_TOP, x + ___SHADOW_DIST_X, y[0] - ___SHADOW_DIST_Y, ___BOX_W, ___BOX_EDGE_H);
            sb.draw(ImageMaster.KEYWORD_BODY, x + ___SHADOW_DIST_X, y[0] - h - ___BOX_EDGE_H - ___SHADOW_DIST_Y, ___BOX_W, h + ___BOX_EDGE_H);
            sb.draw(ImageMaster.KEYWORD_BOT, x + ___SHADOW_DIST_X, y[0] - h - ___BOX_BODY_H - ___SHADOW_DIST_Y, ___BOX_W, ___BOX_EDGE_H);
            sb.setColor(loreColor.cpy());
            sb.draw(TIP_TOP, x, y[0], ___BOX_W, ___BOX_EDGE_H);
            sb.draw(TIP_MID, x, y[0] - h - ___BOX_EDGE_H, ___BOX_W, h + ___BOX_EDGE_H);
            sb.draw(TIP_BOT, x, y[0] - h - ___BOX_BODY_H, ___BOX_W, ___BOX_EDGE_H);

            FontHelper.renderSmartText(sb, loreFont, s,x + ___TEXT_OFFSET_X,
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
        public static SpireReturn insertPatch(float x, SpriteBatch sb, AbstractCard ___card,
                                              float ___BODY_TEXT_WIDTH, float ___TIP_DESC_LINE_SPACING, float ___BOX_EDGE_H,
                                              @ByRef float[] sumTooltipHeight) {
            String s;
            try {
                Field field1 = AbstractCard.class.getField("lore");
                s = (String) field1.get(___card);
            }
            catch (Exception e) {
                return SpireReturn.Continue();
            }

            float textHeight = -FontHelper.getSmartHeight(loreFont, s, ___BODY_TEXT_WIDTH, ___TIP_DESC_LINE_SPACING)
                    - 40.0F * Settings.scale;
            sumTooltipHeight[0] += textHeight + ___BOX_EDGE_H * 3.15F;
            return SpireReturn.Continue();
        }
        private static class Locator extends SpireInsertLocator {
            private Locator() {}
            public int[] Locate(CtBehavior behavior) throws Exception {
                Matcher matcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "IMG_HEIGHT");
                return LineFinder.findInOrder(behavior, matcher);
            }
        }
    }

    @SpirePatch2(
            clz = CardStrings.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class CardStringsLoreVarPatch {
        @SpireRawPatch
        public static void rawPatch(CtBehavior ctBehavior) throws NotFoundException, CannotCompileException {
            CtClass cardStringsCtClass = ctBehavior.getDeclaringClass().getClassPool().get(CardStrings.class.getName());
            String fieldSource = "public String LORE;";
            CtField field = CtField.make(fieldSource, cardStringsCtClass);
            cardStringsCtClass.addField(field);
        }
    }

    @SpirePatch2(
            clz = AbstractCard.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {String.class, String.class, String.class, int.class, String.class,
                    AbstractCard.CardType.class, AbstractCard.CardColor.class, AbstractCard.CardRarity.class,
                    AbstractCard.CardTarget.class, DamageInfo.DamageType.class}
    )
    public static class AbstractCardLoreFields {
        @SpireRawPatch
        public static void rawPatch(CtBehavior ctBehavior) throws NotFoundException, CannotCompileException {
            CtClass abstractCardClass = ctBehavior.getDeclaringClass().getClassPool().get(AbstractCard.class.getName());
            String fieldSource1 = "public String lore = null;";
            String fieldSource2 = "public com.badlogic.gdx.graphics.Color loreColor = com.badlogic.gdx.graphics.Color.WHITE.cpy();";
            String fieldSource3 = "public com.badlogic.gdx.graphics.Color loreTextColor = null;";
            CtField field1 = CtField.make(fieldSource1, abstractCardClass);
            CtField field2 = CtField.make(fieldSource2, abstractCardClass);
            CtField field3 = CtField.make(fieldSource3, abstractCardClass);
            abstractCardClass.addField(field1);
            abstractCardClass.addField(field2);
            abstractCardClass.addField(field3);
        }
        @SpirePostfixPatch
        public static void postfix(AbstractCard __instance) {
            CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(__instance.cardID);
            setLore(__instance, cardStrings);
        }
    }

    public static String getLore(CardStrings cardStrings) {
        try {
            Field field2 = CardStrings.class.getField("LORE");
            return (String) field2.get(cardStrings);
        }
        catch (Exception e) {
            return null;
        }
    }

    // returns false if the operation fails, true otherwise
    public static boolean setLore(AbstractCard card, String lore) {
        try {
            Field field = AbstractCard.class.getField("lore");
            field.set(card, lore);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean setLore(AbstractCard card, CardStrings cardStrings) {
        try {
            Field field1 = AbstractCard.class.getField("lore");
            Field field2 = CardStrings.class.getField("LORE");
            field1.set(card, field2.get(cardStrings));
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean setLoreColor(AbstractCard card, Color loreColor) {
        try {
            Field field = AbstractCard.class.getField("loreColor");
            field.set(card, loreColor);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean setLoreTextColor(AbstractCard card, Color loreTextColor) {
        try {
            Field field = AbstractCard.class.getField("loreTextColor");
            field.set(card, loreTextColor);
        }
        catch (Exception e) {
            return false;
        }
        return true;
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
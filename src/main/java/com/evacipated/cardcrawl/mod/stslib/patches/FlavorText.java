package com.evacipated.cardcrawl.mod.stslib.patches;

import basemod.ReflectionHacks;
import basemod.patches.com.megacrit.cardcrawl.helpers.TipHelper.FakeKeywords;
import basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup.ScrollingTooltips;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.google.gson.annotations.SerializedName;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import javassist.CtBehavior;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

import static basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup.TitleFontSize.fontFile;

public class FlavorText {
    private static final BitmapFont flavorFont = FlavorText.prepFont(22.0f, false);

    private static final String TIP_TOP_STRING = "images/stslib/ui/tipTop.png";
    private static final String TIP_MID_STRING = "images/stslib/ui/tipMid.png";
    private static final String TIP_BOT_STRING = "images/stslib/ui/tipBot.png";

    public static Texture TIP_TOP;
    public static Texture TIP_MID;
    public static Texture TIP_BOT;

    private static AbstractCard scvCard = null;
    private static AbstractCard regularCard = null;

    private static float BODY_TEXT_WIDTH;
    private static float TIP_DESC_LINE_SPACING;
    private static float SHADOW_DIST_X;
    private static float SHADOW_DIST_Y;
    private static float BOX_EDGE_H;
    private static float BOX_BODY_H;
    private static float BOX_W;
    private static float TEXT_OFFSET_X;

    private static final String HEADER_STRING = "@STSLIB:FLAVOR@";

    public enum boxType {
        WHITE,
        TRADITIONAL,
        CUSTOM
    }

    private static void setConstants() {
        BODY_TEXT_WIDTH = ReflectionHacks.getPrivate(null, TipHelper.class, "BODY_TEXT_WIDTH");
        TIP_DESC_LINE_SPACING = ReflectionHacks.getPrivate(null, TipHelper.class, "TIP_DESC_LINE_SPACING");
        SHADOW_DIST_X = ReflectionHacks.getPrivate(null, TipHelper.class, "SHADOW_DIST_X");
        SHADOW_DIST_Y = ReflectionHacks.getPrivate(null, TipHelper.class, "SHADOW_DIST_Y");
        BOX_EDGE_H = ReflectionHacks.getPrivate(null, TipHelper.class, "BOX_EDGE_H");
        BOX_BODY_H = ReflectionHacks.getPrivate(null, TipHelper.class, "BOX_BODY_H");
        BOX_W = ReflectionHacks.getPrivate(null, TipHelper.class, "BOX_W");
        TEXT_OFFSET_X = ReflectionHacks.getPrivate(null, TipHelper.class, "TEXT_OFFSET_X");
    }

    @SpirePatch2(
            clz = AbstractCard.class,
            method = SpirePatch.CLASS
    )
    public static class AbstractCardFlavorFields {
        public static SpireField<String> flavor = new SpireField<>(() -> null);
        public static SpireField<Color> boxColor = new SpireField<>(Color.WHITE::cpy);
        public static SpireField<Color> textColor = new SpireField<>(Color.BLACK::cpy);
        public static SpireField<boxType> flavorBoxType = new SpireField<>(() -> boxType.WHITE);
        public static SpireField<Texture> boxTop = new SpireField<>(() -> null);
        public static SpireField<Texture> boxMid = new SpireField<>(() -> null);
        public static SpireField<Texture> boxBot = new SpireField<>(() -> null);
    }

    @SpirePatch2(
            clz = AbstractPotion.class,
            method = SpirePatch.CLASS
    )
    public static class PotionFlavorFields {
        public static SpireField<String> flavor = new SpireField<>(() -> null);
        public static SpireField<Color> boxColor = new SpireField<>(Color.WHITE::cpy);
        public static SpireField<Color> textColor = new SpireField<>(Color.BLACK::cpy);
        public static SpireField<boxType> flavorBoxType = new SpireField<>(() -> boxType.WHITE);
        public static SpireField<Texture> boxTop = new SpireField<>(() -> null);
        public static SpireField<Texture> boxMid = new SpireField<>(() -> null);
        public static SpireField<Texture> boxBot = new SpireField<>(() -> null);
    }

    @SpirePatch2(
            clz = PowerTip.class,
            method = SpirePatch.CLASS
    )
    public static class PowerTipFlavorFields {
        public static SpireField<Color> boxColor = new SpireField<>(Color.WHITE::cpy);
        public static SpireField<Color> textColor = new SpireField<>(Color.BLACK::cpy);
        public static SpireField<boxType> flavorBoxType = new SpireField<>(() -> boxType.WHITE);
        public static SpireField<Texture> boxTop = new SpireField<>(() -> null);
        public static SpireField<Texture> boxMid = new SpireField<>(() -> null);
        public static SpireField<Texture> boxBot = new SpireField<>(() -> null);
    }

    @SpirePatch(
            clz = CardStrings.class,
            method = SpirePatch.CLASS
    )
    public static class CardStringsFlavorField {
        @SerializedName("FLAVOR")
        public static SpireField<String> flavor = new SpireField<>(() -> null);
    }

    @SpirePatch(
            clz = PotionStrings.class,
            method = SpirePatch.CLASS
    )
    public static class PotionStringsFlavorField {
        @SerializedName("FLAVOR")
        public static SpireField<String> flavor = new SpireField<>(() -> null);
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
            CardStrings cardStrings = ((Map<String, CardStrings>)ReflectionHacks.getPrivateStatic(LocalizedStrings.class, "cards")).get(__instance.cardID);
            if (cardStrings == null || CardStringsFlavorField.flavor.get(cardStrings) == null)
                return;

            AbstractCardFlavorFields.flavor.set(__instance, CardStringsFlavorField.flavor.get(cardStrings));
        }
    }

    @SpirePatch2(
            clz = AbstractPotion.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {String.class, String.class, AbstractPotion.PotionRarity.class, AbstractPotion.PotionSize.class,
                    AbstractPotion.PotionColor.class}
    )
    @SpirePatch2(
            clz = AbstractPotion.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {String.class, String.class, AbstractPotion.PotionRarity.class, AbstractPotion.PotionSize.class,
                    AbstractPotion.PotionEffect.class, Color.class, Color.class, Color.class}
    )
    public static class FlavorIntoPotionStrings {
        @SpirePostfixPatch
        public static void postfix(AbstractPotion __instance) {
            PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString(__instance.ID);
            if (potionStrings == null || PotionStringsFlavorField.flavor.get(potionStrings) == null ||
                    PotionStringsFlavorField.flavor.get(potionStrings).equals(""))
                return;

            PotionFlavorFields.flavor.set(__instance, PotionStringsFlavorField.flavor.get(potionStrings));
        }
    }

    @SpirePatch2(
            clz = SingleCardViewPopup.class,
            method = "renderTips"
    )
    public static class PassScvTooltip {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"t"}
        )
        public static void Insert(SingleCardViewPopup __instance, ArrayList<PowerTip> t) {
            for (PowerTip tip : t)
                if (tip.header.equals(HEADER_STRING))
                    return;
            AbstractCard card = ReflectionHacks.getPrivate(__instance, SingleCardViewPopup.class, "card");
            PowerTip tip = new PowerTip(HEADER_STRING, AbstractCardFlavorFields.flavor.get(card));
            PowerTipFlavorFields.textColor.set(tip, AbstractCardFlavorFields.textColor.get(card));
            PowerTipFlavorFields.boxColor.set(tip, AbstractCardFlavorFields.boxColor.get(card));
            PowerTipFlavorFields.flavorBoxType.set(tip, AbstractCardFlavorFields.flavorBoxType.get(card));
            PowerTipFlavorFields.boxBot.set(tip, AbstractCardFlavorFields.boxBot.get(card));
            PowerTipFlavorFields.boxMid.set(tip, AbstractCardFlavorFields.boxMid.get(card));
            PowerTipFlavorFields.boxTop.set(tip, AbstractCardFlavorFields.boxTop.get(card));
            t.add(tip);
        }
        private static class Locator extends SpireInsertLocator {
            private Locator() {}

            @Override
            public int[] Locate(CtBehavior behavior) throws Exception {
                Matcher matcher = new Matcher.MethodCallMatcher(ArrayList.class, "isEmpty");
                return LineFinder.findInOrder(behavior, matcher);
            }
        }
    }

    @SpirePatch2(
            clz = AbstractPotion.class,
            method = "updateEffect"
    )
    public static class PassPotionTooltip {
        @SpirePrefixPatch
        public static void Prefix(AbstractPotion __instance) {
            if (PotionFlavorFields.flavor.get(__instance) == null || PotionFlavorFields.flavor.get(__instance) == null ||
                    PotionFlavorFields.flavor.get(__instance).equals(""))
                return;
            for (PowerTip tip : __instance.tips)
                if (tip.header.equals(HEADER_STRING))
                    return;
            PowerTip tip = new PowerTip(HEADER_STRING, PotionFlavorFields.flavor.get(__instance));
            PowerTipFlavorFields.textColor.set(tip, PotionFlavorFields.textColor.get(__instance));
            PowerTipFlavorFields.boxColor.set(tip, PotionFlavorFields.boxColor.get(__instance));
            PowerTipFlavorFields.flavorBoxType.set(tip, PotionFlavorFields.flavorBoxType.get(__instance));
            PowerTipFlavorFields.boxBot.set(tip, PotionFlavorFields.boxBot.get(__instance));
            PowerTipFlavorFields.boxMid.set(tip, PotionFlavorFields.boxMid.get(__instance));
            PowerTipFlavorFields.boxTop.set(tip, PotionFlavorFields.boxTop.get(__instance));
            __instance.tips.add(tip);
        }
    }

    @SpirePatch2(
            clz = TipHelper.class,
            method = "renderPowerTips"
    )
    public static class TipHelperRenderPowerTips {
        // This also fixes a ui issue where the power tips are too long, but is less often a problem
        // When you don't have flavor tips
        @SpirePrefixPatch
        public static void Prefix(@ByRef float[] y, ArrayList<PowerTip> powerTips) {
            if (scvCard != null) {
                PowerTip tipFlavor = null;

                for (PowerTip tip : powerTips) {
                    if (tip.header.equals(HEADER_STRING)) {
                        if (tip != powerTips.get(powerTips.size() - 1))
                            tipFlavor = tip;
                        break;
                    }
                }

                if (tipFlavor != null) {
                    powerTips.remove(tipFlavor);
                    powerTips.add(tipFlavor);
                }

                return;
            }

            float altY = TipHelper.calculateToAvoidOffscreen(powerTips, 0);
            y[0] = Math.max(altY, y[0]);
        }

        @SpireInsertPatch(
                locator = Locator.class,
                localvars = "tip"
        )
        public static void Insert(float x, float y, SpriteBatch sb, PowerTip tip) {
            addFlavorTip(x, y, sb, tip);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior behavior) throws Exception {
                Matcher matcher = new Matcher.MethodCallMatcher(TipHelper.class, "renderTipBox");
                return LineFinder.findInOrder(behavior, matcher);
            }
        }

    }

    @SpirePatch2(
            clz = TipHelper.class,
            method = "renderTipBox"
    )
    public static class TipHelperRenderFlavorPowerTips {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(float x, float y, SpriteBatch sb, String title, String description) {
            if (!title.equals(HEADER_STRING))
                return SpireReturn.Continue();
            return SpireReturn.Return();
        }
    }

    private static float addFlavorTip(float x, float y, SpriteBatch sb, PowerTip tip) {
        if (tip == null || tip.body == null || tip.body.equals(""))
            return y;

        Color boxColor;
        Color textColor;
        String s;

        s = tip.body;
        boxColor = PowerTipFlavorFields.boxColor.get(tip);
        textColor = PowerTipFlavorFields.textColor.get(tip);

        if (boxColor == null || textColor == null)
            return y;

        if (TIP_BOT == null || TIP_MID == null || TIP_TOP == null)
            setTextures();

        Texture topTexture = TIP_TOP;
        Texture midTexture = TIP_MID;
        Texture botTexture = TIP_BOT;

        if (PowerTipFlavorFields.flavorBoxType.get(tip) == boxType.TRADITIONAL) {
            topTexture = ImageMaster.KEYWORD_TOP;
            midTexture = ImageMaster.KEYWORD_BODY;
            botTexture = ImageMaster.KEYWORD_BOT;
        }
        else if (PowerTipFlavorFields.flavorBoxType.get(tip) == boxType.CUSTOM &&
                PowerTipFlavorFields.boxTop.get(tip) != null &&
                PowerTipFlavorFields.boxMid.get(tip) != null &&
                PowerTipFlavorFields.boxBot.get(tip) != null)
        {
            topTexture = PowerTipFlavorFields.boxTop.get(tip);
            midTexture = PowerTipFlavorFields.boxMid.get(tip);
            botTexture = PowerTipFlavorFields.boxBot.get(tip);
        }

        if (BODY_TEXT_WIDTH == 0)
            setConstants();

        float h = -FontHelper.getSmartHeight(flavorFont, s, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING)
                - 40.0F * Settings.scale;

        sb.setColor(Settings.TOP_PANEL_SHADOW_COLOR);
        sb.draw(ImageMaster.KEYWORD_TOP, x + SHADOW_DIST_X, y - SHADOW_DIST_Y, BOX_W, BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BODY, x + SHADOW_DIST_X, y - h - BOX_EDGE_H - SHADOW_DIST_Y,
                BOX_W, h + BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BOT, x + SHADOW_DIST_X, y - h - BOX_BODY_H - SHADOW_DIST_Y,
                BOX_W, BOX_EDGE_H);
        sb.setColor(boxColor.cpy());
        sb.draw(topTexture, x, y, BOX_W, BOX_EDGE_H);
        sb.draw(midTexture, x, y - h - BOX_EDGE_H, BOX_W, h + BOX_EDGE_H);
        sb.draw(botTexture, x, y - h - BOX_BODY_H, BOX_W, BOX_EDGE_H);

        FontHelper.renderSmartText(sb, flavorFont, s,x + TEXT_OFFSET_X,
                y + 13.0F * Settings.scale, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING,
                textColor);

        y -= h + BOX_EDGE_H * 3.15F;
        return y;
    }

    // FakeKeywords patches TipHelper.renderKeywords, which is card only, not scv
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
    @SpirePatch2(
            clz = FakeKeywords.class,
            method = "Postfix"
    )
    public static class FlavorAfterCustomTooltips {
        @SpirePostfixPatch
        public static void Postifx(float x, @ByRef float[] y, SpriteBatch sb, ArrayList<String> keywords) {

            try {
                Field cardField = TipHelper.class.getDeclaredField("card");
                cardField.setAccessible(true);
                AbstractCard card = (AbstractCard)cardField.get((Object)null);

                String flavor = AbstractCardFlavorFields.flavor.get(card);
                if (flavor == null || flavor.equals(""))
                    return;
                PowerTip tip = new PowerTip(HEADER_STRING, flavor);
                PowerTipFlavorFields.textColor.set(tip, AbstractCardFlavorFields.textColor.get(card));
                PowerTipFlavorFields.boxColor.set(tip, AbstractCardFlavorFields.boxColor.get(card));
                PowerTipFlavorFields.flavorBoxType.set(tip, AbstractCardFlavorFields.flavorBoxType.get(card));
                PowerTipFlavorFields.boxBot.set(tip, AbstractCardFlavorFields.boxBot.get(card));
                PowerTipFlavorFields.boxMid.set(tip, AbstractCardFlavorFields.boxMid.get(card));
                PowerTipFlavorFields.boxTop.set(tip, AbstractCardFlavorFields.boxTop.get(card));

                y[0] = addFlavorTip(x, y[0], sb, tip);
            } catch (IllegalAccessException | NoSuchFieldException var13) {
                var13.printStackTrace();
            }
        }
    }

    @SpirePatch2(
            clz = SingleCardViewPopup.class,
            method = "open",
            paramtypez = { AbstractCard.class, CardGroup.class }
    )
    public static class CatchOpen {
        @SpirePrefixPatch
        public static void prefix(AbstractCard card) {
            FlavorText.scvCard = card;
        }
    }

    @SpirePatch2(
            clz = SingleCardViewPopup.class,
            method = "close"
    )
    public static class CatchClose {
        @SpirePrefixPatch
        public static void prefix() {
            FlavorText.scvCard = null;
        }
    }

    @SpirePatch2(
            clz = ScrollingTooltips.class,
            method = "powerTipsHeight"
    )
    public static class PatchInPatchTwoElectricBoogaloo {
        public static class TipHelperRenderFlavorPatch {
            @SpirePostfixPatch
            public static float postfix(float __result, ArrayList<PowerTip> powerTips) {
                if (scvCard == null)
                    return __result;

                String s = AbstractCardFlavorFields.flavor.get(scvCard);

                if (s == null || s.equals(""))
                    return __result;

                float BOX_EDGE_H = ReflectionHacks.getPrivate(null, ScrollingTooltips.class,
                        "BOX_EDGE_H");
                float BODY_TEXT_WIDTH = ReflectionHacks.getPrivate(null, ScrollingTooltips.class,
                        "BODY_TEXT_WIDTH");
                float TIP_DESC_LINE_SPACING = ReflectionHacks.getPrivate(null, ScrollingTooltips.class,
                        "TIP_DESC_LINE_SPACING");

                float textHeight = -FontHelper.getSmartHeight(flavorFont, s, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING)
                        - 40.0F * Settings.scale;
                __result += textHeight + BOX_EDGE_H * 3.15F;
                return __result;
            }
        }
    }

    @SpirePatch2(
            clz = TipHelper.class,
            method = "getPowerTipHeight"
    )
    public static class FlavorTipsAreShorter {
        @SpirePrefixPatch
        public static SpireReturn<Float> Prefix(PowerTip powerTip) {
            if (BODY_TEXT_WIDTH == 0)
                setConstants();

            if (powerTip.header.equals(HEADER_STRING)) {
                float height = -FontHelper.getSmartHeight(flavorFont, powerTip.body, BODY_TEXT_WIDTH,
                        TIP_DESC_LINE_SPACING) - 40.0F * Settings.scale;
                return SpireReturn.Return(height);
            }
            return SpireReturn.Continue();
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
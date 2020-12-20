package com.evacipated.cardcrawl.mod.stslib.patches;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.CommonKeywordIconsField;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import javassist.CtBehavior;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class CommonKeywordIconsPatches {
    public static String purgeName = "";

    //Hook into AbstractCard render methods (above renderType) and call the badge rendering logic if the relevant field is set.
    @SpirePatch(
            clz= AbstractCard.class,
            method="renderCard"
    )
    public static class RenderIcons
    {
        @SpireInsertPatch(
                locator= Locator.class
        )
        public static void patch(AbstractCard __instance, SpriteBatch sb, boolean hovered, boolean selected)
        {
            if(CommonKeywordIconsField.useIcons.get(__instance)) {
                RenderBadges(sb, __instance);
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "renderType");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz= AbstractCard.class,
            method="renderInLibrary"
    )
    public static class RenderIconsInLibrary
    {
        @SpireInsertPatch(
                locator= Locator.class
        )
        public static void patch(AbstractCard __instance, SpriteBatch sb)
        {
            if(CommonKeywordIconsField.useIcons.get(__instance)) {
                RenderBadges(sb, __instance);
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "renderType");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    //Add Keyword powertips when the card uses the icons and make sure there are no duplicates
    @SpirePatch(clz = TipHelper.class, method = "renderTipForCard")
    public static class RenderKeywords {
        @SpireInsertPatch(locator = Locator.class)
        public static void patch(AbstractCard c, SpriteBatch sb, @ByRef ArrayList<String>[] keywords) {
            if(CommonKeywordIconsField.useIcons.get(c)) {
                keywords[0] = addKeywords(c, keywords[0]);
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(TipHelper.class, "KEYWORDS");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }

    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderTips")
    public static class RenderKeywordsForSingleCardViewPopup {
        @SpireInsertPatch(locator = Locator.class)
        public static void patch(SingleCardViewPopup __instance, SpriteBatch sb, AbstractCard ___card) {
            if(CommonKeywordIconsField.useIcons.get(___card)) {
                ___card.keywords = addKeywords(___card, ___card.keywords);
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "keywords");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }

    public static ArrayList<String> addKeywords(AbstractCard c, ArrayList<String> kws) {
        if (c.isInnate)
        {
            kws.add(GameDictionary.INNATE.NAMES[0]);
        }
        if (c.isEthereal)
        {
            kws.add(GameDictionary.ETHEREAL.NAMES[0]);
        }
        if (c.retain || c.selfRetain)
        {
            kws.add(GameDictionary.RETAIN.NAMES[0]);
        }
        if (c.purgeOnUse)
        {
            kws.add(purgeName);
        }
        if (c.exhaust || c.exhaustOnUseOnce)
        {
            kws.add(GameDictionary.EXHAUST.NAMES[0]);
        }

        return kws.stream().distinct().collect(Collectors.toCollection(ArrayList::new));
    }

    //Render icon on keyword powertips for clarity
    @SpirePatch(
            clz = TipHelper.class,
            method = "renderBox"
    )
    public static class RenderIconOnTips {
        @SpirePostfixPatch
        public static void patch(SpriteBatch sb, String word, float x, float y, AbstractCard ___card) {
            if (___card == null || !CommonKeywordIconsField.useIcons.get(___card)) {
                return;
            }

            Texture badge = null;
            if (word.equals(GameDictionary.INNATE.NAMES[0]))
            {
                badge = StSLib.BADGE_INNATE;
            }
            else if (word.equals(GameDictionary.ETHEREAL.NAMES[0]))
            {
                badge = StSLib.BADGE_ETHEREAL;
            }
            else if (word.equals(GameDictionary.RETAIN.NAMES[0]))
            {
                badge = StSLib.BADGE_RETAIN;
            }
            else if (word.equals(purgeName))
            {
                badge = StSLib.BADGE_PURGE;
            }
            else if (word.equals(GameDictionary.EXHAUST.NAMES[0]))
            {
                badge = StSLib.BADGE_EXHAUST;
            }

            drawBadgeOnTip(x, y, sb, badge);
        }
    }

    //Render in single card view madness
    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "render"
    )
    public static class SingleCardViewRenderIconOnCard {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void patch(SingleCardViewPopup __instance, SpriteBatch sb, AbstractCard ___card, Hitbox ___cardHb) {
            if(CommonKeywordIconsField.useIcons.get(___card)) {
                int offset_y = 0;
                if (___card.isInnate) {
                    drawBadge(sb, ___card, ___cardHb, StSLib.BADGE_INNATE, offset_y);
                    offset_y++;
                }
                if (___card.isEthereal) {
                    drawBadge(sb, ___card, ___cardHb, StSLib.BADGE_ETHEREAL, offset_y);
                    offset_y++;
                }
                if (___card.retain || ___card.selfRetain) {
                    drawBadge(sb, ___card, ___cardHb, StSLib.BADGE_RETAIN, offset_y);
                    offset_y++;
                }
                if (___card.purgeOnUse) {
                    drawBadge(sb, ___card, ___cardHb, StSLib.BADGE_PURGE, offset_y);
                    offset_y++;
                }
                if (___card.exhaust || ___card.exhaustOnUseOnce) {
                    drawBadge(sb, ___card, ___cardHb, StSLib.BADGE_EXHAUST, offset_y);
                    offset_y++;
                }
            }

        }

        private static void drawBadge(SpriteBatch sb, AbstractCard card, Hitbox hb, Texture img, int offset) {
            float badge_w = img.getWidth();
            float badge_h = img.getHeight();
            sb.draw(img, hb.x + hb.width - (badge_w * Settings.scale) * 0.66f, hb.y + hb.height - (badge_h * Settings.scale) * 0.5f - ((offset * (badge_h * Settings.scale)*0.6f)), 0, 0, badge_w , badge_h ,
                    Settings.scale, Settings.scale, card.angle, 0, 0, (int)badge_w, (int)badge_h, false, false);
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(SingleCardViewPopup.class, "renderTips");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }


    private static boolean workaroundSwitch = false;
    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "renderTips"
    )
    public static class DontAlwaysShowIconsPls {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"t"}
        )
        public static void patch(SingleCardViewPopup __instance, SpriteBatch sb, AbstractCard ___card, ArrayList<PowerTip> t) {
            if(CommonKeywordIconsField.useIcons.get(___card)) {
                workaroundSwitch = true;
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(TipHelper.class, "queuePowerTips");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = TipHelper.class,
            method = "renderPowerTips"
    )
    public static class SingleCardViewRenderIconOnTips {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"tip"}
        )
        public static void patch(float x, float y, SpriteBatch sb, ArrayList<PowerTip> powerTips, PowerTip tip) {
            if(!workaroundSwitch) {
                return;
            }

            Texture badge = null;
            if (tip.header.equalsIgnoreCase(GameDictionary.INNATE.NAMES[0]))
            {
                badge = StSLib.BADGE_INNATE;
            }
            else if (tip.header.equalsIgnoreCase(GameDictionary.ETHEREAL.NAMES[0]))
            {
                badge = StSLib.BADGE_ETHEREAL;
            }
            else if (tip.header.equalsIgnoreCase(GameDictionary.RETAIN.NAMES[0]))
            {
                badge = StSLib.BADGE_RETAIN;
            }
            else if (tip.header.equalsIgnoreCase(purgeName))
            {
                badge = StSLib.BADGE_PURGE;
            }
            else if (tip.header.equalsIgnoreCase(GameDictionary.EXHAUST.NAMES[0]))
            {
                badge = StSLib.BADGE_EXHAUST;
            }

            drawBadgeOnTip(x, y, sb, badge);

            if(powerTips.get(powerTips.size() - 1).equals(tip)) {
                workaroundSwitch = false;
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(GlyphLayout.class, "setText");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }

    private static void drawBadgeOnTip(float x, float y, SpriteBatch sb, Texture badge) {
        if(badge != null) {
            float badge_w = badge.getWidth();
            float badge_h = badge.getHeight();
            sb.draw(badge, x + ((320.0F - badge_w/2 - 8f) * Settings.scale), y + (-16.0F * Settings.scale), 0, 0, badge_w, badge_h,
                    0.5f * Settings.scale, 0.5f * Settings.scale, 0, 0, 0, (int)badge_w, (int)badge_h, false, false);
        }
    }

    //The below code was written by EatYourBeetS (https://github.com/EatYourBeetS) with minimal changes made by me (gkjzhgffjh)
    private static void RenderBadges(SpriteBatch sb, AbstractCard card)
    {
        final float alpha = card.transparency; //UpdateBadgeAlpha(card);

        int offset_y = 0;
        if (card.isInnate)
        {
            offset_y -= RenderBadge(sb, card, StSLib.BADGE_INNATE, offset_y, alpha);
        }
        if (card.isEthereal)
        {
            offset_y -= RenderBadge(sb, card,  StSLib.BADGE_ETHEREAL, offset_y, alpha);
        }
        if (card.retain || card.selfRetain)
        {
            offset_y -= RenderBadge(sb, card,  StSLib.BADGE_RETAIN, offset_y, alpha);
        }
        if (card.purgeOnUse)
        {
            offset_y -= RenderBadge(sb, card,  StSLib.BADGE_PURGE, offset_y, alpha);
        }
        if (card.exhaust || card.exhaustOnUseOnce)
        {
            offset_y -= RenderBadge(sb, card,  StSLib.BADGE_EXHAUST, offset_y, alpha);
        }
    }

    private static float RenderBadge(SpriteBatch sb, AbstractCard card, Texture texture, float offset_y, float alpha)
    {
        Vector2 offset = new Vector2(AbstractCard.RAW_W * 0.45f, AbstractCard.RAW_H * 0.45f + offset_y);

        DrawOnCardAuto(sb, card, texture, offset, 64, 64, Color.WHITE, alpha, 1);

        return 38;
    }

    //Rendering code
    private static void DrawOnCardAuto(SpriteBatch sb, AbstractCard card, Texture img, Vector2 offset, float width, float height, Color color, float alpha, float scaleModifier)
    {
        if (card.angle != 0)
        {
            offset.rotate(card.angle);
        }

        offset.scl(Settings.scale * card.drawScale);

        DrawOnCardCentered(sb, card, new Color(color.r, color.g, color.b, alpha), img, card.current_x + offset.x, card.current_y + offset.y, width, height, scaleModifier);
    }

    private static void DrawOnCardCentered(SpriteBatch sb, AbstractCard card, Color color, Texture img, float drawX, float drawY, float width, float height, float scaleModifier)
    {
        final float scale = card.drawScale * Settings.scale * scaleModifier;

        sb.setColor(color);
        sb.draw(img, drawX - (width / 2f), drawY - (height / 2f), width / 2f, height / 2f, width, height,
                scale, scale, card.angle, 0, 0, img.getWidth(), img.getHeight(), false, false);
    }
}

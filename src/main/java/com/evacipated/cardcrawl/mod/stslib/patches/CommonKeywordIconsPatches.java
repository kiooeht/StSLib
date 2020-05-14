package com.evacipated.cardcrawl.mod.stslib.patches;

import basemod.ReflectionHacks;
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
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import javassist.CtBehavior;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class CommonKeywordIconsPatches {
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
                if (c.isInnate)
                {
                    keywords[0].add("innate");
                }
                if (c.isEthereal)
                {
                    keywords[0].add("ethereal");
                }
                if (c.retain || c.selfRetain)
                {
                    keywords[0].add("retain");
                }
                if (c.purgeOnUse)
                {
                    keywords[0].add("purge");
                }
                if (c.exhaust || c.exhaustOnUseOnce)
                {
                    keywords[0].add("exhaust");
                }

                keywords[0] = keywords[0].stream().distinct().collect(Collectors.toCollection(ArrayList::new));
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

    //Render icon on keyword powertips for clarity. Sadly I can't only enable this only on cards that have the keywords without a lot more effort than I'm willing to put into this
    @SpirePatch(
            clz = TipHelper.class,
            method = "renderBox"
    )
    public static class RenderIconOnTips {
        @SpirePostfixPatch
        public static void patch(SpriteBatch sb, String word, float x, float y) {
            Texture badge = null;
            if (word.equals("innate"))
            {
                badge = StSLib.BADGE_INNATE;
            }
            else if (word.equals("ethereal"))
            {
                badge = StSLib.BADGE_ETHEREAL;
            }
            else if (word.equals("retain"))
            {
                badge = StSLib.BADGE_RETAIN;
            }
            else if (word.equals("purge"))
            {
                badge = StSLib.BADGE_PURGE;
            }
            else if (word.equals("exhaust"))
            {
                badge = StSLib.BADGE_EXHAUST;
            }

            if(badge != null) {
                float badge_w = badge.getWidth();
                float badge_h = badge.getHeight();
                sb.draw(badge, x + ((320.0F - badge_w/2 - 8f) * Settings.scale), y + (-16.0F * Settings.scale), 0, 0, badge_w, badge_h,
                        0.5f * Settings.scale, 0.5f * Settings.scale, 0, 0, 0, (int)badge_w, (int)badge_h, false, false);
            }
        }
    }

    //Render in single card view madness
    private static Field cardField = null;
    private static Field cardHbField = null;


    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "render"
    )
    public static class SingleCardViewRenderIconOnCard {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void patch(SingleCardViewPopup __instance, SpriteBatch sb) throws IllegalAccessException {
            if(cardField == null) {
                try {
                    cardField = SingleCardViewPopup.class.getDeclaredField("card");
                    cardField.setAccessible(true);
                } catch (Exception e) {}
            }
            AbstractCard c = (AbstractCard) cardField.get(__instance);

            if(CommonKeywordIconsField.useIcons.get(c)) {
                if(cardHbField == null) {
                    try {
                        cardHbField = SingleCardViewPopup.class.getDeclaredField("cardHb");
                        cardHbField.setAccessible(true);
                    } catch (Exception e) {}
                }
                Hitbox cardHb = (Hitbox) cardHbField.get(__instance);

                int offset_y = 0;
                if (c.isInnate) {
                    drawBadge(sb, c, cardHb, StSLib.BADGE_INNATE, offset_y);
                    offset_y++;
                }
                if (c.isEthereal) {
                    drawBadge(sb, c, cardHb, StSLib.BADGE_ETHEREAL, offset_y);
                    offset_y++;
                }
                if (c.retain || c.selfRetain) {
                    drawBadge(sb, c, cardHb, StSLib.BADGE_RETAIN, offset_y);
                    offset_y++;
                }
                if (c.purgeOnUse) {
                    drawBadge(sb, c, cardHb, StSLib.BADGE_PURGE, offset_y);
                    offset_y++;
                }
                if (c.exhaust || c.exhaustOnUseOnce) {
                    drawBadge(sb, c, cardHb, StSLib.BADGE_EXHAUST, offset_y);
                    offset_y++;
                }
            }

        }

        private static void drawBadge(SpriteBatch sb, AbstractCard card, Hitbox hb, Texture img, int offset) {
            float badge_w = img.getWidth();
            float badge_h = img.getHeight();
            sb.draw(img, hb.x + hb.width - (badge_w * Settings.scale) * 0.66f, hb.y + hb.height - (badge_h * Settings.scale) * 0.5f - ((offset * (badge_h * Settings.scale)*0.6f)), 0, 0, badge_w * Settings.scale, badge_h * Settings.scale,
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
            Texture badge = null;
            if (tip.header.equalsIgnoreCase("innate"))
            {
                badge = StSLib.BADGE_INNATE;
            }
            else if (tip.header.equalsIgnoreCase("ethereal"))
            {
                badge = StSLib.BADGE_ETHEREAL;
            }
            else if (tip.header.equalsIgnoreCase("retain"))
            {
                badge = StSLib.BADGE_RETAIN;
            }
            else if (tip.header.equalsIgnoreCase("purge"))
            {
                badge = StSLib.BADGE_PURGE;
            }
            else if (tip.header.equalsIgnoreCase("exhaust"))
            {
                badge = StSLib.BADGE_EXHAUST;
            }

            if(badge != null) {
                float badge_w = badge.getWidth();
                float badge_h = badge.getHeight();
                sb.draw(badge, x + ((320.0F - badge_w/2 - 8f) * Settings.scale), y + (-16.0F * Settings.scale), 0, 0, badge_w, badge_h,
                        0.5f * Settings.scale, 0.5f * Settings.scale, 0, 0, 0, (int)badge_w, (int)badge_h, false, false);
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

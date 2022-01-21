package com.evacipated.cardcrawl.mod.stslib.patches;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;
import com.evacipated.cardcrawl.mod.stslib.icons.CustomIconHelper;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DescriptionLine;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import javassist.CtBehavior;

public class CardDescriptionCustomIconsCN {

    @SpirePatch(clz= AbstractCard.class, method="renderDescriptionCN")
    public static class FixDBM
    {
        @SpireInsertPatch(
                locator=Locator.class,
                localvars={"tmp"}
        )
        public static void Insert(AbstractCard __instance, SpriteBatch sb, @ByRef String[] tmp)
        {
            if (tmp[0].length() > 0 && tmp[0].charAt(0) == '[') {
                AbstractCustomIcon icon = CustomIconHelper.getIcon(tmp[0].trim());
                if (icon != null) {
                    tmp[0] = tmp[0].replace("D", "*d").replace("B", "*b").replace("M", "*m");
                }
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception
            {
                Matcher matcher = new Matcher.MethodCallMatcher(String.class, "length");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }
    }

    @SpirePatch(
            clz= SingleCardViewPopup.class,
            method="renderDescriptionCN"
    )
    public static class FixDBMSingleCardView
    {
        @SpireInsertPatch(
                locator=Locator.class,
                localvars={"tmp"}
        )
        public static void Insert(SingleCardViewPopup __instance, SpriteBatch sb, @ByRef String[] tmp)
        {
            if (tmp[0].length() > 0 && tmp[0].charAt(0) == '[') {
                AbstractCustomIcon icon = CustomIconHelper.getIcon(tmp[0].trim());
                if (icon != null) {
                    tmp[0] = tmp[0].replace("D", "*d").replace("B", "*b").replace("M", "*m");
                }
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception
            {
                Matcher matcher = new Matcher.MethodCallMatcher(String.class, "length");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }
    }

    @SpirePatch(
            clz=AbstractCard.class,
            method="initializeDescriptionCN"
    )
    public static class AlterIconDescriptionSize
    {
        private static final float CARD_ENERGY_IMG_WIDTH = 16.0f * Settings.scale;

        @SpireInsertPatch(
                locator=Locator.class,
                localvars={"gl", "word", "sbuilder", "currentWidth", "numLines", "CN_DESC_BOX_WIDTH"}
        )
        public static void Insert(AbstractCard __instance, @ByRef GlyphLayout[] gl, @ByRef String[] word,
                                  @ByRef StringBuilder[] currentLine, @ByRef float[] currentWidth, @ByRef int[] numLines,
                                  float CN_DESC_BOX_WIDTH)
        {
            if (word[0].length() > 0 && word[0].charAt(0) == '[') {
                AbstractCustomIcon icon = CustomIconHelper.getIcon(word[0].trim());
                if (icon != null) {
                    gl[0].setText(FontHelper.cardDescFont_N, " ");
                    gl[0].width = CARD_ENERGY_IMG_WIDTH;
                    currentLine[0].append(" ").append(word[0]).append(" ");
                    if (currentWidth[0] + gl[0].width > CN_DESC_BOX_WIDTH) {
                        ++numLines[0];
                        __instance.description.add(new DescriptionLine(currentLine[0].toString(), currentWidth[0]));
                        currentLine[0] = new StringBuilder();
                        currentWidth[0] = gl[0].width;
                    } else {
                    }
                    word[0] = "";
                }
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception
            {
                Matcher matcher = new Matcher.MethodCallMatcher(String.class, "trim");
                int[] lines = LineFinder.findAllInOrder(ctBehavior, matcher);
                return new int[]{lines[lines.length-1]}; // Only last occurrence
            }
        }
    }
}

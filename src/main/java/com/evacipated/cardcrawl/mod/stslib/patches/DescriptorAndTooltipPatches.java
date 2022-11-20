package com.evacipated.cardcrawl.mod.stslib.patches;

import basemod.abstracts.CustomCard;
import basemod.helpers.TooltipInfo;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.RenderCardDescriptors;
import basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup.RenderCardDescriptorsSCV;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.blockmods.AbstractBlockModifier;
import com.evacipated.cardcrawl.mod.stslib.blockmods.BlockModifierManager;
import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;
import com.evacipated.cardcrawl.mod.stslib.icons.CustomIconHelper;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.DamageModApplyingPower;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.List;

public class DescriptorAndTooltipPatches {
    @SpirePatch2(clz = basemod.patches.com.megacrit.cardcrawl.helpers.TipHelper.FakeKeywords.class, method = "Prefix")
    public static class AddTooltipTop {
        @SpireInsertPatch(locator = Locator1.class, localvars = "tooltips")
        public static void part1(AbstractCard ___card, @ByRef List<TooltipInfo>[] tooltips) {
            if (tooltips[0] == null) {
                tooltips[0] = new ArrayList<>();
            }
            for (AbstractDamageModifier mod : DamageModifierManager.modifiers(___card)) {
                if (mod.getCustomTooltips() != null) {
                    tooltips[0].addAll(mod.getCustomTooltips());
                }
            }
            for (AbstractBlockModifier mod : BlockModifierManager.modifiers(___card)) {
                if (mod.getCustomTooltips() != null) {
                    tooltips[0].addAll(mod.getCustomTooltips());
                }
            }
            for (AbstractCustomIcon icon : CustomIconHelper.iconsOnCard(___card)) {
                if (icon.getCustomTooltips() != null) {
                    tooltips[0].addAll(icon.getCustomTooltips());
                }
            }
            if (AbstractDungeon.player != null && RenderElementsOnCardPatches.validLocation(___card)) {
                for (AbstractPower p : AbstractDungeon.player.powers) {
                    if (p instanceof DamageModApplyingPower && ((DamageModApplyingPower) p).shouldPushMods(null, ___card, DamageModifierManager.modifiers(___card))) {
                        for (AbstractDamageModifier mod : ((DamageModApplyingPower) p).modsToPush(null, ___card, DamageModifierManager.modifiers(___card))) {
                            if (mod.getCustomTooltips() != null) {
                                tooltips[0].addAll(mod.getCustomTooltips());
                            }
                        }
                    }
                }
            }
        }

        private static class Locator1 extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(CustomCard.class, "getCustomTooltipsTop");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }

        @SpireInsertPatch(locator = Locator2.class, localvars = "tooltips")
        public static void part2(AbstractCard ___card, @ByRef List<TooltipInfo>[] tooltips) {
            if (tooltips[0] == null) {
                tooltips[0] = new ArrayList<>();
            }
            for (AbstractDamageModifier mod : DamageModifierManager.modifiers(___card)) {
                tooltips[0].addAll(mod.getCustomTooltips());
            }
            for (AbstractBlockModifier mod : BlockModifierManager.modifiers(___card)) {
                tooltips[0].addAll(mod.getCustomTooltips());
            }
            for (AbstractCustomIcon icon : CustomIconHelper.iconsOnCard(___card)) {
                if (icon.getCustomTooltips() != null) {
                    tooltips[0].addAll(icon.getCustomTooltips());
                }
            }
            if (AbstractDungeon.player != null && RenderElementsOnCardPatches.validLocation(___card)) {
                for (AbstractPower p : AbstractDungeon.player.powers) {
                    if (p instanceof DamageModApplyingPower && ((DamageModApplyingPower) p).shouldPushMods(null, ___card, DamageModifierManager.modifiers(___card))) {
                        for (AbstractDamageModifier mod : ((DamageModApplyingPower) p).modsToPush(null, ___card, DamageModifierManager.modifiers(___card))) {
                            if (mod.getCustomTooltips() != null) {
                                tooltips[0].addAll(mod.getCustomTooltips());
                            }
                        }
                    }
                }
            }
        }

        private static class Locator2 extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(List.class, "iterator");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                int[] ret = new int[1];
                for (int value : tmp) {
                    ret[0] = value - 1;
                }
                return ret;
            }
        }
    }

    @SpirePatch2(clz = basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup.FakeKeywords.class, method = "InsertBefore")
    public static class AddTooltipTopSCV {
        @SpirePostfixPatch()
        public static void pls(AbstractCard acard, ArrayList<PowerTip>[] t) {
            for (AbstractDamageModifier mod : DamageModifierManager.modifiers(acard)) {
                if (mod.getCustomTooltips() != null) {
                    for (TooltipInfo tip : mod.getCustomTooltips()) {
                        t[0].add(tip.toPowerTip());
                    }
                }
            }
            for (AbstractBlockModifier mod : BlockModifierManager.modifiers(acard)) {
                if (mod.getCustomTooltips() != null) {
                    for (TooltipInfo tip : mod.getCustomTooltips()) {
                        t[0].add(tip.toPowerTip());
                    }
                }
            }
            for (AbstractCustomIcon icon : CustomIconHelper.iconsOnCard(acard)) {
                if (icon.getCustomTooltips() != null) {
                    for (TooltipInfo tip : icon.getCustomTooltips()) {
                        t[0].add(tip.toPowerTip());
                    }
                }
            }
            if (AbstractDungeon.player != null && RenderElementsOnCardPatches.validLocation(acard)) {
                for (AbstractPower p : AbstractDungeon.player.powers) {
                    if (p instanceof DamageModApplyingPower && ((DamageModApplyingPower) p).shouldPushMods(null, acard, DamageModifierManager.modifiers(acard))) {
                        for (AbstractDamageModifier mod : ((DamageModApplyingPower) p).modsToPush(null, acard, DamageModifierManager.modifiers(acard))) {
                            if (mod.getCustomTooltips() != null) {
                                for (TooltipInfo tip : mod.getCustomTooltips()) {
                                    t[0].add(tip.toPowerTip());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SpirePatch(clz = RenderCardDescriptors.Frame.class, method = "Insert")
    public static class AddDescriptorFrame {
        @SpireInsertPatch(locator = Locator.class, localvars = "descriptors")
        public static void pls(AbstractCard __instance, SpriteBatch sb, float x, float y, float[] tOffset, float[] tWidth, @ByRef List<String>[] descriptors) {
            for (AbstractDamageModifier mod : DamageModifierManager.modifiers(__instance)) {
                if (mod.getCardDescriptor() != null) {
                    descriptors[0].add(mod.getCardDescriptor());
                }
            }
            for (AbstractBlockModifier mod : BlockModifierManager.modifiers(__instance)) {
                if (mod.getCardDescriptor() != null) {
                    descriptors[0].add(mod.getCardDescriptor());
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(List.class, "size");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(clz = RenderCardDescriptors.Text.class, method = "Insert")
    public static class AddDescriptorText {
        @SpireInsertPatch(locator = Locator.class, localvars = "descriptors")
        public static void pls(AbstractCard __instance, SpriteBatch sb, String[] text, @ByRef List<String>[] descriptors) {
            for (AbstractDamageModifier mod : DamageModifierManager.modifiers(__instance)) {
                if (mod.getCardDescriptor() != null) {
                    descriptors[0].add(mod.getCardDescriptor());
                }
            }
            for (AbstractBlockModifier mod : BlockModifierManager.modifiers(__instance)) {
                if (mod.getCardDescriptor() != null) {
                    descriptors[0].add(mod.getCardDescriptor());
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(List.class, "size");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = RenderCardDescriptorsSCV.Frame.class, method = "Insert")
    public static class AddDescriptorSCVFrame {
        @SpireInsertPatch(locator = Locator.class, localvars = "descriptors")
        public static void pls(AbstractCard ___card, @ByRef List<String>[] descriptors) {
            for (AbstractDamageModifier mod : DamageModifierManager.modifiers(___card)) {
                if (mod.getCardDescriptor() != null) {
                    descriptors[0].add(mod.getCardDescriptor());
                }
            }
            for (AbstractBlockModifier mod : BlockModifierManager.modifiers(___card)) {
                if (mod.getCardDescriptor() != null) {
                    descriptors[0].add(mod.getCardDescriptor());
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(List.class, "size");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}

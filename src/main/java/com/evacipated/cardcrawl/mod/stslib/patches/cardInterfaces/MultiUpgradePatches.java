package com.evacipated.cardcrawl.mod.stslib.patches.cardInterfaces;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.cards.interfaces.MultiUpgradeCard;
import com.evacipated.cardcrawl.mod.stslib.ui.MultiUpgradeTree;
import com.evacipated.cardcrawl.mod.stslib.util.UpgradeData;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.runHistory.RunHistoryScreen;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.ui.buttons.GridSelectConfirmButton;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class MultiUpgradePatches {

    @SpirePatch(clz = GridCardSelectScreen.class, method = SpirePatch.CLASS)
    public static class MultiSelectFields {
        public static SpireField<ArrayList<AbstractCard>> previewCards = new SpireField<>(ArrayList::new);
        public static SpireField<Boolean> waitingForUpgradeSelection = new SpireField<>(() -> false);
        public static SpireField<Integer> chosenIndex = new SpireField<>(() -> -1);
    }

    @SpirePatch(clz = AbstractCard.class, method = SpirePatch.CLASS)
    public static class MultiUpgradeFields {
        public static SpireField<ArrayList<UpgradeData>> upgrades = new SpireField<>(ArrayList::new);
        public static SpireField<Integer> upgradeIndex = new SpireField<>(() -> -1);
        public static SpireField<Boolean> glowRed = new SpireField<>(() -> false);
    }

    @SpirePatch(clz = AbstractCard.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {String.class, String.class, String.class, int.class, String.class, AbstractCard.CardType.class, AbstractCard.CardColor.class, AbstractCard.CardRarity.class, AbstractCard.CardTarget.class, DamageInfo.DamageType.class})
    @SpirePatch(clz = AbstractCard.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class})
    public static class AddUpgrades {
        @SpirePostfixPatch
        public static void plz(AbstractCard __instance) {
            if (__instance instanceof MultiUpgradeCard) {
                ((MultiUpgradeCard) __instance).addUpgrades();
            }
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "canUpgrade")
    public static class AllowUpgrades {
        @SpirePrefixPatch
        public static SpireReturn<?> canUpgrade(AbstractCard __instance) {
            if (__instance instanceof MultiUpgradeCard) {
                return SpireReturn.Return(((MultiUpgradeCard) __instance).canPerformUpgrade());
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = CardLibrary.class, method = "getCopy", paramtypez = {String.class, int.class, int.class})
    public static class SaveMultiUpgrades {
        static int muxedUpgrades = 0;
        @SpireInsertPatch(rloc = 9, localvars = {"retVal"})
        public static void getMuxedUpgrades(String key, @ByRef int[] upgradeTime, int misc, AbstractCard retVal) {
            if (retVal instanceof MultiUpgradeCard) {
                muxedUpgrades = upgradeTime[0];
                upgradeTime[0] = 0;
            }
        }
        @SpireInsertPatch(rloc = 13, localvars = {"retVal"})
        public static void doMuxedUpgrades(String key, @ByRef int[] upgradeTime, int misc, AbstractCard retVal) {
            if (retVal instanceof MultiUpgradeCard) {
                for (int i = 0 ; i < 32 ; i++) {
                    if ((muxedUpgrades & (1 << i)) != 0) {
                        MultiUpgradeFields.upgradeIndex.set(retVal, i);
                        retVal.upgrade();
                    }
                }
            }
        }
    }

    @SpirePatch2(clz = RunHistoryScreen.class, method = "cardForName")
    public static class SaveMultiUpgradesRunHistory {
        static int muxedUpgrades = 0;
        static AbstractCard multiUpCard = null;
        @SpireInsertPatch(locator = Locator.class, localvars = {"card","upgrades"})
        public static void getMuxedUpgrades2(AbstractCard card, @ByRef int[] upgrades) {
            if (card instanceof MultiUpgradeCard) {
                muxedUpgrades = upgrades[0];
                multiUpCard = card;
                upgrades[0] = 0;
            }
        }
        @SpirePostfixPatch
        public static void doMuxedUpgrades2() {
            if (multiUpCard instanceof MultiUpgradeCard) {
                for (int i = 0 ; i < 32 ; i++) {
                    if ((muxedUpgrades & (1 << i)) != 0) {
                        MultiUpgradeFields.upgradeIndex.set(multiUpCard, i);
                        multiUpCard.upgrade();
                    }
                }
            }
            multiUpCard = null;
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher m = new Matcher.MethodCallMatcher(AbstractCard.class, "makeCopy");
                return new int[]{LineFinder.findInOrder(ctBehavior, m)[0]+1};
            }
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "makeStatEquivalentCopy"
    )
    public static class CopiesRetainMultiUpgrade {
        static int muxedUpgrades = 0;
        @SpireInsertPatch(rloc = 2, localvars = {"card"})
        public static void getUpgrades(AbstractCard __instance, AbstractCard card) {
            if (__instance instanceof MultiUpgradeCard) {
                muxedUpgrades = __instance.timesUpgraded;
                __instance.timesUpgraded = 0;
            }
        }

        @SpireInsertPatch(rloc = 6, localvars = {"card"})
        public static void doUpgrades(AbstractCard __instance, AbstractCard card) {
            if (__instance instanceof MultiUpgradeCard) {
                __instance.timesUpgraded = muxedUpgrades;
                for (int i = 0 ; i < 32 ; i++) {
                    if ((muxedUpgrades & (1 << i)) != 0) {
                        MultiUpgradeFields.upgradeIndex.set(card, i);
                        card.upgrade();
                    }
                }
            }
        }
    }

    @SpirePatch(clz = GridCardSelectScreen.class, method = "update")
    public static class GetMultiUpgrades {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(GridCardSelectScreen __instance) throws Exception {
            AbstractCard c = BranchingUpgradesPatch.getHoveredCard();
            if (c instanceof MultiUpgradeCard) {
                MultiUpgradeTree.open(c, true);
                MultiSelectFields.waitingForUpgradeSelection.set(__instance, true);
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "makeStatEquivalentCopy");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(clz = GridCardSelectScreen.class, method = "cancelUpgrade")
    public static class CancelUpgrade {
        public static void Prefix(GridCardSelectScreen __instance) {
            MultiSelectFields.waitingForUpgradeSelection.set(__instance, false);
            MultiSelectFields.chosenIndex.set(__instance, -1);
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "update")
    public static class SelectMultiUpgrade {
        public static void Postfix(AbstractCard __instance) {
            AbstractCard hovered = BranchingUpgradesPatch.getHoveredCard();
            if (__instance != hovered && hovered instanceof MultiUpgradeCard && AbstractDungeon.screen == AbstractDungeon.CurrentScreen.GRID && AbstractDungeon.gridSelectScreen.forUpgrade && __instance.hb.hovered && InputHelper.justClickedLeft) {
                MultiUpgradeTree.selectCard(__instance);
            }
        }
    }

    @SpirePatch(clz = GridCardSelectScreen.class, method = "update")
    public static class ConfirmUpgrade {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(GridCardSelectScreen __instance) {
            AbstractCard hoveredCard = BranchingUpgradesPatch.getHoveredCard();
            if (hoveredCard instanceof MultiUpgradeCard) {
                MultiUpgradeFields.upgradeIndex.set(hoveredCard, MultiSelectFields.chosenIndex.get(__instance));
                MultiSelectFields.chosenIndex.set(__instance, -1);
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "closeCurrentScreen");
                int[] found = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[]{found[found.length - 1]};
            }
        }
    }

    @SpirePatch(clz = GridCardSelectScreen.class, method = "renderArrows")
    public static class RenderSplitArrows {
        @SpirePrefixPatch
        public static SpireReturn<?> downwardArrows(GridCardSelectScreen __instance, SpriteBatch sb, @ByRef float[] ___arrowTimer, @ByRef float[] ___arrowScale1, @ByRef float[] ___arrowScale2, @ByRef float[] ___arrowScale3) {
            AbstractCard card = BranchingUpgradesPatch.getHoveredCard();
            if (__instance.forUpgrade && card instanceof MultiUpgradeCard) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = GridSelectConfirmButton.class, method = "render")
    public static class MultiUpgradeConfirmRender {
        public static SpireReturn<?> Prefix(GridSelectConfirmButton __instance, SpriteBatch sb) {
            AbstractCard c = BranchingUpgradesPatch.getHoveredCard();
            return MultiSelectFields.waitingForUpgradeSelection.get(AbstractDungeon.gridSelectScreen) && c instanceof MultiUpgradeCard ? SpireReturn.Return(null) : SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = GridSelectConfirmButton.class, method = "update")
    public static class MultiUpgradeConfirmUpdate {
        public static SpireReturn<?> Prefix(GridSelectConfirmButton __instance) {
            AbstractCard c = BranchingUpgradesPatch.getHoveredCard();
            return MultiSelectFields.waitingForUpgradeSelection.get(AbstractDungeon.gridSelectScreen) && c instanceof MultiUpgradeCard ? SpireReturn.Return(null) : SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = GridCardSelectScreen.class, method = "render")
    public static class HideGhostCard {
        @SpireInstrumentPatch
        public static ExprEditor plz() {
            return new ExprEditor() {
                int count = 0;
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (count == 0 && m.getMethodName().equals("render") && m.getClassName().equals(AbstractCard.class.getName())) {
                        count++;
                        m.replace("if ("+MultiUpgradePatches.HideGhostCard.class.getName()+".renderTheCard()) {$_ = $proceed($$);}");
                    }
                }
            };
        }

        @SpireInstrumentPatch
        public static ExprEditor plz2() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("renderHoverShadow") && m.getClassName().equals(AbstractCard.class.getName())) {
                        m.replace("if ("+MultiUpgradePatches.HideGhostCard.class.getName()+".renderTheCard()) {$_ = $proceed($$);}");
                    }
                }
            };
        }

        public static boolean renderTheCard() {
            if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.GRID && AbstractDungeon.gridSelectScreen.forUpgrade && AbstractDungeon.gridSelectScreen.confirmScreenUp && BranchingUpgradesPatch.getHoveredCard() instanceof MultiUpgradeCard) {
                return false;
            }
            return true;
        }
    }

    @SpirePatch(clz = GridCardSelectScreen.class, method = "render")
    public static class RenderMultiUpgrade {
        public static ExprEditor Instrument() {
            return new ExprEditor() {// 137
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("renderArrows")) {
                        m.replace("$_ = $proceed($$);if (" + RenderMultiUpgrade.class.getName() + ".Do(this, sb).isPresent()) {return;}");
                    }
                }// 147
            };
        }

        public static SpireReturn<?> Do(GridCardSelectScreen __instance, SpriteBatch sb) {
            AbstractCard c = BranchingUpgradesPatch.getHoveredCard();
            if (__instance.forUpgrade && c instanceof MultiUpgradeCard) {
                MultiUpgradeTree.render(sb);

                if (__instance.forUpgrade || __instance.forTransform || __instance.forPurge || __instance.isJustForConfirming || __instance.anyNumber) {
                    __instance.confirmButton.render(sb);
                }

                CardGroup targetGroup = ReflectionHacks.getPrivate(__instance, GridCardSelectScreen.class, "targetGroup");
                String tipMsg = ReflectionHacks.getPrivate(__instance, GridCardSelectScreen.class, "tipMsg");
                if (!__instance.isJustForConfirming || targetGroup.size() > 5) {
                    FontHelper.renderDeckViewTip(sb, tipMsg, 96.0F * Settings.scale, Settings.CREAM_COLOR);
                }
                return SpireReturn.Return(null);
            } else {
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch(clz = GridCardSelectScreen.class, method = "update")
    public static class UpdatePreviewCards {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(GridCardSelectScreen __instance) {
            AbstractCard c = BranchingUpgradesPatch.getHoveredCard();
            if (__instance.forUpgrade && c instanceof MultiUpgradeCard) {
                MultiUpgradeTree.update();
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "update");
                return new int[]{LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher)[1]};
            }
        }
    }

    @SpirePatch(clz = GridCardSelectScreen.class, method = "update")
    public static class ForceNormalUpgrade {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(GridCardSelectScreen __instance) {
            if (__instance.upgradePreviewCard instanceof MultiUpgradeCard) {
                MultiUpgradeFields.upgradeIndex.set(__instance.upgradePreviewCard, -1);
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    //this used to be a patch, but is now called in SingleCardViewRenderPatch.postfixFix to guarantee order.
    public static class RenderTreeSCV {
        public static void renderTree(SingleCardViewPopup __instance, SpriteBatch sb, AbstractCard ___card) {
            if (___card instanceof MultiUpgradeCard && SingleCardViewPopup.isViewingUpgrade) {
                sb.setColor(new Color(0.0F, 0.0F, 0.0F, 0.8F));// 809
                sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float)Settings.WIDTH, (float)Settings.HEIGHT - 64.0F * Settings.scale);// 810
                MultiUpgradeTree.render(sb);
            }
        }
    }

    @SpirePatch2(clz = SingleCardViewPopup.class, method = "open", paramtypez = {AbstractCard.class})
    @SpirePatch2(clz = SingleCardViewPopup.class, method = "open", paramtypez = {AbstractCard.class, CardGroup.class})
    public static class LoadTree {
        @SpirePrefixPatch
        public static void load(SingleCardViewPopup __instance, AbstractCard card) throws Exception {
            if (card instanceof MultiUpgradeCard) {
                MultiUpgradeTree.open(card, true);
            }
        }
    }

    @SpirePatch(clz = SingleCardViewPopup.class, method = "update")
    public static class UpdateTree {
        @SpirePostfixPatch
        public static void renderTree(SingleCardViewPopup __instance, AbstractCard ___card) {
            if (___card instanceof MultiUpgradeCard && SingleCardViewPopup.isViewingUpgrade) {
                MultiUpgradeTree.update();
            }
        }
    }

    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderTips")
    public static class TipsBeGone {
        @SpirePrefixPatch
        public static SpireReturn<?> stop(SingleCardViewPopup __instance, SpriteBatch sb, AbstractCard ___card) {
            if (___card instanceof MultiUpgradeCard && SingleCardViewPopup.isViewingUpgrade) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = AbstractCard.class, method = "renderCardTip")
    public static class StopMakingCopiesToRender {
        static Field cardField;
        @SpireInstrumentPatch
        public static ExprEditor plz() {
            return new ExprEditor() {
                @Override
                public void edit(FieldAccess f) throws CannotCompileException {
                    if (f.getClassName().equals(SingleCardViewPopup.class.getName()) && f.getFieldName().equals("isViewingUpgrade")) {
                        f.replace("$_ ="+ StopMakingCopiesToRender.class.getName()+".checkMU($proceed($$));");
                    }
                }
            };
        }

        public static boolean checkMU(boolean retval) {
            try {
                if (cardField == null) {
                    cardField = SingleCardViewPopup.class.getDeclaredField("card");
                }
                cardField.setAccessible(true);
                return retval && !(cardField.get(CardCrawlGame.cardPopup) instanceof MultiUpgradeCard);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return retval;
        }
    }
}

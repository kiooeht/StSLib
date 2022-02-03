package com.evacipated.cardcrawl.mod.stslib.patches.cardInterfaces;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.cards.interfaces.BranchingUpgradesCard;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.ui.buttons.GridSelectConfirmButton;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class BranchingUpgradesPatch {

    @SpirePatch(
            clz = AbstractCard.class,
            method = SpirePatch.CLASS
    )
    public static class BranchingUpgradeField {
        public static SpireField<BranchingUpgradesCard.UpgradeType> upgradeType = new SpireField<>(() -> BranchingUpgradesCard.UpgradeType.RANDOM_UPGRADE);
    }

    @SpirePatch(
            clz = GridCardSelectScreen.class,
            method = SpirePatch.CLASS
    )
    public static class BranchSelectFields {
        //BranchSelectFields
        public static SpireField<AbstractCard> branchUpgradePreviewCard = new SpireField<>(() -> null);
        //WaitingForBranchUpgradeSelection
        public static SpireField<Boolean> waitingForBranchUpgradeSelection = new SpireField<>(() -> false);
        //IsBranchUpgrading
        public static SpireField<Boolean> isBranchUpgrading = new SpireField<>(() -> false);
    }

    @SpirePatch(
            clz = GridCardSelectScreen.class,
            method = "update"
    )
    public static class GetBranchingUpgrade {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(GridCardSelectScreen __instance) {
            AbstractCard c = getHoveredCard();
            if (c instanceof BranchingUpgradesCard) {
                AbstractCard previewCard = c.makeStatEquivalentCopy();
                ((BranchingUpgradesCard) previewCard).doBranchUpgrade();
                previewCard.displayUpgrades();
                BranchSelectFields.branchUpgradePreviewCard.set(__instance, previewCard);
                BranchSelectFields.waitingForBranchUpgradeSelection.set(__instance, true);
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "makeStatEquivalentCopy");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = GridCardSelectScreen.class,
            method = "update"
    )
    public static class ForceNormalUpgrade {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(GridCardSelectScreen __instance) {
            if (__instance.upgradePreviewCard instanceof BranchingUpgradesCard) {
                ((BranchingUpgradesCard) __instance.upgradePreviewCard).setUpgradeType(BranchingUpgradesCard.UpgradeType.NORMAL_UPGRADE);
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = GridCardSelectScreen.class,
            method = "update"
    )
    public static class StupidFuckingUpdateBullshitImSoMadDontChangeThisClassNameKio {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(GridCardSelectScreen __instance) {
            if (BranchSelectFields.branchUpgradePreviewCard.get(__instance) != null) {
                BranchSelectFields.branchUpgradePreviewCard.get(__instance).update();
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "update");
                return new int[]{LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher)[1]};
            }
        }
    }

    public static ArrayList<AbstractCard> cardList = new ArrayList<>();

    @SpirePatch(
            clz = GridCardSelectScreen.class,
            method = "render"
    )
    public static class RenderBranchingUpgrade {
        // Instrument to insert the patch call after renderArrows()
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException
                {
                    if (m.getMethodName().equals("renderArrows")) {
                        m.replace("$_ = $proceed($$);" +
                                "if (" + RenderBranchingUpgrade.class.getName() + ".Do(this, sb).isPresent()) {" +
                                "return;" +
                                "}");
                    }
                }
            };
        }
        public static SpireReturn Do(GridCardSelectScreen __instance, SpriteBatch sb) {
            AbstractCard c = getHoveredCard();
            if (__instance.forUpgrade && c instanceof BranchingUpgradesCard) {
                cardList.clear();
                AbstractCard branchUpgradedCard = BranchSelectFields.branchUpgradePreviewCard.get(__instance);
                c.current_x = (Settings.WIDTH * 0.36F);
                c.current_y = (Settings.HEIGHT / 2.0F);
                c.target_x = (Settings.WIDTH * 0.36F);
                c.target_y = (Settings.HEIGHT / 2.0F);
                c.render(sb);
                c.updateHoverLogic();
                c.hb.resize(0, 0);
                if (__instance.upgradePreviewCard.hb.hovered) {
                    __instance.upgradePreviewCard.drawScale = 1;
                } else {
                    __instance.upgradePreviewCard.drawScale = 0.9F;
                }
                __instance.upgradePreviewCard.current_x = (Settings.WIDTH * 0.63F);
                __instance.upgradePreviewCard.current_y = (Settings.HEIGHT * 0.75F - (50 * Settings.scale));
                __instance.upgradePreviewCard.target_x = (Settings.WIDTH * 0.63F);
                __instance.upgradePreviewCard.target_y = (Settings.HEIGHT * 0.75F - (50 * Settings.scale));
                __instance.upgradePreviewCard.render(sb);
                __instance.upgradePreviewCard.updateHoverLogic();
                __instance.upgradePreviewCard.renderCardTip(sb);
                cardList.add(__instance.upgradePreviewCard);
                if (branchUpgradedCard.hb.hovered) {
                    branchUpgradedCard.drawScale = 1;
                } else {
                    branchUpgradedCard.drawScale = 0.9F;
                }
                branchUpgradedCard.current_x = (Settings.WIDTH * 0.63F);
                branchUpgradedCard.current_y = (Settings.HEIGHT / 4.0F + (50 * Settings.scale));
                branchUpgradedCard.target_x = (Settings.WIDTH * 0.63F);
                branchUpgradedCard.target_y = (Settings.HEIGHT / 4.0F + (50 * Settings.scale));
                branchUpgradedCard.render(sb);
                branchUpgradedCard.updateHoverLogic();
                branchUpgradedCard.renderCardTip(sb);
                cardList.add(branchUpgradedCard);
                if ((__instance.forUpgrade) || (__instance.forTransform) || (__instance.forPurge) || (__instance.isJustForConfirming) || (__instance.anyNumber)) {
                    __instance.confirmButton.render(sb);
                }
                CardGroup targetGroup = (CardGroup) ReflectionHacks.getPrivate(__instance, GridCardSelectScreen.class, "targetGroup");
                String tipMsg = (String) ReflectionHacks.getPrivate(__instance, GridCardSelectScreen.class, "tipMsg");
                if ((!__instance.isJustForConfirming) || (targetGroup.size() > 5)) {
                    FontHelper.renderDeckViewTip(sb, tipMsg, 96.0F * Settings.scale, Settings.CREAM_COLOR);
                }
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }


    @SpirePatch(
            clz = GridSelectConfirmButton.class,
            method = "render"
    )
    public static class BranchUpgradeConfirm {
        public static SpireReturn Prefix(GridSelectConfirmButton __instance, SpriteBatch sb) {
            AbstractCard c = getHoveredCard();
            if (BranchSelectFields.waitingForBranchUpgradeSelection.get(AbstractDungeon.gridSelectScreen) && c instanceof BranchingUpgradesCard) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = GridCardSelectScreen.class,
            method = "renderArrows"
    )
    public static class RenderSplitArrows {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                private int count = 0;
                @Override
                public void edit(MethodCall m) throws CannotCompileException
                {
                    if (m.getClassName().equals(SpriteBatch.class.getName()) && m.getMethodName().equals("draw")) {
                        if (count != 0) {
                            m.replace("if (forUpgrade && hoveredCard instanceof " + BranchingUpgradesCard.class.getName() + ") {" +
                                    "$10 = 45f;" +
                                    "$3 += 64f * " + Settings.class.getName() + ".scale *" + count + ";" +
                                    "$_ = $proceed($$);" +
                                    "$10 = -45f;" +
                                    "$3 -= 2 * 64f * " + Settings.class.getName() + ".scale *" + count + ";" +
                                    "}" +
                                    "$_ = $proceed($$);");
                        }
                        ++count;
                    }
                }
            };
        }
    }

    @SpirePatch(
            clz = GridCardSelectScreen.class,
            method = "cancelUpgrade"
    )
    public static class CancelUpgrade {
        public static void Prefix(GridCardSelectScreen __instance) {
            BranchSelectFields.waitingForBranchUpgradeSelection.set(__instance, false);
            BranchSelectFields.isBranchUpgrading.set(__instance, false);
        }
    }

    @SpirePatch(
            clz = GridCardSelectScreen.class,
            method = "update"
    )
    public static class ConfirmUpgrade {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(GridCardSelectScreen __instance) {
            AbstractCard hoveredCard = getHoveredCard();
            if (hoveredCard instanceof BranchingUpgradesCard) {
                if (BranchSelectFields.isBranchUpgrading.get(__instance)) {
                    ((BranchingUpgradesCard) hoveredCard).setUpgradeType(BranchingUpgradesCard.UpgradeType.BRANCH_UPGRADE);
                } else {
                    ((BranchingUpgradesCard) hoveredCard).setUpgradeType(BranchingUpgradesCard.UpgradeType.NORMAL_UPGRADE);
                }
                BranchSelectFields.isBranchUpgrading.set(__instance, false);
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "closeCurrentScreen");
                int[] found = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[]{found[found.length - 1]}; // last
            }
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "update"
    )
    public static class SelectBranchedUpgrade {
        public static void Postfix(AbstractCard __instance) {
            if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.GRID && AbstractDungeon.gridSelectScreen.forUpgrade) {
                if (__instance.hb.hovered && InputHelper.justClickedLeft) {
                    if (__instance.timesUpgraded < 0) {
                        BranchSelectFields.isBranchUpgrading.set(AbstractDungeon.gridSelectScreen, true);
                    } else {
                        BranchSelectFields.isBranchUpgrading.set(AbstractDungeon.gridSelectScreen, false);
                    }

                    if (__instance instanceof BranchingUpgradesCard) {
                        __instance.beginGlowing();
                        cardList.forEach(c -> {
                            if (c != __instance) c.stopGlowing();
                        });
                    }

                    BranchSelectFields.waitingForBranchUpgradeSelection.set(AbstractDungeon.gridSelectScreen, false);
                }
            }
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "makeStatEquivalentCopy"
    )
    public static class CopiesRetainBranchUpgrade {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"card"}
        )
        public static void Insert(AbstractCard __instance, AbstractCard card) {
            BranchingUpgradeField.upgradeType.set(card, BranchingUpgradeField.upgradeType.get(__instance));
            if (__instance.timesUpgraded < 0 && card instanceof BranchingUpgradesCard) {
                BranchingUpgradesCard c = (BranchingUpgradesCard) card;
                for (int i = 0; i > __instance.timesUpgraded; i--) {
                    c.doBranchUpgrade();
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "timesUpgraded");
                return LineFinder.findAllInOrder(ctBehavior, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = CardLibrary.class,
            method = "getCopy",
            paramtypez = {
                    String.class,
                    int.class,
                    int.class
            }
    )
    public static class SaveBranchingUpgrades {
        @SpireInsertPatch(
                rloc = 9,
                localvars = {"retVal"}
        )
        public static void Insert(String key, @ByRef int[] upgradeTime, int misc, AbstractCard retVal) {
            if (retVal instanceof BranchingUpgradesCard) {
                if (upgradeTime[0] < 0) {
                    upgradeTime[0] *= -1;
                    ((BranchingUpgradesCard) retVal).setUpgradeType(BranchingUpgradesCard.UpgradeType.BRANCH_UPGRADE);
                } else if (upgradeTime[0] > 0) {
                    ((BranchingUpgradesCard) retVal).setUpgradeType(BranchingUpgradesCard.UpgradeType.NORMAL_UPGRADE);
                }
            }
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "upgradeName"
    )
    public static class AvoidSomeFractalsOrSomethingIGuess {
        public static void Postfix(AbstractCard __instance) {
            if (__instance instanceof BranchingUpgradesCard) {
                if (((BranchingUpgradesCard) __instance).isBranchUpgrade()) {
                    __instance.timesUpgraded -= 2;
                    String tmp = __instance.name.substring(__instance.name.length() - 1);
                    if (tmp.equals("+")) {
                        __instance.name = __instance.name.substring(0, __instance.name.length() - 1) + "*";
                    }
                }
            }
        }
    }

    // Add second button for branch upgrade to SingleCardViewPopup
    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = SpirePatch.CLASS
    )
    public static class BranchUpgradeButton {
        public static SpireField<Hitbox> branchUpgradeHb = new SpireField<>(() -> new Hitbox(250f * Settings.scale, 80f * Settings.scale));
        public static SpireField<Boolean> isViewingBranchUpgrade = new SpireField<>(() -> false);
    }

    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "render"
    )
    public static class DoBranchUpgradePreview {
        @SpireInsertPatch(
                locator = NormalLocator.class
        )
        public static void InsertNormalPreview(SingleCardViewPopup __instance, SpriteBatch sb, AbstractCard ___card) {
            if (___card instanceof BranchingUpgradesCard && ((BranchingUpgradesCard) ___card).getUpgradeType() == BranchingUpgradesCard.UpgradeType.RANDOM_UPGRADE) {
                ((BranchingUpgradesCard) ___card).setUpgradeType(BranchingUpgradesCard.UpgradeType.NORMAL_UPGRADE);
            }
        }
        @SpireInsertPatch(
                locator = BranchLocator.class,
                localvars = {"copy"}
        )
        public static void InsertBranchPreview(SingleCardViewPopup __instance, SpriteBatch sb, AbstractCard ___card, @ByRef AbstractCard[] copy) {
            if (___card instanceof BranchingUpgradesCard) {
                if (BranchUpgradeButton.isViewingBranchUpgrade.get(__instance) && ((BranchingUpgradesCard) ___card).getUpgradeType() == BranchingUpgradesCard.UpgradeType.RANDOM_UPGRADE) {
                    copy[0] = ___card.makeStatEquivalentCopy();
                    ((BranchingUpgradesCard) ___card).doBranchUpgrade();
                    ___card.displayUpgrades();
                }
            }
        }

        private static class NormalLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }

        private static class BranchLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(SpriteBatch.class, "setColor");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "open",
            paramtypez = {AbstractCard.class, CardGroup.class}
    )
    public static class MoveBranchUpgradeButton1 {
        public static void Postfix(SingleCardViewPopup __instance, AbstractCard card, CardGroup group, Hitbox ___upgradeHb) {
            if (card instanceof BranchingUpgradesCard) {
                Hitbox branchUpgradeHb = BranchUpgradeButton.branchUpgradeHb.get(__instance);
                try {
                    Method canToggleBetaArt = SingleCardViewPopup.class.getDeclaredMethod("canToggleBetaArt");
                    canToggleBetaArt.setAccessible(true);
                    Method allowUpgradePreview = SingleCardViewPopup.class.getDeclaredMethod("allowUpgradePreview");
                    allowUpgradePreview.setAccessible(true);
                    if ((boolean) canToggleBetaArt.invoke(__instance)) {
                        if ((boolean) allowUpgradePreview.invoke(__instance)) {
                            ___upgradeHb.move(Settings.WIDTH / 2f - 300f * Settings.scale, 70f * Settings.scale);
                            branchUpgradeHb.move(Settings.WIDTH / 2f - 40f * Settings.scale, 70f * Settings.scale);
                        }
                    } else {
                        ___upgradeHb.move(Settings.WIDTH / 2f + 250f * Settings.scale, 70f * Settings.scale);
                        branchUpgradeHb.move(Settings.WIDTH / 2f - 250f * Settings.scale, 70f * Settings.scale);
                    }
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "open",
            paramtypez = {AbstractCard.class}
    )
    public static class MoveBranchUpgradeButton2 {
        public static void Postfix(SingleCardViewPopup __instance, AbstractCard card, Hitbox ___upgradeHb) {
            if (card instanceof BranchingUpgradesCard) {
                Hitbox branchUpgradeHb = BranchUpgradeButton.branchUpgradeHb.get(__instance);
                try {
                    Method canToggleBetaArt = SingleCardViewPopup.class.getDeclaredMethod("canToggleBetaArt");
                    canToggleBetaArt.setAccessible(true);
                    if ((boolean) canToggleBetaArt.invoke(__instance)) {
                        ___upgradeHb.move(Settings.WIDTH / 2f - 300f * Settings.scale, 70f * Settings.scale);
                        branchUpgradeHb.move(Settings.WIDTH / 2f - 40f * Settings.scale, 70f * Settings.scale);
                    } else {
                        ___upgradeHb.move(Settings.WIDTH / 2f + 250f * Settings.scale, 70f * Settings.scale);
                        branchUpgradeHb.move(Settings.WIDTH / 2f - 250f * Settings.scale, 70f * Settings.scale);
                    }
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "openPrev"
    )
    public static class OpenPrev {
        private static boolean save = false;

        public static void Prefix(SingleCardViewPopup __instance) {
            save = BranchUpgradeButton.isViewingBranchUpgrade.get(__instance);
        }

        public static void Postfix(SingleCardViewPopup __instance, AbstractCard ___prevCard) {
            if (!(___prevCard instanceof BranchingUpgradesCard)) {
                save = false;
            }
            BranchUpgradeButton.isViewingBranchUpgrade.set(__instance, save);
            save = false;
        }
    }

    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "openNext"
    )
    public static class OpenNext {
        private static boolean save = false;

        public static void Prefix(SingleCardViewPopup __instance) {
            save = BranchUpgradeButton.isViewingBranchUpgrade.get(__instance);
        }

        public static void Postfix(SingleCardViewPopup __instance, AbstractCard ___nextCard) {
            if (!(___nextCard instanceof BranchingUpgradesCard)) {
                save = false;
            }
            BranchUpgradeButton.isViewingBranchUpgrade.set(__instance, save);
            save = false;
        }
    }

    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "close"
    )
    public static class CloseDisableBranchPreview {
        public static void Postfix(SingleCardViewPopup __instance) {
            BranchUpgradeButton.isViewingBranchUpgrade.set(__instance, false);
        }
    }

    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "updateUpgradePreview"
    )
    public static class UpdateBranchUpgradeButton {
        public static void Postfix(SingleCardViewPopup __instance, AbstractCard ___card) {
            if (SingleCardViewPopup.isViewingUpgrade) {
                BranchUpgradeButton.isViewingBranchUpgrade.set(__instance, false);
            }

            if (___card instanceof BranchingUpgradesCard) {
                Hitbox branchUpgradeHb = BranchUpgradeButton.branchUpgradeHb.get(__instance);

                branchUpgradeHb.update();
                if (branchUpgradeHb.hovered && InputHelper.justClickedLeft) {
                    branchUpgradeHb.clickStarted = true;
                }

                if (branchUpgradeHb.clicked) {
                    branchUpgradeHb.clicked = false;
                    SingleCardViewPopup.isViewingUpgrade = false;
                    BranchUpgradeButton.isViewingBranchUpgrade.set(__instance, !BranchUpgradeButton.isViewingBranchUpgrade.get(__instance));
                }
            }
        }
    }

    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "updateInput"
    )
    public static class StopClosingOnBranchUpgradeButton {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                private boolean accessedUpgradeHb = false;
                @Override
                public void edit(FieldAccess f) throws CannotCompileException
                {
                    if (accessedUpgradeHb && f.getFieldName().equals("hovered")) {
                        f.replace("$_ = $proceed($$) || ((" + Hitbox.class.getName() + ") " + BranchUpgradeButton.class.getName() + ".branchUpgradeHb.get(this)).hovered;");
                        accessedUpgradeHb = false;
                    } else if (f.getFieldName().equals("upgradeHb")) {
                        accessedUpgradeHb = true;
                    }
                }
            };
        }
    }

    private static UIStrings uiStrings = null;
    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "renderUpgradeViewToggle"
    )
    public static class RenderBranchUpgradeButton {
        public static void Postfix(SingleCardViewPopup __instance, SpriteBatch sb, AbstractCard ___card) {
            if (___card instanceof BranchingUpgradesCard) {
                if (uiStrings == null) {
                    uiStrings = CardCrawlGame.languagePack.getUIString("stslib:SingleCardViewPopup");
                }

                Hitbox branchUpgradeHb = BranchUpgradeButton.branchUpgradeHb.get(__instance);

                sb.setColor(Color.WHITE);
                sb.draw(
                        ImageMaster.CHECKBOX,
                        branchUpgradeHb.cX - 80f * Settings.scale - 32f,
                        branchUpgradeHb.cY - 32f,
                        32f, 32f,
                        64f, 64f,
                        Settings.scale, Settings.scale,
                        0f,
                        0, 0,
                        64, 64,
                        false, false
                );
                Color fontColor = Settings.GOLD_COLOR;
                if (branchUpgradeHb.hovered) {
                    fontColor = Settings.BLUE_TEXT_COLOR;
                }
                FontHelper.renderFont(
                        sb,
                        FontHelper.cardTitleFont,
                        uiStrings.TEXT[0],
                        branchUpgradeHb.cX - 45f * Settings.scale,
                        branchUpgradeHb.cY + 10f * Settings.scale,
                        fontColor
                );
                if (BranchUpgradeButton.isViewingBranchUpgrade.get(__instance)) {
                    sb.setColor(Color.WHITE);
                    sb.draw(
                            ImageMaster.TICK,
                            branchUpgradeHb.cX - 80f * Settings.scale - 32f,
                            branchUpgradeHb.cY - 32f,
                            32f, 32f,
                            64f, 64f,
                            Settings.scale, Settings.scale,
                            0f,
                            0, 0,
                            64, 64,
                            false, false
                    );
                }
                BranchUpgradeButton.branchUpgradeHb.get(__instance).render(sb);
            }
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "renderInLibrary"
    )
    public static class RenderInLibraryUpgrade {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"copy"}
        )
        public static void Insert(AbstractCard __instance, SpriteBatch sb, AbstractCard copy) {
            if (copy instanceof BranchingUpgradesCard) {
                ((BranchingUpgradesCard) copy).setUpgradeType(BranchingUpgradesCard.UpgradeType.NORMAL_UPGRADE);
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    public static Field hoveredCardField;
    public static AbstractCard getHoveredCard() {
        GridCardSelectScreen gc = AbstractDungeon.gridSelectScreen;
        try {
            if (hoveredCardField == null) {
                hoveredCardField = gc.getClass().getDeclaredField("hoveredCard");
            }
            hoveredCardField.setAccessible(true);
            return (AbstractCard) hoveredCardField.get(gc);
        } catch (Exception e) {
            System.out.println("Exception occurred when getting private field hoveredCard from StSLib: " + e.toString());
            return null;
        }
    }
}

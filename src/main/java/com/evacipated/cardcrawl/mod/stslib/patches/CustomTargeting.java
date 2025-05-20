package com.evacipated.cardcrawl.mod.stslib.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.SurroundedPower;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import com.evacipated.cardcrawl.mod.stslib.cards.targeting.TargetingHandler;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.megacrit.cardcrawl.characters.AbstractPlayer.HOVER_CARD_Y_POSITION;

public class CustomTargeting {
    public static void registerCustomTargeting(AbstractCard.CardTarget customTarget, TargetingHandler<?> targeting) {
        switch (customTarget) {
            case ALL:
            case NONE:
            case SELF:
            case ENEMY:
            case ALL_ENEMY:
            case SELF_AND_ENEMY:
                System.out.println("Attempted to define custom targeting for an existing targeting method.");
                break;
            default:
                if (targetingMap.containsKey(customTarget)) {
                    System.out.println("Custom targeting for " + customTarget.name() + " registered multiple times. May be a conflict.");
                }
                targetingMap.put(customTarget, targeting);
        }
    }

    public static final HashMap<AbstractCard.CardTarget, TargetingHandler<?>> targetingMap = new HashMap<>();

    public static <T> void setCardTarget(AbstractCard c, T target) {
        TargetingHandler<?> handler = targetingMap.get(c.target);
        if (handler == null)
            return;

        TargetField.target.get(c).put(handler, target);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getCardTarget(AbstractCard c) {
        TargetingHandler<?> handler = targetingMap.get(c.target);
        if (handler == null)
            return null;

        try {
            return (T) TargetField.target.get(c).getOrDefault(handler, null);
        }
        catch (ClassCastException e) {
            return null;
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = SpirePatch.CLASS
    )
    public static class TargetField {
        public static final SpireField<Map<TargetingHandler<?>, Object>> target = new SpireField<>(HashMap::new);
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "makeSameInstanceOf"
    )
    public static class CopyTarget {
        @SpirePostfixPatch
        public static AbstractCard copy(AbstractCard __result, AbstractCard __instance) {
            TargetField.target.get(__result).putAll(TargetField.target.get(__instance));
            return __result;
        }
    }

    private static Method playCard;

    static {
        try {
            playCard = AbstractPlayer.class.getDeclaredMethod("playCard");
            playCard.setAccessible(true);
        }
        catch (Exception ignored) { }
    }
    private static void tryPlayCard(AbstractPlayer p)
    {
        try {
            playCard.invoke(p);
        }
        catch (Exception ignored) { }
    }


    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "clickAndDragCards"
    )
    public static class UseTargetArrow {
        //Might need to also patch line 1533, touchscreen card play code? But it doesn't check for dragging/targeting there, so I think it's fine
        @SpireInsertPatch(
                locator = NeedTargetLocator.class
        )
        public static SpireReturn<Boolean> cantPlayWithoutTarget(AbstractPlayer __instance)
        {
            //When attempting to play a card without a target
            if (targetingMap.containsKey(__instance.hoveredCard.target))
            {
                CardCrawlGame.sound.play("CARD_OBTAIN");
                __instance.releaseCard();

                ReflectionHacks.setPrivate(__instance, AbstractPlayer.class, "isUsingClickDragControl", false);

                return SpireReturn.Return(true);
            }
            return SpireReturn.Continue();
        }

        @SpireInsertPatch(
                locator = UseTargetingLocator.class
        )
        public static SpireReturn<Boolean> enableTargeting(AbstractPlayer __instance)
        {
            //dragged into drop zone
            if (__instance.isHoveringDropZone && targetingMap.containsKey(__instance.hoveredCard.target)) {
                TargetingHandler<?> handler = targetingMap.get(__instance.hoveredCard.target);

                __instance.inSingleTargetMode = true;
                ReflectionHacks.setPrivate(__instance, AbstractPlayer.class, "arrowX", (float) InputHelper.mX);
                ReflectionHacks.setPrivate(__instance, AbstractPlayer.class, "arrowY", (float) InputHelper.mY);
                GameCursor.hidden = true;
                __instance.hoveredCard.untip();
                __instance.hand.refreshHandLayout();
                __instance.hoveredCard.target_y = handler.cardTargetingY();
                __instance.hoveredCard.target_x = handler.cardTargetingX();
                __instance.isDraggingCard = false;

                return SpireReturn.Return(true);
            }
            return SpireReturn.Continue();
        }
        @SpireInsertPatch(
                locator = NeedTargetLocatorTwo.class
        )
        public static SpireReturn<Boolean> reallyNeedATarget(AbstractPlayer __instance)
        {
            //Same purpose as previous, just slightly different as isHoveringDropZone check is unnecessary
            if (targetingMap.containsKey(__instance.hoveredCard.target)) {
                TargetingHandler<?> handler = targetingMap.get(__instance.hoveredCard.target);

                __instance.inSingleTargetMode = true;
                ReflectionHacks.setPrivate(__instance, AbstractPlayer.class, "arrowX", (float) InputHelper.mX);
                ReflectionHacks.setPrivate(__instance, AbstractPlayer.class, "arrowY", (float) InputHelper.mY);
                GameCursor.hidden = true;
                __instance.hoveredCard.untip();
                __instance.hand.refreshHandLayout();
                __instance.hoveredCard.target_y = handler.cardTargetingY();
                __instance.hoveredCard.target_x = handler.cardTargetingX();
                __instance.isDraggingCard = false;

                return SpireReturn.Return(true);
            }
            return SpireReturn.Continue();
        }

        @SpireInsertPatch(
                locator = KeyboardAutotargetLocator.class
        )
        public static SpireReturn<Boolean> keyboardModeAutotarget(AbstractPlayer __instance) {
            //Selecting a card using controller input, I believe
            if (targetingMap.containsKey(__instance.hoveredCard.target)) {
                Gdx.input.setCursorPosition(targetingMap.get(__instance.hoveredCard.target).getDefaultTargetX(), Settings.HEIGHT - targetingMap.get(__instance.hoveredCard.target).getDefaultTargetY());
                targetingMap.get(__instance.hoveredCard.target).setDefaultTarget();
                return SpireReturn.Return(true);
            }
            return SpireReturn.Continue();
        }
        @SpireInstrumentPatch
        public static ExprEditor adjustDefaultSelectPosition() {
            //Selecting a card using keyboard selection key while in keyboard mode
            return new ExprEditor() {
                int count = 0;

                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("setCursorPosition") && m.getClassName().equals("com.badlogic.gdx.Input")) {
                        if (count == 1) {
                            m.replace("if (" + CustomTargeting.class.getName() + ".targetingMap.containsKey(hoveredCard.target)) {" +
                                    "$1 = ((" + TargetingHandler.class.getName() + ") " + CustomTargeting.class.getName() + ".targetingMap.get(hoveredCard.target)).getDefaultTargetX();" +
                                    "$2 = " + Settings.class.getName() + ".HEIGHT - ((" + TargetingHandler.class.getName() + ") " + CustomTargeting.class.getName() + ".targetingMap.get(hoveredCard.target)).getDefaultTargetY();" +
                                    "((" + TargetingHandler.class.getName() + ") " + CustomTargeting.class.getName() + ".targetingMap.get(hoveredCard.target)).setDefaultTarget();" +
                                    "}" +
                                    "$_ = $proceed($1, $2);");
                        }

                        ++count;
                    }
                }
            };
        }

        private static class NeedTargetLocator extends SpireInsertLocator { //Line 1422
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "isHoveringDropZone");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
        private static class NeedTargetLocatorTwo extends SpireInsertLocator { //Line 1497
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "target");
                return new int[] { LineFinder.findAllInOrder(ctBehavior, finalMatcher)[6] };
            }
        }
        private static class UseTargetingLocator extends SpireInsertLocator { //Line 1473
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "isHoveringDropZone");
                return new int[] { LineFinder.findAllInOrder(ctBehavior, finalMatcher)[2] };
            }
        } //Intelij decompiler puts the code in a weird order >:(
        private static class KeyboardAutotargetLocator extends SpireInsertLocator { //Line 1370
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "target");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "updateSingleTargetInput"
    )
    public static class UseAlternateTargeting {
        @SpirePrefixPatch
        public static SpireReturn<?> alternateTargeting(AbstractPlayer __instance)
        {
            if (__instance.hoveredCard != null && targetingMap.containsKey(__instance.hoveredCard.target))
            {
                customTargeting(__instance, targetingMap.get(__instance.hoveredCard.target));
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "renderTargetingUi"
    )
    public static class HighlightArrowColor {
        @SpireInstrumentPatch
        public static ExprEditor highlightOnTarget() {
            //Rendering targeting arrow, change color if on a valid target
            return new ExprEditor() {
                boolean first = true;

                @Override
                public void edit(FieldAccess f) throws CannotCompileException {
                    if (f.getFieldName().equals("hoveredMonster") && f.getClassName().equals(AbstractPlayer.class.getName()) && first) {
                        first = false;
                        f.replace("$_ = $proceed();" +
                                "if (" + CustomTargeting.class.getName() + ".targetingMap.containsKey(hoveredCard.target)) {" +
                                        "if (((" + TargetingHandler.class.getName() + ") " + CustomTargeting.class.getName() + ".targetingMap.get(hoveredCard.target)).hasTarget()) {" +
                                        //"$_ = " + AbstractDungeon.class.getName() + ".getMonsters().monsters.isEmpty() ? null : " + AbstractDungeon.class.getName() + ".getMonsters().monsters.get(0);" +
                                        "$_ = \"\";" +
                                "}}"
                        );
                        //AbstractDungeon.getMonsters().monsters.isEmpty() ? null : AbstractDungeon.getMonsters().monsters.get(0)
                    }
                }
            };
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "playCard"
    )
    public static class SetFinalTarget {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void setFinalTarget(AbstractPlayer __instance)
        {
            TargetingHandler<?> targeting = targetingMap.get(__instance.hoveredCard.target);
            if (targeting != null)
            {
                targeting.lockTarget(__instance.hoveredCard);
                Object o = getCardTarget(__instance.hoveredCard);
                if (o instanceof AbstractMonster && __instance.hasPower(SurroundedPower.POWER_ID)) {
                    __instance.flipHorizontal = ((AbstractMonster) o).drawX < __instance.drawX;
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "target");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "renderHoverReticle"
    )
    public static class RenderReticle {
        @SpirePrefixPatch
        public static SpireReturn<?> customReticle(AbstractPlayer __instance, SpriteBatch sb) {
            if (targetingMap.containsKey(__instance.hoveredCard.target)) {
                targetingMap.get(__instance.hoveredCard.target).renderReticle(sb);

                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }


    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "renderHand"
    )
    public static class CalculateDamageWhenHovering {
        @SpirePrefixPatch
        public static void calcWhenHovering(AbstractPlayer __instance, SpriteBatch sb) {
            if (__instance.hoveredCard != null) {
                TargetingHandler<?> targeting = targetingMap.get(__instance.hoveredCard.target);
                if (targeting != null) {
                    Object target = targeting.getHovered();
                    if (target != null) {
                        AbstractMonster m = target instanceof AbstractMonster ? (AbstractMonster) target : null;
                        CustomTargeting.setCardTarget(__instance.hoveredCard, target);
                        __instance.hoveredCard.calculateCardDamage(m);
                        CustomTargeting.setCardTarget(__instance.hoveredCard, null);
                    }
                }
            }
        }
    }

    
    private static void customTargeting(AbstractPlayer p, TargetingHandler<?> targeting)
    {
        if (Settings.isTouchScreen && !((boolean)ReflectionHacks.getPrivate(p, AbstractPlayer.class, "isUsingClickDragControl")) && !InputHelper.isMouseDown) {
            Gdx.input.setCursorPosition((int) MathUtils.lerp((float)Gdx.input.getX(), (float)Settings.WIDTH / 2.0F, Gdx.graphics.getDeltaTime() * 10.0F), (int)MathUtils.lerp((float)Gdx.input.getY(), (float)Settings.HEIGHT * 1.1F, Gdx.graphics.getDeltaTime() * 4.0F));
        }

        ReflectionHacks.setPrivate(p, AbstractPlayer.class, "hoveredMonster", null);

        AbstractCard cardFromHotkey;
        if (p.isInKeyboardMode) {
            if (InputActionSet.releaseCard.isJustPressed() || CInputActionSet.cancel.isJustPressed()) {
                //Release selected card
                cardFromHotkey = p.hoveredCard;
                p.inSingleTargetMode = false;

                //p.hoverCardInHand(cardFromHotkey);
                // v the contents of this^ method, none of which are private. Unlike the method.
                p.toHover = cardFromHotkey;
                if (Settings.isControllerMode && AbstractDungeon.actionManager.turnHasEnded) {
                    p.toHover = null;
                }
                if (cardFromHotkey != null && !p.inspectMode) {
                    Gdx.input.setCursorPosition((int)cardFromHotkey.hb.cX, (int)((float)Settings.HEIGHT - HOVER_CARD_Y_POSITION));
                }
            }
            else { //Keyboard targeting
                targeting.updateKeyboardTarget();
            }
        } else {
            //Mouse targeting
            targeting.updateHovered();
        }

        if (!AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead() && !InputHelper.justClickedRight && (float)InputHelper.mY >= ((float) ReflectionHacks.getPrivate(p, AbstractPlayer.class, "hoverStartLine")) - 100.0F * Settings.scale && (float)InputHelper.mY >= 50.0F * Settings.scale) {
            cardFromHotkey = InputHelper.getCardSelectedByHotkey(p.hand);
            if (cardFromHotkey != null && !isCardQueued(cardFromHotkey)) {
                boolean isSameCard = cardFromHotkey == p.hoveredCard;
                p.releaseCard();

                targeting.clearHovered();

                if (isSameCard) {
                    //Just release
                    GameCursor.hidden = false;
                } else {
                    //Select new card
                    p.hoveredCard = cardFromHotkey;
                    p.hoveredCard.setAngle(0.0F, false);
                    ReflectionHacks.setPrivate(p, AbstractPlayer.class, "isUsingClickDragControl", true);
                    p.isDraggingCard = true;
                }
            }

            if (!InputHelper.justClickedLeft && !InputActionSet.confirm.isJustPressed() && !CInputActionSet.select.isJustPressed()) {
                if (!((boolean)ReflectionHacks.getPrivate(p, AbstractPlayer.class, "isUsingClickDragControl")) && InputHelper.justReleasedClickLeft && targeting.hasTarget()) {
                    if (p.hoveredCard.canUse(p, null)) {
                        tryPlayCard(p);
                    } else {
                        AbstractDungeon.effectList.add(new ThoughtBubble(p.dialogX, p.dialogY, 3.0F, p.hoveredCard.cantUseMessage, true));
                        //p.energyTip(p.hoveredCard); just a tutorial tip for energy costs
                        p.releaseCard();
                    }

                    p.inSingleTargetMode = false;
                    GameCursor.hidden = false;
                    targeting.clearHovered();
                }
            } else {
                InputHelper.justClickedLeft = false;
                if (!targeting.hasTarget()) {
                    CardCrawlGame.sound.play("UI_CLICK_1");
                } else {
                    if (p.hoveredCard.canUse(p, null)) {
                        tryPlayCard(p);
                    } else {
                        AbstractDungeon.effectList.add(new ThoughtBubble(p.dialogX, p.dialogY, 3.0F, p.hoveredCard.cantUseMessage, true));
                        p.releaseCard();
                    }

                    ReflectionHacks.setPrivate(p, AbstractPlayer.class, "isUsingClickDragControl", false);
                    p.inSingleTargetMode = false;
                    GameCursor.hidden = false;

                    targeting.clearHovered();
                }
            }
        } else {
            if (Settings.isTouchScreen) {
                InputHelper.moveCursorToNeutralPosition();
            }

            p.releaseCard();
            CardCrawlGame.sound.play("UI_CLICK_2");
            ReflectionHacks.setPrivate(p, AbstractPlayer.class, "isUsingClickDragControl", false);
            p.inSingleTargetMode = false;
            GameCursor.hidden = false;

            targeting.clearHovered();
        }
    }


    private static boolean isCardQueued(AbstractCard card) {
        for (CardQueueItem i : AbstractDungeon.actionManager.cardQueue) {
            if (i.card == card)
                return true;
        }
        return false;
    }
}

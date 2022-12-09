package com.evacipated.cardcrawl.mod.stslib.patches;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.blockmods.AbstractBlockModifier;
import com.evacipated.cardcrawl.mod.stslib.blockmods.BlockInstance;
import com.evacipated.cardcrawl.mod.stslib.blockmods.BlockModifierManager;
import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.DamageModApplyingPower;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnCreateBlockInstancePower;
import com.evacipated.cardcrawl.mod.stslib.relics.DamageModApplyingRelic;
import com.evacipated.cardcrawl.mod.stslib.relics.OnCreateBlockInstanceRelic;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.AttackDamageRandomEnemyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

public class BindingPatches {

    public static final ArrayList<AbstractDamageModifier> directlyBoundDamageMods = new ArrayList<>();
    public static final ArrayList<AbstractBlockModifier> directlyBoundBlockMods = new ArrayList<>();
    public static Object directlyBoundInstigator;
    private static AbstractCard cardInUse;

    private static boolean canPassInstigator = true;

    @SpirePatch(clz = AbstractGameAction.class, method = SpirePatch.CLASS)
    public static class BoundGameActionFields {
        public static SpireField<Object> actionDelayedDirectlyBoundInstigator = new SpireField<>(() -> null);
        public static SpireField<AbstractCard> actionDelayedCardInUse = new SpireField<>(() -> null);
        public static SpireField<ArrayList<AbstractDamageModifier>> actionDelayedDamageMods = new SpireField<>(ArrayList::new);
        public static SpireField<ArrayList<AbstractBlockModifier>> actionDelayedBlockMods = new SpireField<>(ArrayList::new);
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "useCard")
    public static class GrabCardInUse {
        @SpireInsertPatch(locator = Locator.class)
        public static void RememberCardPreUseCall(AbstractPlayer __instance, AbstractCard c, AbstractMonster monster, int energyOnUse) {
            //Right before you call card.use, set it as the object in use
            cardInUse = c;
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "use");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
        @SpireInsertPatch(locator = Locator2.class)
        public static void ForgetCardPostUseCall(AbstractPlayer __instance, AbstractCard c, AbstractMonster monster, int energyOnUse) {
            //Once you call card.use, set the object back to null, as any actions were already added to the queue
            cardInUse = null;
        }
        private static class Locator2 extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(GameActionManager.class, "addToBottom");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(clz = GameActionManager.class, method = "addToTop")
    @SpirePatch(clz = GameActionManager.class, method = "addToBottom")
    public static class BindObjectToAction {
        @SpirePrefixPatch
        public static void WithoutCrashingHopefully(GameActionManager __instance, AbstractGameAction action) {
            //When our action is added to the queue, see if there is an active object in use that caused this to happen
            if (cardInUse != null && !(action instanceof ApplyPowerAction)) {
                //If so, this is our instigator object, we need to add any non-innate card mods
                BoundGameActionFields.actionDelayedCardInUse.set(action, cardInUse);
            }
            //Daisy chain our actions if we can
            AbstractGameAction a = AbstractDungeon.actionManager.currentAction;
            if (a != null && BoundGameActionFields.actionDelayedCardInUse.get(a) != null && canPassInstigator) {
                BoundGameActionFields.actionDelayedCardInUse.set(action, BoundGameActionFields.actionDelayedCardInUse.get(a));
            }
        }
    }

    @SpirePatch(clz = DamageInfo.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCreature.class, int.class, DamageInfo.DamageType.class})
    public static class BindObjectToDamageInfo {

        private static final ArrayList<AbstractDamageModifier> boundMods = new ArrayList<>();

        @SpirePostfixPatch()
        public static void PostfixMeToPiggybackBinding(DamageInfo __instance, AbstractCreature damageSource, int base, DamageInfo.DamageType type) {
            Object instigator = null;
            //Grab the action currently running, as this is what was processing when our damage info was created
            AbstractGameAction a = AbstractDungeon.actionManager.currentAction;
            if (a != null && canPassInstigator) {
                if (!BoundGameActionFields.actionDelayedDamageMods.get(a).isEmpty()) {
                    boundMods.addAll(BoundGameActionFields.actionDelayedDamageMods.get(a).stream().filter(m -> m.affectsDamageType(type)).collect(Collectors.toList()));
                    if (BoundGameActionFields.actionDelayedDirectlyBoundInstigator.get(a) != null) {
                        instigator = BoundGameActionFields.actionDelayedDirectlyBoundInstigator.get(a);
                    }
                }
                if (BoundGameActionFields.actionDelayedCardInUse.get(a) != null && a.source == damageSource) {
                    boundMods.addAll(DamageModifierManager.modifiers(BoundGameActionFields.actionDelayedCardInUse.get(a)).stream().filter(m -> m.automaticBindingForCards && m.affectsDamageType(type)).collect(Collectors.toList()));
                    instigator = BoundGameActionFields.actionDelayedCardInUse.get(a);
                }
            }
            if (!directlyBoundDamageMods.isEmpty()) {
                boundMods.addAll(directlyBoundDamageMods.stream().filter(m -> m.affectsDamageType(type)).collect(Collectors.toList()));
                if (directlyBoundInstigator != null) {
                    instigator = directlyBoundInstigator;
                }
            }
            if (cardInUse != null) {
                boundMods.addAll(DamageModifierManager.modifiers(cardInUse).stream().filter(m -> m.automaticBindingForCards && m.affectsDamageType(type)).collect(Collectors.toList()));
                instigator = cardInUse;
            }
            if (damageSource != null) {
                for (AbstractPower p : damageSource.powers) {
                    if (p instanceof DamageModApplyingPower && ((DamageModApplyingPower) p).shouldPushMods(__instance, instigator, boundMods)) {
                        boundMods.addAll(((DamageModApplyingPower) p).modsToPush(__instance, instigator, boundMods));
                        ((DamageModApplyingPower) p).onAddedDamageModsToDamageInfo(__instance, instigator);
                    }
                }
            }
            if (AbstractDungeon.player != null && AbstractDungeon.player == damageSource) {
                for (AbstractRelic r : AbstractDungeon.player.relics) {
                    if (r instanceof DamageModApplyingRelic && ((DamageModApplyingRelic) r).shouldPushMods(__instance, instigator, boundMods)) {
                        boundMods.addAll(((DamageModApplyingRelic) r).modsToPush(__instance, instigator, boundMods));
                        ((DamageModApplyingRelic) r).onAddedDamageModsToDamageInfo(__instance, instigator);
                    }
                }
            }
            DamageModifierManager.bindDamageMods(__instance, boundMods);
            DamageModifierManager.bindInstigator(__instance, instigator);
            boundMods.clear();
        }
    }

    @SpirePatch2(clz = AbstractCard.class, method = "applyPowers")
    @SpirePatch2(clz = AbstractCard.class, method = "calculateCardDamage")
    public static class AddTempModifiers {

        private static final ArrayList<AbstractDamageModifier> pushedMods = new ArrayList<>();
        private static final ArrayList<AbstractDamageModifier> inherentMods = new ArrayList<>();

        @SpirePrefixPatch()
        public static void addMods(AbstractCard __instance) {
            inherentMods.addAll(DamageModifierManager.modifiers(__instance));
            pushedMods.addAll(inherentMods);
            for (AbstractPower p : AbstractDungeon.player.powers) {
                if (p instanceof DamageModApplyingPower && ((DamageModApplyingPower) p).shouldPushMods(null, __instance, pushedMods)) {
                    pushedMods.addAll(((DamageModApplyingPower) p).modsToPush(null, __instance, pushedMods));
                }
            }
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (r instanceof DamageModApplyingRelic && ((DamageModApplyingRelic) r).shouldPushMods(null, __instance, pushedMods)) {
                    pushedMods.addAll(((DamageModApplyingRelic) r).modsToPush(null, __instance, pushedMods));
                }
            }
            pushedMods.removeAll(inherentMods);
            inherentMods.clear();
            DamageModifierManager.addModifiers(__instance, pushedMods);
        }

        @SpirePostfixPatch()
        public static void removeMods(AbstractCard __instance) {
            DamageModifierManager.removeModifiers(__instance, pushedMods);
            pushedMods.clear();
        }
    }

    @SpirePatch(clz = AbstractCreature.class, method = "addBlock")
    public static class AddBlockMakePlaceHolderIfNeeded {
        static final HashSet<AbstractBlockModifier> blockSet = new HashSet<>();
        @SpireInsertPatch(locator = CreatureAddBlockLocator.class, localvars = "tmp")
        public static void pls(AbstractCreature __instance, int amount, float tmp) {
            Object instigator = null;
            //Grab the action currently running, as this is what was processing when our block method was called
            AbstractGameAction a = AbstractDungeon.actionManager.currentAction;
            if (a != null) {
                //If the action is not null, see if it has an instigator object
                if (!BoundGameActionFields.actionDelayedBlockMods.get(a).isEmpty()) {
                    blockSet.addAll(BoundGameActionFields.actionDelayedBlockMods.get(a));
                }
                if (BoundGameActionFields.actionDelayedCardInUse.get(a) != null) {
                    for (AbstractBlockModifier m : BlockModifierManager.modifiers(BoundGameActionFields.actionDelayedCardInUse.get(a))) {
                        if (m.automaticBindingForCards) {
                            blockSet.add(m);
                        }
                    }
                    instigator = BoundGameActionFields.actionDelayedCardInUse.get(a);
                }
            }
            if (!directlyBoundBlockMods.isEmpty()) {
                blockSet.addAll(directlyBoundBlockMods);
                if (directlyBoundInstigator != null) {
                    instigator = directlyBoundInstigator;
                }
            }
            if (cardInUse != null) {
                for (AbstractBlockModifier m : BlockModifierManager.modifiers(cardInUse)) {
                    if (m.automaticBindingForCards) {
                        blockSet.add(m);
                    }
                }
                instigator = cardInUse;
            }
            for (AbstractPower p : __instance.powers) {
                if (p instanceof OnCreateBlockInstancePower) {
                    ((OnCreateBlockInstancePower) p).onCreateBlockInstance(blockSet, instigator);
                }
            }
            if (AbstractDungeon.player != null && AbstractDungeon.player == __instance) {
                for (AbstractRelic r : AbstractDungeon.player.relics) {
                    if (r instanceof OnCreateBlockInstanceRelic) {
                        ((OnCreateBlockInstanceRelic) r).onCreateBlockInstance(blockSet, instigator);
                    }
                }
            }
            ArrayList<AbstractBlockModifier> blockTypes = new ArrayList<>();
            for (AbstractBlockModifier m : blockSet) {
                blockTypes.add(m.makeCopy());
            }
            blockSet.clear();
            Collections.sort(blockTypes);
            BlockInstance b = new BlockInstance(__instance, (int)tmp, blockTypes);
            BlockModifierManager.addBlockInstance(__instance, b);
        }
    }

    private static class CreatureAddBlockLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(MathUtils.class, "floor");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "damage")
    @SpirePatch(clz = AbstractPlayer.class, method = "onCardDrawOrDiscard")
    @SpirePatch(clz = AbstractPlayer.class, method = "draw", paramtypez = int.class)
    @SpirePatch(clz = AbstractPlayer.class, method = "onVictory")
    @SpirePatch(clz = AbstractPlayer.class, method = "channelOrb")
    @SpirePatch(clz = AbstractMonster.class, method = "damage")
    @SpirePatch(clz = AbstractMonster.class, method = "heal")
    @SpirePatch(clz = AbstractMonster.class, method = "die", paramtypez = boolean.class)
    @SpirePatch(clz = AbstractCreature.class, method = "heal", paramtypez = {int.class, boolean.class})
    @SpirePatch(clz = AbstractCreature.class, method = "addBlock")
    @SpirePatch(clz = AbstractCreature.class, method = "addPower")
    @SpirePatch(clz = AbstractCreature.class, method = "applyStartOfTurnPowers")
    @SpirePatch(clz = AbstractCreature.class, method = "applyTurnPowers")
    @SpirePatch(clz = AbstractCreature.class, method = "applyStartOfTurnPostDrawPowers")
    public static class DisableReactionaryActionBinding {
        @SpirePrefixPatch
        public static void disableBefore(AbstractCreature __instance) {
            canPassInstigator = false;
        }
        @SpirePostfixPatch
        public static void enableAfter(AbstractCreature __instance) {
            canPassInstigator = true;
        }
    }

    @SpirePatch(clz = AttackDamageRandomEnemyAction.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class, AbstractGameAction.AttackEffect.class})
    public static class LetAttackDamageRandomEnemyActionWorkWithDamageMods {
        @SpirePostfixPatch
        public static void setSource(AttackDamageRandomEnemyAction __instance) {
            if (cardInUse != null) {
                __instance.source = AbstractDungeon.player;
            }
        }
    }

}

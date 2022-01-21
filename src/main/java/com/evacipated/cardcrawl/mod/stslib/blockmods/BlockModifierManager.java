package com.evacipated.cardcrawl.mod.stslib.blockmods;

import com.evacipated.cardcrawl.mod.stslib.patches.BindingPatches;
import com.evacipated.cardcrawl.mod.stslib.patches.BlockModifierPatches;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockModifierManager {

    @SpirePatch(clz = AbstractCreature.class, method = SpirePatch.CLASS)
    public static class BlockTypes {
        public static SpireField<ArrayList<BlockInstance>> blockInstances = new SpireField<>(ArrayList::new);
    }

    public static class BlockModifierFields {
        @SpirePatch(clz = AbstractCard.class, method = SpirePatch.CLASS)
        public static class CardField {
            public static final SpireField<List<AbstractBlockModifier>> blockModifiers = new SpireField<>(ArrayList::new);
        }
    }

    public static void addBlockInstance(AbstractCreature owner, BlockInstance instance) {
        boolean stacked = false;
        for (BlockInstance b : BlockTypes.blockInstances.get(owner)) {
            if (b.containsSameBlockTypes(instance) && b.shouldStack() && instance.shouldStack()) {
                b.setBlockAmount(b.getBlockAmount()+instance.getBlockAmount());
                for (AbstractBlockModifier m : b.getBlockTypes()) {
                    m.onStack(instance.getBlockAmount());
                }
                stacked = true;
            }
        }
        if (!stacked) {
            for (AbstractBlockModifier m : instance.getBlockTypes()) {
                m.onApplication();
            }
            BlockTypes.blockInstances.get(owner).add(0, instance);
            Collections.sort(BlockTypes.blockInstances.get(owner));
        }
    }

    public static List<AbstractBlockModifier> modifiers(AbstractCard c) {
        return BlockModifierFields.CardField.blockModifiers.get(c);
    }

    public static void addModifier(AbstractCard card, AbstractBlockModifier blockMod) {
        modifiers(card).add(blockMod);
        Collections.sort(modifiers(card));
    }

    public static void addModifiers(AbstractCard card, ArrayList<AbstractBlockModifier> blockMods) {
        modifiers(card).addAll(blockMods);
        Collections.sort(modifiers(card));
    }

    public static void removeModifier(AbstractCard card, AbstractBlockModifier blockMod) {
        modifiers(card).remove(blockMod);
    }

    public static void removeModifiers(AbstractCard card, ArrayList<AbstractBlockModifier> blockMods) {
        modifiers(card).removeAll(blockMods);
    }

    public static void clearModifiers(AbstractCard card) {
        modifiers(card).clear();
    }

    public static BlockInstance getTopBlockInstance(AbstractCreature owner) {
        return BlockTypes.blockInstances.get(owner).get(0);
    }

    public static boolean hasCustomBlockType(AbstractCreature owner) {
        return !BlockTypes.blockInstances.get(owner).isEmpty();
    }

    public static void addCustomBlock(Object instigator, List<AbstractBlockModifier> mods, AbstractCreature owner, int amount) {
        BindingPatches.directlyBoundInstigator = instigator;
        BindingPatches.directlyBoundBlockMods.addAll(mods);
        owner.addBlock(amount);
        BindingPatches.directlyBoundBlockMods.clear();
        BindingPatches.directlyBoundInstigator = null;
    }

    public static void addCustomBlock(AbstractCard card, AbstractCreature owner,  int amount) {
        addCustomBlock(card, modifiers(card), owner, amount);
    }

    public static void addCustomBlock(BlockModContainer instance, AbstractCreature owner,  int amount) {
        addCustomBlock(instance.instigator(), instance.modifiers(), owner, amount);
    }

    public static ArrayList<BlockInstance> blockInstances(AbstractCreature owner) {
        return BlockTypes.blockInstances.get(owner);
    }

    public static void reduceSpecificBlockType(BlockInstance instance, int amount) {
        int toRemove = Math.min(instance.getBlockAmount(), amount);
        BlockModifierPatches.specificInstanceToReduce = instance;
        instance.getOwner().loseBlock(toRemove);
    }

    public static void removeSpecificBlockType(BlockInstance instance) {
        BlockModifierPatches.specificInstanceToReduce = instance;
        instance.getOwner().loseBlock(instance.getBlockAmount());
    }

    public static void removeEmptyBlockInstances(AbstractCreature owner) {
        BlockModifierManager.blockInstances(owner).removeIf(b -> b.getBlockAmount() <= 0);
    }

    public static void removeAllBlockInstances(AbstractCreature owner) {
        BlockTypes.blockInstances.get(owner).clear();
    }

    public static int getBlockRetValBasedOnRemainingAmounts(AbstractCreature owner) {
        int ret = 0;
        for (BlockInstance b : blockInstances(owner)) {
            ret += b.getBlockAmount();
        }
        return owner.currentBlock - ret;
    }

    private static void addToTop(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToTop(action);
    }

    private static void addToBot(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToBottom(action);
    }

    public static void atEndOfRound(AbstractCreature owner) {
        for (BlockInstance b : blockInstances(owner)) {
            for (AbstractBlockModifier m : b.getBlockTypes()) {
                m.atEndOfRound();
            }
        }
    }

    public static float atDamageReceive(AbstractCreature owner, float damage, DamageInfo.DamageType type, AbstractCreature source) {
        for (BlockInstance b : blockInstances(owner)) {
            for (AbstractBlockModifier m : b.getBlockTypes()) {
                damage = m.atDamageReceive(damage, type, source);
            }
        }
        return damage;
    }

    public static float atDamageFinalReceive(AbstractCreature owner, float damage, DamageInfo.DamageType type, AbstractCreature source) {
        for (BlockInstance b : blockInstances(owner)) {
            for (AbstractBlockModifier m : b.getBlockTypes()) {
                damage = m.atDamageFinalReceive(damage, type, source);
            }
        }
        return damage;
    }

    public static float atDamageGive(AbstractCreature owner, float damage, DamageInfo.DamageType type, AbstractCreature target, AbstractCard card) {
        for (BlockInstance b : blockInstances(owner)) {
            for (AbstractBlockModifier m : b.getBlockTypes()) {
                damage = m.atDamageGive(damage, type, target, card);
            }
        }
        return damage;
    }

    public static float atDamageFinalGive(AbstractCreature owner, float damage, DamageInfo.DamageType type, AbstractCreature target, AbstractCard card) {
        for (BlockInstance b : blockInstances(owner)) {
            for (AbstractBlockModifier m : b.getBlockTypes()) {
                damage = m.atDamageFinalGive(damage, type, target, card);
            }
        }
        return damage;
    }

    public static int onHeal(AbstractCreature owner, int healAmount) {
        for (BlockInstance b : blockInstances(owner)) {
            for (AbstractBlockModifier m : b.getBlockTypes()) {
                healAmount = m.onHeal(healAmount);
            }
        }
        return healAmount;
    }

    public static void onAttack(AbstractCreature owner, DamageInfo info, int damageAmount, AbstractCreature target) {
        for (BlockInstance b : blockInstances(owner)) {
            for (AbstractBlockModifier m: b.getBlockTypes()) {
                m.onAttack(info, damageAmount, target);
            }
        }
    }

    //TODO not manipulating damage taken. This is a design choice, but revisit later.
    public static void onAttacked(AbstractCreature owner, DamageInfo info, int damageAmount) {
        for (BlockInstance b : blockInstances(owner)) {
            for (AbstractBlockModifier m : b.getBlockTypes()) {
                m.onAttacked(info, damageAmount);
            }
        }
    }

    //TODO not manipulating damage taken. This is a design choice, but revisit later.
    public static int onAttackedPostBlockReductions(AbstractCreature owner, DamageInfo info, int damageAmount) {
        for (BlockInstance b : blockInstances(owner)) {
            for (AbstractBlockModifier m : b.getBlockTypes()) {
                m.onAttackedPostBlockReductions(info, damageAmount);
            }
        }
        return damageAmount;
    }

    public static void onCardDraw(AbstractCreature owner, AbstractCard card) {
        for (BlockInstance b : blockInstances(owner)) {
            for (AbstractBlockModifier m : b.getBlockTypes()) {
                m.onCardDraw(card);
            }
        }
    }

    public static void onUseCard(AbstractCreature owner, AbstractCard card, UseCardAction action) {
        for (BlockInstance b : blockInstances(owner)) {
            for (AbstractBlockModifier m : b.getBlockTypes()) {
                m.onUseCard(card, action);
            }
        }
    }

    public static boolean onApplyPower(AbstractCreature owner, AbstractPower power, AbstractCreature target, AbstractCreature source) {
        boolean retVal = true;
        if (owner != null) {
            for (BlockInstance b : blockInstances(owner)) {
                for (AbstractBlockModifier m : b.getBlockTypes()) {
                    retVal &= m.onApplyPower(power, target, source);
                }
            }
        }
        return retVal;
    }

    public static int onApplyPowerStacks(AbstractCreature owner, AbstractPower power, AbstractCreature target, AbstractCreature source, int stackAmount) {
        if (owner != null) {
            for (BlockInstance b : blockInstances(owner)) {
                for (AbstractBlockModifier m : b.getBlockTypes()) {
                    stackAmount = m.onApplyPowerStacks(power, target, source, stackAmount);
                }
            }
        }
        return stackAmount;
    }

    public static boolean onReceivePower(AbstractCreature owner, AbstractPower power, AbstractCreature target, AbstractCreature source) {
        boolean retVal = true;
        if (owner != null) {
            for (BlockInstance b : blockInstances(owner)) {
                for (AbstractBlockModifier m : b.getBlockTypes()) {
                    retVal &= m.onReceivePower(power, target, source);
                }
            }
        }
        return retVal;
    }

    public static int onReceivePowerStacks(AbstractCreature owner, AbstractPower power, AbstractCreature target, AbstractCreature source, int stackAmount) {
        if (owner != null) {
            for (BlockInstance b : blockInstances(owner)) {
                for (AbstractBlockModifier m : b.getBlockTypes()) {
                    stackAmount = m.onReceivePowerStacks(power, target, source, stackAmount);
                }
            }
        }
        return stackAmount;
    }
}

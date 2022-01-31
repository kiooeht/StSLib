package com.evacipated.cardcrawl.mod.stslib.blockmods;

import basemod.helpers.TooltipInfo;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.ArrayList;

public abstract class AbstractBlockModifier implements Comparable<AbstractBlockModifier>{
    public enum Priority{
        NORMAL,
        TOP,
        BOTTOM
    }
    public AbstractCreature owner;
    public BlockInstance instance;
    public boolean automaticBindingForCards = true;

    public AbstractBlockModifier() {}

    public void setInstance(BlockInstance container) {
        this.instance = container;
    }
    public void setOwner(AbstractCreature owner) {
        this.owner = owner;
    }

    public int amountLostAtStartOfTurn() {
        return getCurrentAmount();
    }

    public float onModifyBlock(float block, AbstractCard card) {
        return block;
    }

    public float onModifyBlockFinal(float block, AbstractCard card) {
        return block;
    }

    public float atDamageGive(float damage, DamageInfo.DamageType type, AbstractCreature target, AbstractCard card) {
        return damage;
    }

    public float atDamageFinalGive(float damage, DamageInfo.DamageType type, AbstractCreature target, AbstractCard card) {
        return damage;
    }

    public float atDamageReceive(float damage, DamageInfo.DamageType type, AbstractCreature source) {
        return damage;
    }

    public float atDamageFinalReceive(float damage, DamageInfo.DamageType type, AbstractCreature source) {
        return damage;
    }

    public int onHeal(int healAmount) {
        return healAmount;
    }

    public void atStartOfTurnPreBlockLoss() {}

    public void onStartOfTurnBlockLoss(int blockLost) {}

    public void onApplication() {}

    public void onStack(int amount) {}

    public int onRemove(boolean lostByStartOfTurn, DamageInfo info, int remainingDamage) {
        return remainingDamage;
    }

    public void onThisBlockDamaged(DamageInfo info, int lostAmount) {}

    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {}
    
    public void onAttacked(DamageInfo info, int damageAmount) {}

    public void onAttackedPostBlockReductions(DamageInfo info, int damageAmount) {}

    public void atEndOfRound() {}

    public void onCardDraw(AbstractCard card) {}

    public void onUseCard(AbstractCard card, UseCardAction action) {}

    public boolean shouldStack() {
        return true;
    }

    public boolean isInherent() {
        return false;
    }

    public boolean onApplyPower(AbstractPower abstractPower, AbstractCreature target, AbstractCreature source) {
        return true;
    }

    public int onApplyPowerStacks(AbstractPower power, AbstractCreature target, AbstractCreature source, int stackAmount) {
        return stackAmount;
    }

    public boolean onReceivePower(AbstractPower abstractPower, AbstractCreature target, AbstractCreature source) {
        return true;
    }

    public int onReceivePowerStacks(AbstractPower power, AbstractCreature target, AbstractCreature source, int stackAmount) {
        return stackAmount;
    }

    abstract public String getName();

    abstract public String getDescription();

    protected void addToTop(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToTop(action);
    }

    protected void addToBot(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToBottom(action);
    }

    protected void reduceThisBlockContainer(int amount) {
        BlockModifierManager.reduceSpecificBlockType(instance, amount);
    }

    protected void removeThisBlockContainer() {
        BlockModifierManager.removeSpecificBlockType(instance);
    }

    protected int getCurrentAmount() {
        return instance.getBlockAmount();
    }

    public Priority priority() {
        return Priority.NORMAL;
    }

    public short subPriority() {
        return 0;
    }

    public Texture customBlockImage() {
        return null;
    }

    public Color blockImageColor() {
        return null;
    }

    public Color blockTextColor() {
        return null;
    }

    public String getCardDescriptor() {
        return null;
    }

    public ArrayList<TooltipInfo> getCustomTooltips() {
        return new ArrayList<>();
    }

    @Override
    public int compareTo(AbstractBlockModifier other) {
        return this.subPriority() - other.subPriority();
    }

    public abstract AbstractBlockModifier makeCopy();

}

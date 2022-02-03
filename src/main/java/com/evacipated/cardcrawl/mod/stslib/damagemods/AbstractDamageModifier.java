package com.evacipated.cardcrawl.mod.stslib.damagemods;

import basemod.helpers.TooltipInfo;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

public abstract class AbstractDamageModifier implements Comparable<AbstractDamageModifier> {
    public short priority = 0;
    public boolean automaticBindingForCards = true;

    public boolean isInherent() {
        return false;
    }

    public boolean ignoresBlock(AbstractCreature target) {
        return false;
    }

    public boolean ignoresTempHP(AbstractCreature target) {
        return false;
    }

    public boolean ignoresThorns() {
        return false;
    }

    public boolean affectsDamageType(DamageInfo.DamageType type) {
        return true;
    }

    public float atDamageGive(float damage, DamageInfo.DamageType type, AbstractCreature target, AbstractCard card) {
        return damage;
    }

    public float atDamageFinalGive(float damage, DamageInfo.DamageType type, AbstractCreature target, AbstractCard card) {
        return damage;
    }

    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {}

    public int onAttackToChangeDamage(DamageInfo info, int damageAmount, AbstractCreature target) {
        return damageAmount;
    }

    public void onDamageModifiedByBlock(DamageInfo info, int unblockedAmount, int blockedAmount, AbstractCreature target) {}

    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature target) {}

    public String getCardDescriptor() {
        return null;
    }

    public ArrayList<TooltipInfo> getCustomTooltips() {
        return new ArrayList<>();
    }

    public boolean removeWhenActivated() {
        return false;
    }

    protected void addToTop(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToTop(action);
    }

    protected void addToBot(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToBottom(action);
    }

    public AbstractCustomIcon getAccompanyingIcon() {
        return null;
    }

    public boolean shouldPushIconToCard(AbstractCard card) {
        return false;
    }

    @Override
    public int compareTo(AbstractDamageModifier other) {
        return this.priority - other.priority;
    }

    public abstract AbstractDamageModifier makeCopy();
}

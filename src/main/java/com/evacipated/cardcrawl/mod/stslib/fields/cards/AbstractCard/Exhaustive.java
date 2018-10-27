package com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard;

import com.evacipated.cardcrawl.mod.stslib.powers.ExhaustiveNegationPower;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import basemod.abstracts.DynamicVariable;

public class Exhaustive extends DynamicVariable
{
	@SpirePatch(
	        cls="com.megacrit.cardcrawl.cards.AbstractCard",
	        method=SpirePatch.CLASS
	)
	public static class ExhaustiveFields {
		public static SpireField<Integer> exhaustive = new SpireField<>(() -> -1);
		public static SpireField<Integer> baseExhaustive = new SpireField<>(() -> -1);
		public static SpireField<Boolean> isExhaustiveUpgraded = new SpireField<>(() -> false);
	}
	
    public String key() {
        return "replay:ex";
    }
    
    public boolean isModified(final AbstractCard card) {
        return ExhaustiveFields.exhaustive.get(card) != ExhaustiveFields.baseExhaustive.get(card);
    }
    
    public int value(final AbstractCard card) {
        return ExhaustiveFields.exhaustive.get(card);
    }
    
    public int baseValue(final AbstractCard card) {
        return ExhaustiveFields.baseExhaustive.get(card);
    }
    
    public boolean upgraded(final AbstractCard card) {
        return ExhaustiveFields.isExhaustiveUpgraded.get(card);
    }
    
    public static void setBaseValue(final AbstractCard card, final int amount) {
    	ExhaustiveFields.baseExhaustive.set(card, amount);
    	ExhaustiveFields.exhaustive.set(card, amount);
    	card.initializeDescription();
    }
    
    public static void upgrade(final AbstractCard card, final int amount) {
    	ExhaustiveFields.isExhaustiveUpgraded.set(card, true);
    	setBaseValue(card, ExhaustiveFields.baseExhaustive.get(card) + amount);
    }
    
    public static void increment(final AbstractCard card) {
    	if (AbstractDungeon.player.hasPower(ExhaustiveNegationPower.POWER_ID)) {
            AbstractDungeon.player.getPower(ExhaustiveNegationPower.POWER_ID).onSpecificTrigger();
            return;
        }
    	ExhaustiveFields.exhaustive.set(card, ExhaustiveFields.exhaustive.get(card) - 1);
    	if (ExhaustiveFields.exhaustive.get(card) <= 0) {
    		card.exhaust = true;
    	}
    	card.initializeDescription();
    }
}

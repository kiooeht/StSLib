package com.evacipated.cardcrawl.mod.stslib.dynamicdynamic;

import basemod.BaseMod;
import basemod.abstracts.DynamicVariable;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.HashMap;

public class DynamicDynamicVariable extends DynamicVariable {
    String key;
    DynamicProvider provider;

    public DynamicDynamicVariable(String key, DynamicProvider mod) {
        this.key = key;
        this.provider = mod;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public boolean isModified(AbstractCard card) {
        return provider.isModified(card);
    }

    @Override
    public int value(AbstractCard card) {
        return provider.value(card);
    }

    @Override
    public int baseValue(AbstractCard card) {
        return provider.baseValue(card);
    }

    @Override
    public Color getNormalColor() {
        Color color = provider.getNormalColor();
        return color != null ? color : super.getNormalColor();
    }

    @Override
    public Color getIncreasedValueColor() {
        Color color = provider.getIncreasedValueColor();
        return color != null ? color : super.getIncreasedValueColor();
    }

    @Override
    public Color getDecreasedValueColor() {
        Color color = provider.getDecreasedValueColor();
        return color != null ? color : super.getDecreasedValueColor();
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        return false; //this is never used
    }

    //variable management helpers
    public static HashMap<String, DynamicDynamicVariable> inherentVariableDatabase = new HashMap<>(); //will contain variables found in the card library
    public static HashMap<String, DynamicDynamicVariable> mainDeckVariableDatabase = new HashMap<>();
    public static HashMap<String, DynamicDynamicVariable> temporaryVariableDatabase = new HashMap<>();

    public static void clearTemporaryVariables() {
        for (String id : temporaryVariableDatabase.keySet()) {
            BaseMod.cardDynamicVariableMap.remove(id);
        }
        temporaryVariableDatabase.clear();
    }

    public static void clearMasterDeckVariables() {
        for (String id : mainDeckVariableDatabase.keySet()) {
            BaseMod.cardDynamicVariableMap.remove(id);
        }
        mainDeckVariableDatabase.clear();
    }

    public static void registerVariable(AbstractCard card, DynamicProvider mod) {
        HashMap<String, DynamicDynamicVariable> variableMap = AbstractDungeon.player == null ? inherentVariableDatabase : AbstractDungeon.player.masterDeck.contains(card) ? mainDeckVariableDatabase : temporaryVariableDatabase;
        String key = DynamicProvider.generateKey(card, mod);
        if (!variableMap.containsKey(key)) {
            DynamicDynamicVariable variable = new DynamicDynamicVariable(key, mod);
            variableMap.put(key, variable);
            BaseMod.cardDynamicVariableMap.put(key, variable);
        }
    }
}

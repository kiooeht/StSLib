package com.evacipated.cardcrawl.mod.stslib.dynamicdynamic;

import basemod.BaseMod;
import basemod.abstracts.DynamicVariable;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;

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
    public static HashMap<String, DynamicDynamicVariable> variableDatabase = new HashMap<>();

    public static void clearVariables() {
        for (String id : variableDatabase.keySet()) {
            BaseMod.cardDynamicVariableMap.remove(id);
        }
        variableDatabase.clear();
    }

    public static void registerVariable(AbstractCard card, DynamicProvider mod) {
        if (!variableDatabase.containsKey(mod.getKey())) {
            DynamicDynamicVariable variable = new DynamicDynamicVariable(mod.getKey(), mod);
            variableDatabase.put(mod.getKey(), variable);
            BaseMod.cardDynamicVariableMap.put(mod.getKey(), variable);
        }
    }

    public static String generateKey(AbstractCard card, DynamicProvider mod) {
        String key = "stslib:" + card.uuid + ":" + mod.getDynamicUUID();
        mod.setKey(key);
        return key;
    }
}

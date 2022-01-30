package com.evacipated.cardcrawl.mod.stslib.damagemods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DamageModContainer {
    private final List<AbstractDamageModifier> damageModifiers;
    private final Object instigator;

    public DamageModContainer(Object instigator, AbstractDamageModifier... damageModifiers) {
        this(instigator, new ArrayList<>(Arrays.asList(damageModifiers)));
    }

    public DamageModContainer(Object instigator, List<AbstractDamageModifier> damageModifiers) {
        this.instigator = instigator;
        this.damageModifiers = damageModifiers;
        Collections.sort(this.damageModifiers);
    }

    public List<AbstractDamageModifier> modifiers() {
        return damageModifiers;
    }

    public Object instigator() {
        return instigator;
    }

    public void addModifier(AbstractDamageModifier damageMod) {
        damageModifiers.add(damageMod);
        Collections.sort(damageModifiers);
    }

    public void addModifiers(List<AbstractDamageModifier> damageMods) {
        damageModifiers.addAll(damageMods);
        Collections.sort(damageModifiers);
    }

    public void removeModifier(AbstractDamageModifier damageMod) {
        damageModifiers.remove(damageMod);
    }

    public void removeModifiers(ArrayList<AbstractDamageModifier> damageMods) {
        damageModifiers.removeAll(damageMods);
    }

    public void clearModifiers() {
        damageModifiers.clear();
    }
}

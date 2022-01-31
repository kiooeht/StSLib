package com.evacipated.cardcrawl.mod.stslib.blockmods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BlockModContainer {
    private final List<AbstractBlockModifier> blockModifiers;
    private final Object instigator;

    public BlockModContainer(Object instigator, AbstractBlockModifier... blockModifiers) {
        this(instigator, new ArrayList<>(Arrays.asList(blockModifiers)));
    }

    public BlockModContainer(Object instigator, List<AbstractBlockModifier> blockModifiers) {
        this.instigator = instigator;
        this.blockModifiers = blockModifiers;
        Collections.sort(this.blockModifiers);
    }

    public List<AbstractBlockModifier> modifiers() {
        return blockModifiers;
    }

    public Object instigator() {
        return instigator;
    }

    public void addModifier(AbstractBlockModifier blockMod) {
        blockModifiers.add(blockMod);
        Collections.sort(blockModifiers);
    }

    public void addModifiers(List<AbstractBlockModifier> blockMods) {
        blockModifiers.addAll(blockMods);
        Collections.sort(blockModifiers);
    }

    public void removeModifier(AbstractBlockModifier blockMod) {
        blockModifiers.remove(blockMod);
    }

    public void removeModifiers(ArrayList<AbstractBlockModifier> blockMods) {
        blockModifiers.removeAll(blockMods);
    }

    public void clearModifiers() {
        blockModifiers.clear();
    }
}

package com.evacipated.cardcrawl.mod.stslib.powers.interfaces;

import com.evacipated.cardcrawl.mod.stslib.blockmods.AbstractBlockModifier;

import java.util.HashSet;

public interface OnCreateBlockInstancePower {
    void onCreateBlockInstance(HashSet<AbstractBlockModifier> blockTypes, Object instigator);
}

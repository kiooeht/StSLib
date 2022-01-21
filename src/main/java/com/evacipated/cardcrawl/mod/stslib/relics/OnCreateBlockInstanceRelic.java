package com.evacipated.cardcrawl.mod.stslib.relics;

import com.evacipated.cardcrawl.mod.stslib.blockmods.AbstractBlockModifier;

import java.util.HashSet;

public interface OnCreateBlockInstanceRelic {
    void onCreateBlockInstance(HashSet<AbstractBlockModifier> blockTypes, Object instigator);
}

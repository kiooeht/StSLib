package com.evacipated.cardcrawl.mod.stslib.relics;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.megacrit.cardcrawl.cards.DamageInfo;

import java.util.List;

public interface DamageModApplyingRelic {
    default void onAddedDamageModsToDamageInfo(DamageInfo info, Object instigator) {}

    boolean shouldPushMods(DamageInfo infoMayBeNull, Object instigator, List<AbstractDamageModifier> activeDamageModifiers);

    List<AbstractDamageModifier> modsToPush(DamageInfo infoMayBeNull, Object instigator, List<AbstractDamageModifier> activeDamageModifiers);
}

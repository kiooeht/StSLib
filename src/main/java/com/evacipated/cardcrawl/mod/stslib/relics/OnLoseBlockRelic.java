package com.evacipated.cardcrawl.mod.stslib.relics;

import com.megacrit.cardcrawl.cards.DamageInfo;

public interface OnLoseBlockRelic
{
    int onLoseBlock(DamageInfo info, int damageAmount);
}

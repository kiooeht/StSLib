package com.evacipated.cardcrawl.mod.stslib.powers.interfaces;

import com.megacrit.cardcrawl.cards.DamageInfo;

public interface OnLoseBlockPower
{
    int onLoseBlock(DamageInfo info, int damageAmount);
}

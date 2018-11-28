package com.evacipated.cardcrawl.mod.stslib.powers.interfaces;

import com.megacrit.cardcrawl.cards.DamageInfo;

public interface OnLoseTempHpPower
{
    int onLoseTempHp(DamageInfo info, int damageAmount);
}

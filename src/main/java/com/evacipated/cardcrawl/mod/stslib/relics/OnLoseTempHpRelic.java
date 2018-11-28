package com.evacipated.cardcrawl.mod.stslib.relics;

import com.megacrit.cardcrawl.cards.DamageInfo;

public interface OnLoseTempHpRelic
{
    int onLoseTempHp(DamageInfo info, int damageAmount);
}

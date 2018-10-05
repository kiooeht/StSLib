package com.evacipated.cardcrawl.mod.stslib.relics;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public interface OnReceivePowerRelic
{
    // return: true to continue
    //         false to negate the power, stopping it from applying
    boolean onReceivePower(AbstractPower power, AbstractCreature source);
}

package com.evacipated.cardcrawl.mod.stslib.powers.interfaces;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public interface BetterOnApplyPowerPower
{
    // return: true to continue
    //         false to negate the power, stopping it from applying
    boolean betterOnApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source);
}

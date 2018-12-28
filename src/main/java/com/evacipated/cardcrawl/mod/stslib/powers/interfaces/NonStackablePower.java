package com.evacipated.cardcrawl.mod.stslib.powers.interfaces;

import com.megacrit.cardcrawl.powers.AbstractPower;

public interface NonStackablePower
{
    default boolean isStackable(AbstractPower power)
    {
        return false;
    }
}

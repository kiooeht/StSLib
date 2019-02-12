package com.evacipated.cardcrawl.mod.stslib.powers.interfaces;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public interface OnReceivePowerPower
{
    /**
     * @param power  The power the target is receiving
     * @param target The target receiving the power
     * @param source The source applying the power
     * @return       Whether or not to apply the power (true = apply, false = negate)
     */
    boolean onReceivePower(AbstractPower power, AbstractCreature target, AbstractCreature source);

    /**
     * @param power       The power the target is receiving
     * @param target      The target receiving the power
     * @param source      The source applying the power
     * @param stackAmount The amount to stack the power if the owner already has it
     * @return            Whether or not to apply the power (true = apply, false = negate)
     */
    default int onReceivePowerStacks(AbstractPower power, AbstractCreature target, AbstractCreature source, int stackAmount)
    {
        return stackAmount;
    }
}

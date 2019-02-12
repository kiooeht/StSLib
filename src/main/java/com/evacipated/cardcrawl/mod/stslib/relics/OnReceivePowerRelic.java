package com.evacipated.cardcrawl.mod.stslib.relics;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public interface OnReceivePowerRelic
{
    /**
     * @param power  The power the player is receiving
     * @param source The source applying the power
     * @return       Whether or not to apply the power (true = apply, false = negate)
     */
    boolean onReceivePower(AbstractPower power, AbstractCreature source);

    /**
     * @param power       The power the player is receiving
     * @param source      The source applying the power
     * @param stackAmount The amount to stack the power if the player already has it
     * @return            The new stackAmount value
     */
    default int onReceivePowerStacks(AbstractPower power, AbstractCreature source, int stackAmount)
    {
        return stackAmount;
    }
}

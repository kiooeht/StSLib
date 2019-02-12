package com.evacipated.cardcrawl.mod.stslib.powers.interfaces;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public interface BetterOnApplyPowerPower
{
    /**
     * @param power  The power the source is applying
     * @param target The target receiving the power
     * @param source The source applying the power
     * @return       Whether or not to apply the power (true = apply, false = negate)
     */
    boolean betterOnApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source);

    /**
     * @param power       The power the source is applying
     * @param target      The target receiving the power
     * @param source      The source applying the power
     * @param stackAmount The amount to stack the power if the owner already has it
     * @return            Whether or not to apply the power (true = apply, false = negate)
     */
    default int betterOnApplyPowerStacks(AbstractPower power, AbstractCreature target, AbstractCreature source, int stackAmount)
    {
        return stackAmount;
    }
}

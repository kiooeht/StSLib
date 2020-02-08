package com.evacipated.cardcrawl.mod.stslib.relics;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public interface OnApplyPowerRelic {
    /**
     * @param power  The power the source is applying
     * @param target The target receiving the power
     * @param source The source applying the power
     * @return       Whether or not to apply the power (true = apply, false = negate)
     */
    boolean onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source);

    /**
     * @param power       The power the source is applying
     * @param target      The target receiving the power
     * @param source      The source applying the power
     * @param stackAmount The amount to stack the power if the owner already has it
     * @return            Allows changing the stackAmount.
     */
    default int onApplyPowerStacks(AbstractPower power, AbstractCreature target, AbstractCreature source, int stackAmount)
    {
        return stackAmount;
    }
}

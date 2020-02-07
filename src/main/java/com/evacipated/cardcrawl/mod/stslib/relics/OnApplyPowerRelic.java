package com.evacipated.cardcrawl.mod.stslib.relics;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public interface OnApplyPowerRelic {
    /**
     * @param source The source applying the power
     * @param target The target the power is applied to
     * @param power  The power the target is receiving
     */
    void onApplyPower(AbstractCreature source, AbstractCreature target, AbstractPower power);
}

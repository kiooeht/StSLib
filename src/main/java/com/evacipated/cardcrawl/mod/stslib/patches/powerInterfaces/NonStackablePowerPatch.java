package com.evacipated.cardcrawl.mod.stslib.patches.powerInterfaces;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.NonStackablePower;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.powers.AbstractPower;

@SpirePatch(
        clz= AbstractPower.class,
        method="shouldStackPower"
)
public class NonStackablePowerPatch
{
    public static boolean Postfix(AbstractPower __instance, AbstractPower power) {
        if (__instance instanceof NonStackablePower) {
            if (((NonStackablePower) __instance).isStackable(power)) {
                return false;
            }
            else {
                return true;
            }
        }
        else {
            return true;
        }
    }
}

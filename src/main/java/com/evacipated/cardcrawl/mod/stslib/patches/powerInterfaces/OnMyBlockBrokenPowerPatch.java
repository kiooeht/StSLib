package com.evacipated.cardcrawl.mod.stslib.patches.powerInterfaces;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnMyBlockBrokenPower;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

@SpirePatch(
        clz=AbstractCreature.class,
        method="brokeBlock"
)
public class OnMyBlockBrokenPowerPatch
{
    public static void Prefix(AbstractCreature __instance)
    {
        for (AbstractPower power : __instance.powers) {
            if (power instanceof OnMyBlockBrokenPower) {
                ((OnMyBlockBrokenPower) power).onMyBlockBroken();
            }
        }
    }
}

package com.evacipated.cardcrawl.mod.stslib.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

@SpirePatch(
        clz=InputHelper.class,
        method="updateLast"
)
public class FixJustReleasedRightClick
{
    public static void Postfix()
    {
        InputHelper.justReleasedClickRight = false;
    }
}

package com.evacipated.cardcrawl.mod.stslib.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

@SpirePatch(
        clz=Hitbox.class,
        method=SpirePatch.CLASS
)
public class HitboxRightClick
{
    public static SpireField<Boolean> rightClicked = new SpireField<>(() -> false);
    public static SpireField<Boolean> rightClickStarted = new SpireField<>(() -> false);

    @SpirePatch(
            clz=Hitbox.class,
            method="update",
            paramtypez={}
    )
    public static class Update
    {
        public static void Postfix(Hitbox __instance)
        {
            if (rightClicked.get(__instance)) {
                rightClicked.set(__instance, false);
            } else {
                if (rightClickStarted.get(__instance) && InputHelper.justReleasedClickRight) {
                    if (__instance.hovered) {
                        rightClicked.set(__instance, true);
                    }
                    rightClickStarted.set(__instance, false);
                }

                if (__instance.hovered && InputHelper.justClickedRight) {
                    rightClickStarted.set(__instance, true);
                }
            }
        }
    }
}

package com.evacipated.cardcrawl.mod.stslib.patches.cardInterfaces;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.mod.stslib.cards.interfaces.OnObtainCard;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.vfx.FastCardObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

public class OnObtainCardPatches {
    @SpirePatch(clz = ShowCardAndObtainEffect.class, method = "update")
    public static class OnPickupCardDoStuffPatch {
        public static void Postfix(ShowCardAndObtainEffect __instance) {
            AbstractCard q = ReflectionHacks.getPrivate(__instance, ShowCardAndObtainEffect.class, "card");
            if (__instance.isDone && q instanceof OnObtainCard) {
                ((OnObtainCard) q).onObtainCard();
            }
        }
    }

    @SpirePatch(clz = FastCardObtainEffect.class, method = "update")
    public static class OnPickupCardDoStuffPatch2 {
        public static void Postfix(FastCardObtainEffect __instance) {
            AbstractCard q = ReflectionHacks.getPrivate(__instance, FastCardObtainEffect.class, "card");
            if (__instance.isDone && q instanceof OnObtainCard) {
                ((OnObtainCard) q).onObtainCard();
            }
        }
    }
}

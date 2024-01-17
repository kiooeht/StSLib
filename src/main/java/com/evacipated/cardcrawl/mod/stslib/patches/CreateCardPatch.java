package com.evacipated.cardcrawl.mod.stslib.patches;

import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDrawPileEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
public class CreateCardPatch {
    @SpirePatch2(clz = ShowCardAndAddToDiscardEffect.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class, float.class, float.class})
    @SpirePatch2(clz = ShowCardAndAddToDrawPileEffect.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class, boolean.class, boolean.class})
    public static class CatchSourceCard {
        public static void Postfix(AbstractGameEffect __instance, AbstractCard srcCard) {
            StSLib.onCreateCard(srcCard);
        }
    }

    @SpirePatch2(clz = ShowCardAndAddToDiscardEffect.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class})
    @SpirePatch2(clz = ShowCardAndAddToDrawPileEffect.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class, float.class, float.class, boolean.class, boolean.class, boolean.class})
    @SpirePatch2(clz = ShowCardAndAddToHandEffect.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class, float.class, float.class})
    @SpirePatch2(clz = ShowCardAndAddToHandEffect.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class})
    public static class CatchCopyCard {
        public static void Postfix(AbstractGameEffect __instance, AbstractCard ___card) {
            StSLib.onCreateCard(___card);
        }
    }
}

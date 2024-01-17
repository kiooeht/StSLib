package com.evacipated.cardcrawl.mod.stslib.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.patches.cardInterfaces.MultiUpgradePatches;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;

@SpirePatch2(
        clz = SingleCardViewPopup.class,
        method = "render"
)
public class SingleCardViewRenderPatch {

    @SpirePostfixPatch
    public static void postfixFix(SingleCardViewPopup __instance, SpriteBatch sb, AbstractCard ___card) {
        MultiUpgradePatches.RenderTreeSCV.renderTree(__instance, sb, ___card);
    }

}

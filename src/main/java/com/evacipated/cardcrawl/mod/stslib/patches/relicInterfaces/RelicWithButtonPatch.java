package com.evacipated.cardcrawl.mod.stslib.patches.relicInterfaces;

import basemod.abstracts.CustomEnergyOrb;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableForRelic;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;

// Thanks to Alison for how to patch this in for proper update timing
public class RelicWithButtonPatch {
    @SpirePatch2(clz = CustomEnergyOrb.class, method = "renderOrb")
    public static class RenderElementAfterOrb {
        @SpirePostfixPatch
        public static void renderPls(SpriteBatch sb) {
            for (ClickableForRelic clicky : ClickableForRelic.getClickableList())
                clicky.render(sb);
        }
    }

    @SpirePatch2(clz = CustomEnergyOrb.class, method = "updateOrb")
    public static class UpdateElementAfterOrb {
        @SpirePostfixPatch
        public static void updatePls() {
            for (ClickableForRelic clicky : ClickableForRelic.getClickableList())
                clicky.update();
        }
    }
}

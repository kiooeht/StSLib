package com.evacipated.cardcrawl.mod.stslib.patches.relicInterfaces;

import basemod.abstracts.CustomEnergyOrb;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelicWithUI;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

// Thanks to Alison for how to patch this in for proper update timing
public class RelicWithUIPatch {
    @SpirePatch2(clz = CustomEnergyOrb.class, method = "renderOrb")
    public static class RenderElementAfterOrb {
        @SpirePostfixPatch
        public static void renderPls(SpriteBatch sb) {
            if (AbstractDungeon.player == null || AbstractDungeon.player.relics == null)
                return;

            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (relic instanceof ClickableRelicWithUI) {
                    ClickableRelicWithUI cRelic = (ClickableRelicWithUI) relic;
                    cRelic.doRender(sb);
                }
            }
        }
    }

    @SpirePatch2(clz = CustomEnergyOrb.class, method = "updateOrb")
    public static class UpdateElementAfterOrb {
        @SpirePostfixPatch
        public static void updatePls() {
            if (AbstractDungeon.player == null || AbstractDungeon.player.relics == null)
                return;

            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (relic instanceof ClickableRelicWithUI)
                    ((ClickableRelicWithUI) relic).getElement().update();
            }
        }
    }
}

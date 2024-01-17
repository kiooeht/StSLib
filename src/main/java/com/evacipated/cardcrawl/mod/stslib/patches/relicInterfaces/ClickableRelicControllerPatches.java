package com.evacipated.cardcrawl.mod.stslib.patches.relicInterfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class ClickableRelicControllerPatches {
    @SpirePatch(clz = AbstractRelic.class, method = "renderInTopPanel", paramtypez = { SpriteBatch.class })
    public static class AbstractRelicRenderPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractRelic __instance, SpriteBatch ___sb) {
            if (Settings.hideRelics ||
                    !Settings.isControllerMode ||
                    !(__instance instanceof ClickableRelic) ||
                    (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.BOSS_REWARD && !__instance.isObtained) ||
                    !__instance.hb.hovered) {
                return;
            }

            float scale = Settings.scale;
            ___sb.setColor(1, 1, 1, 1);
            TextureRegion texture = new TextureRegion(CInputActionSet.topPanel.getKeyImg());
            ___sb.draw(texture,
                    __instance.currentX - 30 * scale - texture.getRegionWidth() / 2f,
                    __instance.currentY - 35 * scale - texture.getRegionHeight() / 2f,
                    texture.getRegionWidth() / 2f,
                    texture.getRegionHeight() / 2f,
                    texture.getRegionWidth(),
                    texture.getRegionHeight(),
                    scale * 0.85f,
                    scale * 0.85f,
                    0);
        }
    }

    @SpirePatch(clz = TopPanel.class, method = "update")
    public static class DisablePotionButtonPatch {
        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("isJustPressed")) {
                        m.replace(String.format("$_ = $proceed($$) && !%s.isClickableRelicHovered($0, null);", ClickableRelicControllerPatches.class.getName()));
                    }
                }
            };
        }
    }

    @SpirePatch(clz = TopPanel.class, method = "renderControllerUi")
    public static class HidePotionButtonPatch {
        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("draw")) {
                        m.replace(String.format("if (!%s.isClickableRelicHovered(null, $1)) { $_ = $proceed($$); }", ClickableRelicControllerPatches.class.getName()));
                    }
                }
            };
        }
    }

    public static boolean isClickableRelicHovered(Object action, Object image) {
        if (!Settings.isControllerMode ||
                (action != null && action != CInputActionSet.topPanel) ||
                (image != null && image != CInputActionSet.topPanel.getKeyImg())) {
            return false;
        }

        if (AbstractDungeon.player != null && AbstractDungeon.player.relics != null) {
            return AbstractDungeon.player.relics.stream().anyMatch(r -> r.hb.hovered && r instanceof ClickableRelic);
        }

        return false;
    }
}

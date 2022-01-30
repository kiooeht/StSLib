package com.evacipated.cardcrawl.mod.stslib.patches;

import basemod.ClickableUIElement;
import basemod.helpers.TooltipInfo;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.blockmods.BlockInstance;
import com.evacipated.cardcrawl.mod.stslib.blockmods.BlockModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;

import java.util.ArrayList;
import java.util.Collections;

public class RenderStackedBlockInstances {
    private static final Texture blankTex = new Texture("images/blank.png");
    private static final float dx = 50f * Settings.scale;
    private static final float dy = 40f * Settings.scale;

    @SpirePatch(clz = AbstractCreature.class, method = SpirePatch.CLASS)
    public static class BlockStackElementField {
        public static SpireField<BlockStackElement> element = new SpireField<>(() -> null);
    }


    @SpirePatch(clz = AbstractCreature.class, method = "renderBlockIconAndValue")
    public static class RenderStackedIcons {
        @SpirePostfixPatch()
        public static void pls(AbstractCreature __instance, SpriteBatch sb, float x, float y, float ___BLOCK_ICON_X, float ___BLOCK_ICON_Y, float ___blockOffset, Color ___blockTextColor, Color ___blockOutlineColor, float ___blockScale) {
            if (BlockStackElementField.element.get(__instance) == null) {
                BlockStackElementField.element.set(__instance, new BlockStackElement(__instance));
            }
            if (BlockModifierManager.blockInstances(__instance).stream().allMatch(BlockInstance::defaultBlock)) {
                return;
            }
            int offsetY = 0;
            ArrayList<BlockInstance> instances = new ArrayList<>(BlockModifierManager.blockInstances(__instance));
            Collections.reverse(instances);
            for (BlockInstance b : instances) {
                if (b.getBlockColor() != null) {
                    sb.setColor(b.getBlockColor());
                } else {
                    sb.setColor(___blockOutlineColor);
                }
                sb.draw(b.getBlockImage(), x + ___BLOCK_ICON_X - 32.0F - dx, y + ___BLOCK_ICON_Y - 32.0F + offsetY +___blockOffset, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
                FontHelper.renderFontCentered(sb, FontHelper.blockInfoFont, Integer.toString(b.getBlockAmount()), x + ___BLOCK_ICON_X - dx, y + offsetY - 16.0F * Settings.scale, b.getTextColor()!=null?b.getTextColor():___blockTextColor, ___blockScale);
                if (offsetY == 0) {
                    FontHelper.renderFontCentered(sb, FontHelper.blockInfoFont, "=", x + ___BLOCK_ICON_X - dx/2F, y - 16.0F * Settings.scale, ___blockTextColor, ___blockScale);
                } else {
                    FontHelper.renderFontCentered(sb, FontHelper.blockInfoFont, "+", x + ___BLOCK_ICON_X - dx, y + offsetY - dy/2F - 16.0F * Settings.scale, ___blockTextColor, ___blockScale);
                }
                offsetY += dy;
            }
            BlockStackElement e = BlockStackElementField.element.get(__instance);
            e.setHitboxHeight(offsetY);
            e.move(x + ___BLOCK_ICON_X - 32.0F - dx, y + ___BLOCK_ICON_Y - 32.0F +___blockOffset);
            e.update();
        }
    }

    public static class BlockStackElement extends ClickableUIElement {
        private static final float baseHeight = 60f;
        private static final float baseWidth = 100f * Settings.scale;
        private final AbstractCreature owner;
        public BlockStackElement(AbstractCreature owner) {
            super(blankTex, 0, 0, baseWidth, baseHeight);
            this.owner = owner;
        }

        public void move(float x, float y) {
            move(x, y, 0);
        }

        private void move(float x, float y, float d) {
            this.setX(x-(d * Settings.scale));
            this.setY(y-(d * Settings.scale));
        }

        public void setHitboxHeight(float height){
            this.hitbox.resize(baseWidth, baseHeight + height);
        }

        @Override
        protected void onHover() {
            ArrayList<PowerTip> tips = new ArrayList<>();
            for (BlockInstance b : BlockModifierManager.blockInstances(owner)) {
                tips.add(new TooltipInfo(b.makeName(), b.makeDescription()).toPowerTip());
            }
            if (!tips.isEmpty()) {
                if (owner.hb.cX + hitbox.width / 2.0F < 1544.0F * Settings.scale) {
                    TipHelper.queuePowerTips(owner.hb.cX + owner.hb.width / 2.0F + 20.0F * Settings.scale, owner.hb.cY  + TipHelper.calculateAdditionalOffset(tips, owner.hb.cY), tips);
                } else {
                    TipHelper.queuePowerTips(owner.hb.cX - owner.hb.width / 2.0F + -380.0F * Settings.scale, owner.hb.cY  + TipHelper.calculateAdditionalOffset(tips, owner.hb.cY), tips);
                }
            }
        }

        @Override
        protected void onUnhover() {}

        @Override
        protected void onClick() {}
    }
}

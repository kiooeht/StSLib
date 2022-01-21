package com.evacipated.cardcrawl.mod.stslib.patches;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.DamageModApplyingPower;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.ArrayList;

public class RenderElementsOnCardPatches {
    private static final float UPSCALE_CONSTANT = 4F/3F;

    //Don't bother rendering if it isn't in one of 4 immediately viewable locations. We also don't want to render in master deck
    public static boolean validLocation(AbstractCard c) {
        return AbstractDungeon.player.hand.contains(c) ||
                AbstractDungeon.player.drawPile.contains(c) ||
                AbstractDungeon.player.discardPile.contains(c) ||
                AbstractDungeon.player.exhaustPile.contains(c);
    }

    @SpirePatch(clz = AbstractCard.class, method = "renderEnergy")
    public static class RenderOnCardPatch {
        @SpirePostfixPatch
        public static void RenderOnCard(AbstractCard __instance, SpriteBatch sb) {
            if (AbstractDungeon.player != null && validLocation(__instance)) {
                renderHelper(sb, __instance.current_x, __instance.current_y, __instance);
            }
        }

        private static void renderHelper(SpriteBatch sb, float drawX, float drawY, AbstractCard card) {
            ArrayList<AbstractDamageModifier> mods = new ArrayList<>();
            for (AbstractDamageModifier mod : DamageModifierManager.modifiers(card)) {
                if (mod.shouldPushIconToCard(card) && mod.getAccompanyingIcon() != null) {
                    mods.add(mod);
                }
            }
            for (AbstractPower p : AbstractDungeon.player.powers) {
                if (p instanceof DamageModApplyingPower && ((DamageModApplyingPower) p).shouldPushMods(null, card, DamageModifierManager.modifiers(card))) {
                    for (AbstractDamageModifier mod : ((DamageModApplyingPower) p).modsToPush(null, card, DamageModifierManager.modifiers(card))) {
                        if (mod.shouldPushIconToCard(card) && mod.getAccompanyingIcon() != null) {
                            mods.add(mod);
                        }
                    }
                }
            }
            if (!mods.isEmpty()) {
                sb.setColor(Color.WHITE.cpy());
                float dx = -(mods.size()-1)*UPSCALE_CONSTANT * AbstractCustomIcon.RENDER_CONSTANT / 2F;
                float dy = 210f;
                for (AbstractDamageModifier mod : mods) {
                    mod.getAccompanyingIcon().render(sb, drawX, drawY, dx, dy, UPSCALE_CONSTANT*card.drawScale*Settings.scale, card.angle);
                    dx += UPSCALE_CONSTANT * AbstractCustomIcon.RENDER_CONSTANT;
                }
            }
        }
    }
}

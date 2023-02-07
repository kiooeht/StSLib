package com.evacipated.cardcrawl.mod.stslib.patches.relicInterfaces;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.relics.CardRewardSkipButton;
import com.evacipated.cardcrawl.mod.stslib.relics.CardRewardSkipButtonRelic;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.CardRewardScreen;

import java.util.ArrayList;

public class CardRewardSkipButtonPatches {


    private static void positionButtons(ArrayList<CardRewardSkipButton> buttons) {
        ArrayList<Float> Xs = new ArrayList<>();
        float trueWidth = ImageMaster.REWARD_SCREEN_TAKE_BUTTON.getWidth()* 0.6f * Settings.scale;
        float diffX = ImageMaster.REWARD_SCREEN_TAKE_BUTTON.getWidth() * 0.2f * Settings.scale;
        float Y = (float) Settings.HEIGHT / 2.0F - 340.0F * Settings.scale;
        Y -= ImageMaster.REWARD_SCREEN_TAKE_BUTTON.getHeight() * 0.85f * Settings.scale;
        float pointerX = Settings.WIDTH/2f - diffX;
        float totalWidth = 0;
        for (CardRewardSkipButton button : buttons) {
            Xs.add(pointerX);
            button.move(pointerX, Y);
            pointerX += trueWidth;
            totalWidth += trueWidth;
        }
        for (CardRewardSkipButton button : buttons) {
            button.setX(Xs.get(buttons.indexOf(button)) - totalWidth/2f);
        }
    }

    @SpirePatch(clz = CardRewardScreen.class, method = SpirePatch.CLASS)
    public static class ButtonsField {
        public static SpireField<ArrayList<CardRewardSkipButton>> buttons = new SpireField<>(ArrayList::new);
    }


    @SpirePatch2(clz = CardRewardScreen.class, method = "open")
    public static class OpenPatch {

        @SpirePostfixPatch
        public static void addButtons(CardRewardScreen __instance) {
            ButtonsField.buttons.get(__instance).clear();
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (r instanceof CardRewardSkipButtonRelic && (((CardRewardSkipButtonRelic) r).shouldShowButton(__instance))) {
                    CardRewardSkipButtonRelic bR = (CardRewardSkipButtonRelic)r;
                    CardRewardSkipButton button = new CardRewardSkipButton(bR);
                    ButtonsField.buttons.get(__instance).add(button);
                }
            }
            positionButtons(ButtonsField.buttons.get(__instance));

        }
    }



    @SpirePatch2(clz = CardRewardScreen.class, method = "update")
    public static class UpdatePatch {

        @SpirePostfixPatch
        public static void updateButtons(CardRewardScreen __instance) {
            for (CardRewardSkipButton button : ButtonsField.buttons.get(__instance)) {
                button.update();
            }

        }
    }

    @SpirePatch2(clz = CardRewardScreen.class, method = "render")
    public static class RenderPatch {

        @SpirePostfixPatch
        public static void renderButtons(CardRewardScreen __instance, SpriteBatch sb) {
            for (CardRewardSkipButton button : ButtonsField.buttons.get(__instance)) {
                button.render(sb);
            }
        }
    }

}

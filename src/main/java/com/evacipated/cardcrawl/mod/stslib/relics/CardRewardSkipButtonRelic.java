package com.evacipated.cardcrawl.mod.stslib.relics;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.CardRewardScreen;

public interface CardRewardSkipButtonRelic {

    void onClickedButton();

    String getButtonLabel();

    default boolean shouldShowButton(CardRewardScreen screen) {
        return isActualCardReward(screen);
    }

    static boolean isActualCardReward(CardRewardScreen screen) {
        boolean draft = ReflectionHacks.getPrivate(screen, CardRewardScreen.class, "draft");
        boolean codex = ReflectionHacks.getPrivate(screen, CardRewardScreen.class, "codex");
        boolean discovery = ReflectionHacks.getPrivate(screen, CardRewardScreen.class, "discovery");
        boolean chooseOne = ReflectionHacks.getPrivate(screen, CardRewardScreen.class, "chooseOne");
        return !draft && !codex && !discovery && !chooseOne;
    }

    default Texture getTexture() {
        return ImageMaster.REWARD_SCREEN_TAKE_BUTTON;
    }

}

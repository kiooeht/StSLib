package com.evacipated.cardcrawl.mod.stslib.relics;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public interface CardRewardSkipButtonRelic {

    void onClickedButton();

    String getButtonLabel();

    default Texture getTexture() {
        return ImageMaster.REWARD_SCREEN_TAKE_BUTTON;
    }

}

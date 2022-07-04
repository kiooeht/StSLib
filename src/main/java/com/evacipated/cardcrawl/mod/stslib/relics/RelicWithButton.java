package com.evacipated.cardcrawl.mod.stslib.relics;

import basemod.AutoAdd;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.helpers.PowerTip;

import java.util.ArrayList;

@AutoAdd.Ignore
public interface RelicWithButton {
    Texture getTexture();

    void onButtonPress();

    boolean isButtonDisabled();

    ArrayList<PowerTip> getHoverTips();
}

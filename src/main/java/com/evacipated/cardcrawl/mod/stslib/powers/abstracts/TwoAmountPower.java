package com.evacipated.cardcrawl.mod.stslib.powers.abstracts;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.powers.AbstractPower;

public abstract class TwoAmountPower extends AbstractPower
{
    public int amount2 = 0;

    @Override
    public void renderAmount(SpriteBatch sb, float x, float y, Color c)
    {
        super.renderAmount(sb, x, y, c);

        if (amount2 > 0) {
            FontHelper.renderFontRightTopAligned(sb, FontHelper.powerAmountFont,
                    Integer.toString(amount2), x, y + 15 * Settings.scale, fontScale, c);
        }
    }
}

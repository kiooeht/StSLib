package com.evacipated.cardcrawl.mod.stslib.powers.abstracts;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.powers.AbstractPower;

public abstract class TwoAmountPower extends AbstractPower
{
    public int amount2 = 0;
    private Color redColor = Color.RED.cpy();
    private Color greenColor = Color.GREEN.cpy();
    public boolean canGoNegative2 = false;

    @Override
    public void renderAmount(SpriteBatch sb, float x, float y, Color c)
    {
        super.renderAmount(sb, x, y, c);

        if (amount2 > 0) {
            if (!isTurnBased) {
                greenColor.a = c.a;
                c = greenColor;
            }
            FontHelper.renderFontRightTopAligned(sb, FontHelper.powerAmountFont,
                    Integer.toString(amount2), x, y + 15 * Settings.scale, fontScale, c);
            redColor.a = c.a;
            c = redColor;
        } else if (amount2 < 0 && canGoNegative2) {
            FontHelper.renderFontRightTopAligned(sb, FontHelper.powerAmountFont,
                    Integer.toString(amount2), x, y + 15 * Settings.scale, fontScale, c);
        }
    }
}

package com.evacipated.cardcrawl.mod.stslib.powers.abstracts;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.powers.AbstractPower;

public abstract class TwoAmountPower extends AbstractPower
{
    public int amount2 = 0;
    public boolean canGoNegative2 = false;
    protected Color redColor2 = Color.RED.cpy();
    protected Color greenColor2 = Color.GREEN.cpy();

    @Override
    public void renderAmount(SpriteBatch sb, float x, float y, Color c)
    {
        super.renderAmount(sb, x, y, c);

        if (amount2 > 0) {
            if (!isTurnBased) {
                greenColor2.a = c.a;
                c = greenColor2;
            }
            FontHelper.renderFontRightTopAligned(sb, FontHelper.powerAmountFont,
                    Integer.toString(amount2), x, y + 15 * Settings.scale, fontScale, c);
        } else if (amount2 < 0 && canGoNegative2) {
            redColor2.a = c.a;
            c = redColor2;
            FontHelper.renderFontRightTopAligned(sb, FontHelper.powerAmountFont,
                    Integer.toString(amount2), x, y + 15 * Settings.scale, fontScale, c);
        }
    }
}

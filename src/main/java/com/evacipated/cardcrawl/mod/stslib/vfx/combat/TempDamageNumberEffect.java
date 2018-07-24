package com.evacipated.cardcrawl.mod.stslib.vfx.combat;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.combat.DamageNumberEffect;

public class TempDamageNumberEffect extends DamageNumberEffect
{
    private Color originalColor;

    public TempDamageNumberEffect(AbstractCreature target, float x, float y, int amt)
    {
        super(target, x, y, amt);
        color = Settings.GOLD_COLOR.cpy();
        originalColor = color.cpy();
    }

    @Override
    public void update()
    {
        super.update();
        color = originalColor.cpy();
    }
}

package com.evacipated.cardcrawl.mod.stslib.powers.interfaces;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public interface OnPlayerDeathPower
{
    /**
     * @return       Whether or not to kill the player (true = kill, false = live)
     */
    boolean onPlayerDeath(AbstractPlayer p, DamageInfo damageInfo);
}

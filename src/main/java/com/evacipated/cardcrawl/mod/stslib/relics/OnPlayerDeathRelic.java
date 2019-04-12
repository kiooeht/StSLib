package com.evacipated.cardcrawl.mod.stslib.relics;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public interface OnPlayerDeathRelic
{
    /**
     * @return       Whether or not to kill the player (true = kill, false = live)
     */
    boolean onPlayerDeath(AbstractPlayer p, DamageInfo damageInfo);
}

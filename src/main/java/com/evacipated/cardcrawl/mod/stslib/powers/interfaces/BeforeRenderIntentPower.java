package com.evacipated.cardcrawl.mod.stslib.powers.interfaces;

import com.megacrit.cardcrawl.monsters.AbstractMonster;

public interface BeforeRenderIntentPower {
    /**
     * @param monster      The monster that the intent is about to be rendered for
     * @return            Allows stopping the intent from rendering (false = don't render)
     */
    boolean beforeRenderIntent(AbstractMonster monster);
}

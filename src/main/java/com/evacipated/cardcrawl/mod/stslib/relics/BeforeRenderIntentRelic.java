package com.evacipated.cardcrawl.mod.stslib.relics;

import com.megacrit.cardcrawl.monsters.AbstractMonster;

public interface BeforeRenderIntentRelic {
    /**
     * @param monster      The monster that the intent is about to be rendered for
     * @return            Allows stopping the intent from rendering (false = don't render)
     */
    default boolean beforeRenderIntent(AbstractMonster monster)
    {
        return true;
    }
}
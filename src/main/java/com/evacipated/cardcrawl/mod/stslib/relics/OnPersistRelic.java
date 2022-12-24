package com.evacipated.cardcrawl.mod.stslib.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnPersistRelic
{
    void onPersist(AbstractCard card, int persistCount);
}

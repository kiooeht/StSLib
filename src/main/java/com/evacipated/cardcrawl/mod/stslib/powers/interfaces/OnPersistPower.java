package com.evacipated.cardcrawl.mod.stslib.powers.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnPersistPower
{
    void onPersist(AbstractCard card, int persistCount);
}

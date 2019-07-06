package com.evacipated.cardcrawl.mod.stslib.powers.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;

@Deprecated
public interface OnCardDrawPower
{
    void onCardDraw(AbstractCard drawnCard);
}

package com.evacipated.cardcrawl.mod.stslib.patches.bothInterfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface onCreateCardInterface {
    default void onCreateCard(AbstractCard card){}
    default void onCreateThisCard(){}
}

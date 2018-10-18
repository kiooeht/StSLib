package com.evacipated.cardcrawl.mod.stslib.relics;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnAfterUseCardRelic
{
    void onAfterUseCard(AbstractCard card, UseCardAction action);
}

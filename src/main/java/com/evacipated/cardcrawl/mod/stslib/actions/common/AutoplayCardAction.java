package com.evacipated.cardcrawl.mod.stslib.actions.common;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.QueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class AutoplayCardAction extends AbstractGameAction
{
    private AbstractCard card;
    private CardGroup group;

    public AutoplayCardAction(AbstractCard card, CardGroup group)
    {
        this.card = card;
        this.group = group;
    }

    @Override
    public void update()
    {
        isDone = true;
        if (group.contains(card)) {
            card.targetAngle = 0;
            AbstractDungeon.actionManager.addToTop(new QueueCardAction(card, null));
        }
    }
}

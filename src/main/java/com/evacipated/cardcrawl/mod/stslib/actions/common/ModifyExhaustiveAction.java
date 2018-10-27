package com.evacipated.cardcrawl.mod.stslib.actions.common;

import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.ExhaustiveField;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ModifyExhaustiveAction extends AbstractGameAction
{
    private AbstractCard card;
    private boolean bound;

    public ModifyExhaustiveAction(AbstractCard card, int amount)
    {
        this(card, amount, true);
    }

    public ModifyExhaustiveAction(AbstractCard card, int amount, boolean bound)
    {
        this.amount = amount;
        this.card = card;
        this.bound = bound;
    }

    public void update()
    {
        ExhaustiveField.ExhaustiveFields.exhaustive.set(card, ExhaustiveField.ExhaustiveFields.exhaustive.get(card) + amount);
        if (ExhaustiveField.ExhaustiveFields.exhaustive.get(card) <= 0) {
            AbstractDungeon.actionManager.addToTop(new ExhaustSpecificCardAction(card, AbstractDungeon.player.hand));
            AbstractDungeon.actionManager.addToTop(new ExhaustSpecificCardAction(card, AbstractDungeon.player.drawPile));
            AbstractDungeon.actionManager.addToTop(new ExhaustSpecificCardAction(card, AbstractDungeon.player.discardPile));
        }
        if (this.bound && ExhaustiveField.ExhaustiveFields.exhaustive.get(card) > ExhaustiveField.ExhaustiveFields.baseExhaustive.get(card)) {
            ExhaustiveField.ExhaustiveFields.exhaustive.set(card, ExhaustiveField.ExhaustiveFields.baseExhaustive.get(card));
        }
        card.applyPowers();
        card.initializeDescription();
        isDone = true;
    }
}

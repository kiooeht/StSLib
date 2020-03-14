package com.evacipated.cardcrawl.mod.stslib.actions.common;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SelectCardsInHandAction
    extends AbstractGameAction
{

    private Predicate<AbstractCard> predicate;
    private Consumer<AbstractCard> callback;
    private String text;
    private boolean anyNumber, canPickZero;
    private ArrayList<AbstractCard> hand;
    private ArrayList<AbstractCard> tempHand;

    public SelectCardsInHandAction(int amount, String textForSelect, boolean anyNumber, boolean canPickZero, Predicate<AbstractCard> cardFilter, Consumer<AbstractCard> callback)
    {
        this.amount = amount;
        this.duration = this.startDuration = Settings.ACTION_DUR_XFAST;
        text = textForSelect;
        this.anyNumber = anyNumber;
        this.canPickZero = canPickZero;
        this.predicate = cardFilter;
        this.callback = callback;
        this.hand = AbstractDungeon.player.hand.group;
        tempHand = new ArrayList<>();
        tempHand.addAll(hand);
    }

    public SelectCardsInHandAction(int amount, String textForSelect, Predicate<AbstractCard> cardFilter, Consumer<AbstractCard> callback)
    {
        this(amount, textForSelect, false, false, cardFilter, callback);
    }

    public SelectCardsInHandAction(int amount, String textForSelect, Consumer<AbstractCard> callback)
    {
        this(amount, textForSelect, false, false, (c -> true), callback);
    }

    public SelectCardsInHandAction(String textForSelect, Predicate<AbstractCard> cardFilter, Consumer<AbstractCard> callback)
    {
        this(1, textForSelect, false, false, cardFilter, callback);
    }

    public SelectCardsInHandAction(String textForSelect, Consumer<AbstractCard> callback)
    {
        this(1, textForSelect, false, false, (c -> true), callback);
    }

    @Override
    public void update() {
        if (this.duration == this.startDuration)
        {
            if ((hand.size() == 0) || (hand.stream().noneMatch(predicate)) || callback == null)
            {
                isDone = true;
                return;
            }

            tempHand.removeIf(predicate);

            if (tempHand.size() > 0) hand.removeIf(c -> tempHand.contains(c));

            if (!anyNumber && !canPickZero && hand.size() <= amount)
            {

                returnCards();
                isDone = true;
                return;
            }

            AbstractDungeon.handCardSelectScreen.open(text, amount, anyNumber, canPickZero);
            tickDuration();
            return;
        }

        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved)
        {
            AbstractDungeon.handCardSelectScreen.selectedCards.group.forEach(c -> {callback.accept(c); hand.add(c);});
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
            returnCards();
            isDone = true;
            return;
        }
        tickDuration();
    }

    private void returnCards()
    {
        if (tempHand.size() > 0) hand.addAll(tempHand);
        AbstractDungeon.player.hand.refreshHandLayout();
        AbstractDungeon.player.hand.applyPowers();
    }
}

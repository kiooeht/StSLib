package com.evacipated.cardcrawl.mod.stslib.actions.common;

import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MoveCardsAction extends AbstractGameAction
{
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("stslib:MoveCardsAction");
    public static final String[] TEXT = uiStrings.TEXT;
    private AbstractPlayer p;
    private CardGroup source;
    private CardGroup destination;
    private Predicate<AbstractCard> predicate;
    private Consumer<List<AbstractCard>> callback;
    private boolean sortCards = false;

    public MoveCardsAction(CardGroup destination, CardGroup source, Predicate<AbstractCard> predicate, int amount, Consumer<List<AbstractCard>> callback)
    {
        p = AbstractDungeon.player;
        this.destination = destination;
        this.source = source;
        this.predicate = predicate;
        this.callback = callback;
        setValues(p, AbstractDungeon.player, amount);
        actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
        duration = Settings.ACTION_DUR_MED;
    }

    public MoveCardsAction(CardGroup destination, CardGroup source, Predicate<AbstractCard> predicate, Consumer<List<AbstractCard>> callback)
    {
        this(destination, source, predicate, 1, callback);
    }

    public MoveCardsAction(CardGroup destination, CardGroup source, int amount, Consumer<List<AbstractCard>> callback)
    {
        this(destination, source, c -> true, amount, callback);
    }

    public MoveCardsAction(CardGroup destination, CardGroup source, Consumer<List<AbstractCard>> callback)
    {
        this(destination, source, c -> true, 1, callback);
    }

    public MoveCardsAction(CardGroup destination, CardGroup source, Predicate<AbstractCard> predicate, int amount)
    {
        this (destination, source, predicate, amount, null);
    }

    public MoveCardsAction(CardGroup destination, CardGroup source, Predicate<AbstractCard> predicate)
    {
        this(destination, source, predicate, 1);
    }

    public MoveCardsAction(CardGroup destination, CardGroup source, int amount)
    {
        this(destination, source, c -> true, amount);
    }

    public MoveCardsAction(CardGroup destination, CardGroup source)
    {
        this(destination, source, c -> true, 1);
    }

    public MoveCardsAction sort(boolean sortCards)
    {
        this.sortCards = sortCards;
        return this;
    }

    public void update()
    {
        CardGroup tmp;
        if (duration == Settings.ACTION_DUR_MED) {
            tmp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractCard c : source.group) {
                if (predicate.test(c)) {
                    if (source == p.drawPile) {
                        tmp.addToRandomSpot(c);
                    } else {
                        tmp.addToTop(c);
                    }
                }
            }
            if (tmp.size() == 0) {
                isDone = true;
                return;
            }
            if (tmp.size() == 1) {
                AbstractCard card = tmp.getTopCard();
                if (source == p.exhaustPile) {
                    card.unfadeOut();
                }
                if (destination == p.hand && p.hand.size() == BaseMod.MAX_HAND_SIZE) {
                    source.moveToDiscardPile(card);
                    p.createHandIsFullDialog();
                } else {
                    card.untip();
                    card.unhover();
                    card.lighten(true);
                    card.setAngle(0.0F);
                    card.drawScale = 0.12F;
                    card.targetDrawScale = 0.75F;
                    card.current_x = CardGroup.DRAW_PILE_X;
                    card.current_y = CardGroup.DRAW_PILE_Y;
                    source.removeCard(card);
                    destination.addToTop(card);
                    AbstractDungeon.player.hand.refreshHandLayout();
                    AbstractDungeon.player.hand.applyPowers();
                }
                List<AbstractCard> callbackList = new ArrayList<>();
                callbackList.add(card);
                if (callback != null) {
                    callback.accept(callbackList);
                }
                isDone = true;
                return;
            }
            if (tmp.size() <= amount) {
                List<AbstractCard> callbackList = new ArrayList<>();
                for (int i = 0; i < tmp.size(); ++i) {
                    AbstractCard card = tmp.getNCardFromTop(i);
                    callbackList.add(card);
                    if (source == p.exhaustPile) {
                        card.unfadeOut();
                    }
                    if (destination == p.hand && p.hand.size() == BaseMod.MAX_HAND_SIZE) {
                        source.moveToDiscardPile(card);
                        p.createHandIsFullDialog();
                    } else {
                        card.untip();
                        card.unhover();
                        card.lighten(true);
                        card.setAngle(0.0F);
                        card.drawScale = 0.12F;
                        card.targetDrawScale = 0.75F;
                        card.current_x = CardGroup.DRAW_PILE_X;
                        card.current_y = CardGroup.DRAW_PILE_Y;
                        source.removeCard(card);
                        destination.addToTop(card);
                        p.hand.refreshHandLayout();
                        p.hand.applyPowers();
                    }
                }
                if (callback != null) {
                    callback.accept(callbackList);
                }
                isDone = true;
                return;
            }
            if (sortCards) {
                tmp.sortAlphabetically(true);
                tmp.sortByRarityPlusStatusCardType(true);
            }
            AbstractDungeon.gridSelectScreen.open(tmp, amount, makeText(), false);
            tickDuration();
            return;
        }
        if (AbstractDungeon.gridSelectScreen.selectedCards.size() != 0) {
            List<AbstractCard> callbackList = new ArrayList<>();
            for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                callbackList.add(c);
                c.untip();
                c.unhover();
                if (source == p.exhaustPile) {
                    c.unfadeOut();
                }
                if (destination == p.hand && p.hand.size() == BaseMod.MAX_HAND_SIZE) {
                    source.moveToDiscardPile(c);
                    p.createHandIsFullDialog();
                } else {
                    source.removeCard(c);
                    destination.addToTop(c);
                }
                p.hand.refreshHandLayout();
                p.hand.applyPowers();
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            p.hand.refreshHandLayout();

            if (callback != null) {
                callback.accept(callbackList);
            }
        }
        tickDuration();
    }

    private String makeText()
    {
        String ret;
        if (amount == 1) {
            ret = TEXT[0];
        } else {
            ret = TEXT[1];
        }

        String location = null;
        if (destination == p.hand) {
            location = uiStrings.TEXT_DICT.get("HAND");
        } else if (destination == p.drawPile) {
            location = uiStrings.TEXT_DICT.get("DRAW");
        } else if (destination == p.discardPile) {
            location = uiStrings.TEXT_DICT.get("DISCARD");
        } else if (destination == p.exhaustPile) {
            location = uiStrings.TEXT_DICT.get("EXHAUST");
        }
        if (location == null) {
            location = "<Unknown>";
        }

        return String.format(ret, location);
    }
}

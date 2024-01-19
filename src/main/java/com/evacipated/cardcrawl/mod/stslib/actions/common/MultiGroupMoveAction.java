package com.evacipated.cardcrawl.mod.stslib.actions.common;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.function.Predicate;

public class MultiGroupMoveAction extends MultiGroupSelectAction {
    private static final int EXHAUST_PILE_X, EXHAUST_PILE_Y;

    static {
        EXHAUST_PILE_X = (int) (Settings.WIDTH - 40.0F * Settings.scale);
        EXHAUST_PILE_Y = (int) (200 * Settings.scale);
    }

    protected static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("stslib:MoveCardsAction");
    private static final String[] TEXT = uiStrings.TEXT;
    public static final String HAND = uiStrings.TEXT_DICT.get("HAND");
    public static final String DRAW_PILE = uiStrings.TEXT_DICT.get("DRAW");
    public static final String DISCARD_PILE = uiStrings.TEXT_DICT.get("DISCARD");
    public static final String EXHAUST_PILE = uiStrings.TEXT_DICT.get("EXHAUST");
    public static final String UNKNOWN = "???";

    public MultiGroupMoveAction(CardGroup.CardGroupType destination, int amount, CardGroup.CardGroupType... sources) {
        this(destination, amount, false, (c)->true, sources);
    }

    public MultiGroupMoveAction(CardGroup.CardGroupType destination, int amount, boolean anyNumber, CardGroup.CardGroupType... sources) {
        this(destination, amount, anyNumber, (c)->true, sources);
    }

    public MultiGroupMoveAction(CardGroup.CardGroupType destination, int amount, Predicate<AbstractCard> canSelect, CardGroup.CardGroupType... sources) {
        this(destination, amount, false, canSelect, sources);
    }

    public MultiGroupMoveAction(CardGroup.CardGroupType destination, int amount, boolean anyNumber, Predicate<AbstractCard> canSelect, CardGroup.CardGroupType... sources) {
        super(makeText(amount, destination), (cards, sourceMap)->{
            for (AbstractCard c : cards) {
                moveToDestination(sourceMap.getOrDefault(c, AbstractDungeon.player.limbo), destination, c);
            }
        }, amount, anyNumber, canSelect, sources);
    }

    private static void moveToDestination(CardGroup source, CardGroup.CardGroupType destination, AbstractCard c) {
        if (source.type == CardGroup.CardGroupType.EXHAUST_PILE)
            c.unfadeOut();

        switch (destination) {
            case HAND:
                source.moveToHand(c);
                setCardPosition(source, c);
                break;
            case DRAW_PILE:
                source.moveToDeck(c, true);
                setCardPosition(source, c);
                break;
            case DISCARD_PILE:
                source.moveToDiscardPile(c);
                setCardPosition(source, c);
                break;
            case EXHAUST_PILE:
                source.moveToExhaustPile(c);
                setCardPosition(source, c);
                break;
            default:
                System.out.println("MultiGroupMoveAction attempting to move to cardgroup of invalid type: " + destination.name());
                break;
        }
    }

    private static void setCardPosition(CardGroup source, AbstractCard c) {
        switch (source.type) {
            case DRAW_PILE:
                c.current_x = CardGroup.DRAW_PILE_X;
                c.current_y = CardGroup.DRAW_PILE_Y;
                break;
            case DISCARD_PILE:
                c.current_x = CardGroup.DISCARD_PILE_X;
                c.current_y = CardGroup.DISCARD_PILE_Y;
                break;
            case EXHAUST_PILE:
                c.current_x = EXHAUST_PILE_X;
                c.current_y = EXHAUST_PILE_Y;
                break;
        }
        //Hand: Don't need to move. Anything else: what the heck
    }

    private static String makeText(int amount, CardGroup.CardGroupType destination) {
        String ret;
        if (amount == 1) {
            ret = TEXT[0];
        } else {
            ret = TEXT[1];
        }

        String location = UNKNOWN;
        switch (destination) {
            case HAND:
                location = HAND;
                break;
            case DRAW_PILE:
                location = DRAW_PILE;
                break;
            case DISCARD_PILE:
                location = DISCARD_PILE;
                break;
            case EXHAUST_PILE:
                location = EXHAUST_PILE;
                break;
        }
        return String.format(ret, location);
    }
}

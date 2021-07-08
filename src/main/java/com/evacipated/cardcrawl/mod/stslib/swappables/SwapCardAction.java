package com.evacipated.cardcrawl.mod.stslib.swappables;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public class SwapCardAction extends AbstractGameAction {
    private AbstractCard card1;
    private AbstractCard card2;
    private int index;

    public SwapCardAction(AbstractCard originalCard, AbstractCard swapCard, int spotInHand) {
        this.actionType = ActionType.SPECIAL;
        this.duration = Settings.ACTION_DUR_MED;
        this.card1 = originalCard;
        this.card2 = swapCard;
        this.index = spotInHand;
    }

    @Override
    public void update() {
        AbstractPlayer p = AbstractDungeon.player;
        p.hoveredCard = card2;
        p.hand.group.remove(index);
        p.hand.group.add(index, card2);
        if (card1 instanceof SwappableCard) {
            ((SwappableCard)card1).onSwapOut();
        }
        if (card2.target == AbstractCard.CardTarget.ENEMY || card2.target == AbstractCard.CardTarget.SELF_AND_ENEMY) {
            p.inSingleTargetMode = true;
            p.isDraggingCard = false;
            GameCursor.hidden = true;
            p.hand.refreshHandLayout();
            card2.current_x = card2.target_x;
            card2.current_y = card2.target_y;
        } else {
            p.inSingleTargetMode = false;
            p.isDraggingCard = true;
            GameCursor.hidden = false;
            card2.current_x = card1.current_x;
            card2.current_y = card1.current_y;
            card2.target_x = InputHelper.mX;
            card2.target_y = InputHelper.mY;
        }
        if (card2 instanceof SwappableCard) {
            ((SwappableCard)card2).onSwapIn();
        }
        card2.isGlowing = card1.isGlowing;
        card1.isGlowing = false;
        card2.flash();
        for (AbstractCard handCard : p.hand.group) {
            handCard.applyPowers();
        }
        this.isDone = true;
    }
}

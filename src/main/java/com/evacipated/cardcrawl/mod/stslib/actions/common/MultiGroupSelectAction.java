package com.evacipated.cardcrawl.mod.stslib.actions.common;

import basemod.Pair;
import com.evacipated.cardcrawl.mod.stslib.patches.MultiGroupGridSelectPatches;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class MultiGroupSelectAction extends AbstractGameAction {
    protected final ArrayList<CardGroup> sources;
    protected final AbstractPlayer p;

    protected String selectionText;
    private final HashMap<AbstractCard, CardGroup> cardSourceMap = new HashMap<>();
    private BiConsumer<List<AbstractCard>, Map<AbstractCard, CardGroup>> callback;

    protected boolean first;
    protected boolean anyNumber;
    protected final Predicate<AbstractCard> canSelect;

    public MultiGroupSelectAction(String selectText, BiConsumer<List<AbstractCard>, Map<AbstractCard, CardGroup>> callback, int amount, CardGroup.CardGroupType... sources) {
        this(selectText, callback, amount, false, (c)->true, sources);
    }

    public MultiGroupSelectAction(String selectText, BiConsumer<List<AbstractCard>, Map<AbstractCard, CardGroup>> callback, int amount, boolean anyNumber, CardGroup.CardGroupType... sources) {
        this(selectText, callback, amount, anyNumber, (c)->true, sources);
    }

    public MultiGroupSelectAction(String selectText, BiConsumer<List<AbstractCard>, Map<AbstractCard, CardGroup>> callback, int amount, Predicate<AbstractCard> canSelect, CardGroup.CardGroupType... sources) {
        this(selectText, callback, amount, false, canSelect, sources);
    }

    public MultiGroupSelectAction(String selectText, BiConsumer<List<AbstractCard>, Map<AbstractCard, CardGroup>> callback, int amount, boolean anyNumber, Predicate<AbstractCard> canSelect, CardGroup.CardGroupType... sources) {
        this.amount = amount;
        this.p = AbstractDungeon.player;

        this.actionType = ActionType.CARD_MANIPULATION;

        this.selectionText = selectText;

        this.sources = new ArrayList<>();
        for (CardGroup.CardGroupType groupType : sources) {
            this.sources.add(getGroup(groupType));
        }

        this.callback = callback;
        this.canSelect = canSelect;
        this.anyNumber = anyNumber;

        first = true;
    }

    public MultiGroupSelectAction chainCallback(BiConsumer<List<AbstractCard>, Map<AbstractCard, CardGroup>> callback) {
        BiConsumer<List<AbstractCard>, Map<AbstractCard, CardGroup>> original = this.callback;
        this.callback = (cards, sourceMap) -> {
            original.accept(cards, sourceMap);
            callback.accept(cards, sourceMap);
        };
        return this;
    }

    @Override
    public void update() {
        if (AbstractDungeon.getCurrRoom().isBattleEnding())
        {
            this.isDone = true;
            return;
        }
        if (first) {
            first = false;

            if (sources.isEmpty()) {
                this.isDone = true;
                return;
            }

            CardGroup selectGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            ArrayList<Pair<CardGroup.CardGroupType, Integer>> groupIndexes = new ArrayList<>();

            for (CardGroup group : sources) {
                int amt = 0;
                for (AbstractCard c : group.group) {
                    if (canSelect.test(c)) {
                        ++amt;
                        selectGroup.addToTop(c);
                        cardSourceMap.put(c, group);
                    }
                }

                if (amt > 0)
                    groupIndexes.add(new Pair<>(group.type, selectGroup.size()));
            }

            if (selectGroup.isEmpty()) {
                this.isDone = true;
                return;
            }

            if (selectGroup.size() <= amount) {
                amount = selectGroup.size();

                if (!anyNumber) {
                    callback.accept(selectGroup.group, cardSourceMap);
                    this.isDone = true;
                    return;
                }
            }

            //GridCardSelect patches to display what group cards are from and separate different groups slightly
            MultiGroupGridSelectPatches.Fields.groupIndexes.set(selectGroup, groupIndexes);

            if (anyNumber) {
                AbstractDungeon.gridSelectScreen.open(selectGroup, amount, true, this.selectionText);
            }
            else {
                AbstractDungeon.gridSelectScreen.open(selectGroup, amount, this.selectionText, false, false, false, false);
            }
            for (AbstractCard c : selectGroup.group) {
                c.unhover();
                c.stopGlowing();
            }
        }
        else {
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                HashMap<AbstractCard, CardGroup> filteredSourceMap = new HashMap<>();
                for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                    filteredSourceMap.put(c, cardSourceMap.getOrDefault(c, AbstractDungeon.player.limbo));
                }
                callback.accept(AbstractDungeon.gridSelectScreen.selectedCards, filteredSourceMap);

                AbstractDungeon.gridSelectScreen.selectedCards.clear();
            }
            this.isDone = true;
        }
    }

    private CardGroup getGroup(CardGroup.CardGroupType groupType) {
        switch (groupType) {
            case HAND:
                return AbstractDungeon.player.hand;
            case DRAW_PILE:
                return AbstractDungeon.player.drawPile;
            case DISCARD_PILE:
                return AbstractDungeon.player.discardPile;
            case EXHAUST_PILE:
                return AbstractDungeon.player.exhaustPile;
            default:
                System.out.println("MultiGroupSelectAction attempting to get cardgroup of invalid type: " + groupType.name());
                return AbstractDungeon.player.hand;
        }
    }
}

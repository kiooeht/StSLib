package com.evacipated.cardcrawl.mod.stslib.util;

import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class CardVertex {
    public final ArrayList<CardVertex> parents = new ArrayList<>();
    public final ArrayList<CardVertex> children = new ArrayList<>();
    public final ArrayList<CardVertex> exclusions = new ArrayList<>();
    public final int index;
    public final AbstractCard card;
    public int x, y;
    public boolean strict;

    public CardVertex(AbstractCard card, int index) {
        this(card, index, true);
    }

    public CardVertex(AbstractCard card, int index, boolean strict) {
        this.card = card;
        this.index = index;
        x = y = 0;
        this.strict = strict;
    }

    public void addParent(CardVertex v) {
        parents.add(v);
    }

    public void addChild(CardVertex v) {
        children.add(v);
    }

    public void addExclusion(CardVertex v) {
        exclusions.add(v);
    }

    public void move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void clear() {
        parents.clear();
        children.clear();
        exclusions.clear();
    }
}

package com.evacipated.cardcrawl.mod.stslib.util;

import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;
import java.util.HashMap;

public class CardGraph {
    public final ArrayList<CardVertex> vertices = new ArrayList<>();
    public final ArrayList<AbstractCard> cards = new ArrayList<>();

    public void addVertex(CardVertex v) {
        vertices.add(v);
        cards.add(v.card);
    }

    public void addDependence(CardVertex from, CardVertex to) {
        from.addParent(to);
        to.addChild(from);
    }

    public void addExclusion(CardVertex from, CardVertex to) {
        from.addExclusion(to);
        to.addExclusion(from);
    }

    public ArrayList<AbstractCard> containedCards() {
        return cards;
    }

    public CardVertex getVertexByCard(AbstractCard card) {
        for (CardVertex v : vertices) {
            if (v.card == card) {
                return v;
            }
        }
        return null;
    }

    public void clear() {
        for (CardVertex v : vertices) {
            v.clear();
        }
        vertices.clear();
        cards.clear();
    }

    public int depth() {
        int x = 0;
        for (CardVertex v : vertices) {
            if (v.x > x) {
                x = v.x;
            }
        }
        return x;
    }

    public int height() {
        HashMap<Integer, Integer> heightMap = new HashMap<>();
        for (CardVertex v : vertices) {
            heightMap.put(v.x, heightMap.getOrDefault(v.x, 0)+1);
        }
        int big = 0;
        for (int i : heightMap.values()) {
            if (i > big) {
                big = i;
            }
        }
        return big;
    }
}

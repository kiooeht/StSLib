package com.evacipated.cardcrawl.mod.stslib.icons;

import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeMap;

public class CustomIconHelper {
    private static final HashMap<String, AbstractCustomIcon> icons = new HashMap<>();

    public static void addCustomIcon(AbstractCustomIcon icon) {
        icons.put(icon.cardCode().toLowerCase(), icon);
    }

    public static AbstractCustomIcon getIcon(String key) {
        return icons.get(key.toLowerCase());
    }

    public static Collection<AbstractCustomIcon> getAllIcons() {
        return icons.values();
    }

    public static ArrayList<AbstractCustomIcon> iconsOnCard(AbstractCard card) {
        TreeMap<Integer, AbstractCustomIcon> icons = new TreeMap<>();
        for (AbstractCustomIcon i : getAllIcons()) {
            int indexOf = card.rawDescription.indexOf(i.cardCode());
            if (indexOf > -1) {
                icons.put(indexOf, i);
            }
        }
        return new ArrayList<>(icons.values());
    }
}

package com.evacipated.cardcrawl.mod.stslib.icons;

import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

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
        ArrayList<AbstractCustomIcon> icons = new ArrayList<>();
        for (AbstractCustomIcon i : getAllIcons()) {
            if (card.rawDescription.contains(i.cardCode())) {
                icons.add(i);
            }
        }
        return icons;
    }
}

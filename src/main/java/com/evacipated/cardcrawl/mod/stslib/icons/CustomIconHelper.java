package com.evacipated.cardcrawl.mod.stslib.icons;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomIconHelper {
    private static final ArrayList<AbstractCustomIcon> allIcons = new ArrayList<>();
    private static final HashMap<String, AbstractCustomIcon> icons = new HashMap<>();

    public static void addCustomIcon(AbstractCustomIcon icon) {
        allIcons.add(icon);
        icons.put(icon.cardCode().toLowerCase(), icon);
    }

    public static AbstractCustomIcon getIcon(String key) {
        return icons.get(key.toLowerCase());
    }

    public static ArrayList<AbstractCustomIcon> getAllIcons() {
        return allIcons;
    }
}

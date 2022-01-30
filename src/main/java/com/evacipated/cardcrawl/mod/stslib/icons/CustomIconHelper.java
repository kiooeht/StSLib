package com.evacipated.cardcrawl.mod.stslib.icons;

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
}

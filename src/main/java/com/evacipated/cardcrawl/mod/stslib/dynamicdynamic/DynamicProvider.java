package com.evacipated.cardcrawl.mod.stslib.dynamicdynamic;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import basemod.abstracts.DynamicVariable;

import java.util.UUID;

public interface DynamicProvider {

    /**
     * this will be used in {@link DynamicProvider#generateKey(AbstractCard, DynamicProvider) generateKey} to make pairing unique to both the card applied and the effect.
     * @return an uuid unique to the instance of the effect providing the dynamic variable.
     */
    UUID getDynamicUUID();

    /**
     * all following values and methods are used the same as any equivalent methods in {@link DynamicVariable}
     */
    boolean isModified(AbstractCard card);

    int value(AbstractCard card);

    int baseValue(AbstractCard card);

    default Color getNormalColor() {
        return null;
    }

    default Color getIncreasedValueColor() {
        return null;
    }

    default Color getDecreasedValueColor() {
        return null;
    }


    /**
     * use these helper methods to get the key that would be used to retrieve the dynamic variable.
     */
    static String generateKey(AbstractCard card, DynamicProvider mod) {
        return generateKey(card, mod, false);
    }

    static String generateKey(AbstractCard card, DynamicProvider mod, boolean forText) {
        String retVal = "stslib:" + card.uuid + ":" + mod.getDynamicUUID();
        if (forText) {
            retVal = "!" + retVal + "!";
        }
        return retVal;
    }

}

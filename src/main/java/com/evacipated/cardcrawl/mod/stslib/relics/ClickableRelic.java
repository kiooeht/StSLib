package com.evacipated.cardcrawl.mod.stslib.relics;

import com.evacipated.cardcrawl.mod.stslib.patches.HitboxRightClick;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public interface ClickableRelic
{
    class data
    {
        private static final String ID = "stslib:Clickable";
        private static final String[] DESCRIPTIONS;
        static
        {
            RelicStrings relicStrings = CardCrawlGame.languagePack.getRelicStrings(ID);
            DESCRIPTIONS = relicStrings.DESCRIPTIONS;
        }
    }

    default String[] CLICKABLE_DESCRIPTIONS()
    {
        return data.DESCRIPTIONS;
    }

    void onRightClick();

    default void clickUpdate()
    {
        if (this instanceof AbstractRelic) {
            AbstractRelic relic = (AbstractRelic) this;
            if (HitboxRightClick.rightClicked.get(relic.hb)) {
                onRightClick();
            }
        } else {
            throw new NotImplementedException();
        }
    }

    default boolean hovered()
    {
        if (this instanceof AbstractRelic) {
            AbstractRelic relic = (AbstractRelic) this;
            return relic.hb.hovered;
        }
        throw new NotImplementedException();
    }
}

package com.evacipated.cardcrawl.mod.stslib;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;

public class RelicTools
{
    public static ArrayList<String> relicTierPool(AbstractRelic.RelicTier tier)
    {
        switch (tier) {
            case COMMON:
                return AbstractDungeon.commonRelicPool;
            case UNCOMMON:
                return AbstractDungeon.uncommonRelicPool;
            case RARE:
                return AbstractDungeon.rareRelicPool;
            case BOSS:
                return AbstractDungeon.bossRelicPool;
            case SHOP:
                return AbstractDungeon.shopRelicPool;
            default:
                return null;
        }
    }

    public static boolean returnRelicToPool(AbstractRelic.RelicTier tier, String relicID)
    {
        ArrayList<String> pool = relicTierPool(tier);
        if (pool != null && !pool.isEmpty()) {
            int insertAt = AbstractDungeon.relicRng.random(1, pool.size()-1);
            pool.add(insertAt, relicID);
            return true;
        }
        return false;
    }

    public static boolean returnRelicToPool(String relicID)
    {
        AbstractRelic.RelicTier tier = RelicLibrary.getRelic(relicID).tier;
        return returnRelicToPool(tier, relicID);
    }
}

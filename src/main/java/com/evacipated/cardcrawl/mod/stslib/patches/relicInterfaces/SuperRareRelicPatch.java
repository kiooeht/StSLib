package com.evacipated.cardcrawl.mod.stslib.patches.relicInterfaces;

import com.evacipated.cardcrawl.mod.stslib.RelicTools;
import com.evacipated.cardcrawl.mod.stslib.relics.SuperRareRelic;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class SuperRareRelicPatch
{
    @SpirePatch(
            clz=AbstractDungeon.class,
            method="returnRandomRelicKey"
    )
    public static class RandomRelic
    {
        private static int depth = 0;

        public static String Postfix(String __result, AbstractRelic.RelicTier tier)
        {
            if (depth == 0 && RelicLibrary.getRelic(__result) instanceof SuperRareRelic) {
                RelicTools.returnRelicToPool(tier, __result);
                ++depth;
                __result = AbstractDungeon.returnRandomRelicKey(tier);
                --depth;
            }

            return __result;
        }
    }

    @SpirePatch(
            clz=AbstractDungeon.class,
            method="returnEndRandomRelicKey"
    )
    public static class EndRandomRelic
    {
        private static int depth = 0;

        public static String Postfix(String __result, AbstractRelic.RelicTier tier)
        {
            if (depth == 0 && RelicLibrary.getRelic(__result) instanceof SuperRareRelic) {
                RelicTools.returnRelicToPool(tier, __result);
                ++depth;
                __result = AbstractDungeon.returnEndRandomRelicKey(tier);
                --depth;
            }

            return __result;
        }
    }
}

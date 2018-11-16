package com.evacipated.cardcrawl.mod.stslib.patches.relicInterfaces;

import com.evacipated.cardcrawl.mod.stslib.relics.ActualOnSmithRelic;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.campfire.CampfireSmithEffect;

@SpirePatch(clz = CampfireSmithEffect.class, method = "update")
public class ActualOnSmithRelicPatch {
    @SpireInsertPatch(rloc=13)
    public static void Insert(CampfireSmithEffect __instance) {
        for (AbstractRelic r : AbstractDungeon.player.relics) {
            if (r instanceof ActualOnSmithRelic) {
                ((ActualOnSmithRelic)r).actualOnSmith();
            }
        }
    }
}

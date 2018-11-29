package com.evacipated.cardcrawl.mod.stslib.patches.bothInterfaces;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnLoseBlockPower;
import com.evacipated.cardcrawl.mod.stslib.relics.OnLoseBlockRelic;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

@SpirePatch(
        clz=AbstractCreature.class,
        method="decrementBlock"
)
public class OnLoseBlockPatch
{
    public static SpireReturn<Integer> Prefix(AbstractCreature __instance, DamageInfo info, @ByRef int[] damageAmount)
    {
        for (AbstractPower power : __instance.powers) {
            if (power instanceof OnLoseBlockPower) {
                damageAmount[0] = ((OnLoseBlockPower) power).onLoseBlock(info, damageAmount[0]);
            }
        }
        if (__instance.isPlayer) {
            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (relic instanceof OnLoseBlockRelic) {
                    damageAmount[0] = ((OnLoseBlockRelic) relic).onLoseBlock(info, damageAmount[0]);
                }
            }
        }

        return SpireReturn.Continue();
    }
}

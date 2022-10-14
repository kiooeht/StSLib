package com.evacipated.cardcrawl.mod.stslib.patches.powerInterfaces;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnDrawPileShufflePower;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.actions.common.ShuffleAction;
import com.megacrit.cardcrawl.actions.defect.ShuffleAllAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class OnDrawPileShufflePowerPatch {
    @SpirePatch(clz = EmptyDeckShuffleAction.class, method = SpirePatch.CONSTRUCTOR)
    public static class ShufflePatchOne {
        public static void Postfix(EmptyDeckShuffleAction __instance) {
            for (AbstractPower p : AbstractDungeon.player.powers) {
                if (p instanceof OnDrawPileShufflePower) {
                    ((OnDrawPileShufflePower) p).onShuffle();
                }
            }
        }
    }

    @SpirePatch(clz = ShuffleAction.class, method = "update")
    public static class ShufflePatchTwo {
        public static void Postfix(ShuffleAction __instance) {
            boolean b = ReflectionHacks.getPrivate(__instance, ShuffleAction.class, "triggerRelics");
            if (b) {
                for (AbstractPower p : AbstractDungeon.player.powers) {
                    if (p instanceof OnDrawPileShufflePower) {
                        ((OnDrawPileShufflePower) p).onShuffle();
                    }
                }
            }
        }
    }

    @SpirePatch(clz = ShuffleAllAction.class, method = SpirePatch.CONSTRUCTOR)
    public static class ShufflePatchThree {
        public static void Postfix(ShuffleAllAction __instance) {
            for (AbstractPower p : AbstractDungeon.player.powers) {
                if (p instanceof OnDrawPileShufflePower) {
                    ((OnDrawPileShufflePower) p).onShuffle();
                }
            }
        }
    }
}

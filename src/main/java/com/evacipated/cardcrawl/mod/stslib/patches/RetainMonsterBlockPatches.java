package com.evacipated.cardcrawl.mod.stslib.patches;

import com.evacipated.cardcrawl.mod.stslib.blockmods.AbstractBlockModifier;
import com.evacipated.cardcrawl.mod.stslib.blockmods.BlockInstance;
import com.evacipated.cardcrawl.mod.stslib.blockmods.BlockModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import javassist.CtBehavior;

public class RetainMonsterBlockPatches {
    public static boolean monsterStartOfTurn = false;

    @SpirePatch(clz = MonsterGroup.class, method = "applyPreTurnLogic")
    public static class SetMonsterLoseBlockFlag {
        @SpireInsertPatch(locator = Locator.class)
        public static void SetFlag(MonsterGroup __instance) {
            monsterStartOfTurn = true;
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractMonster.class, "loseBlock");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
        @SpireInsertPatch(locator = Locator2.class)
        public static void ResetFlag(MonsterGroup __instance) {
            monsterStartOfTurn = false;
        }
        private static class Locator2 extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractMonster.class, "loseBlock");
                int[] i = LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
                i[0] += 1;
                return i;
            }
        }
    }

    @SpirePatch(clz = MonsterGroup.class, method = "applyPreTurnLogic")
    public static class PreBlockLossCall {
        @SpireInsertPatch(locator = Locator.class, localvars = "m")
        public static void preBlockLoss(MonsterGroup __instance, AbstractMonster m) {
            for (BlockInstance b : BlockModifierManager.blockInstances(m)) {
                for (AbstractBlockModifier mod : b.getBlockTypes()) {
                    mod.atStartOfTurnPreBlockLoss();
                }
            }
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractMonster.class, "hasPower");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(clz = AbstractCreature.class, method = "loseBlock", paramtypez = {int.class, boolean.class})
    public static class DecrementCustomBlockAmounts {
        @SpirePrefixPatch
        public static void pls(AbstractCreature __instance, @ByRef int[] amount) {
            if (monsterStartOfTurn && amount[0] > 0) {
                int tmp = amount[0];
                int removedAmount;
                //Specifically retain the block types that are not fully removed
                for (BlockInstance b : BlockModifierManager.blockInstances(__instance)) {
                    removedAmount = Math.min(b.getBlockAmount(), Math.min(b.computeStartTurnBlockLoss(), tmp));
                    for (AbstractBlockModifier m : b.getBlockTypes()) {
                        m.onStartOfTurnBlockLoss(removedAmount);
                    }

                    b.setBlockAmount(b.getBlockAmount() - removedAmount);
                    tmp -= removedAmount;
                    if (b.getBlockAmount() <= 0) {
                        for (AbstractBlockModifier m : b.getBlockTypes()) {
                            tmp = m.onRemove(true, null, tmp);
                        }
                    }
                    if (tmp <= 0) {
                        break;
                    }
                }
                BlockModifierManager.removeEmptyBlockInstances(__instance);
                amount[0] = BlockModifierManager.getBlockRetValBasedOnRemainingAmounts(__instance);
            }
        }
    }
}

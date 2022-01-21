package com.evacipated.cardcrawl.mod.stslib.patches;

import basemod.patches.com.megacrit.cardcrawl.actions.GameActionManager.OnPlayerLoseBlockToggle;
import basemod.patches.com.megacrit.cardcrawl.core.AbstractCreature.ModifyPlayerLoseBlock;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.blockmods.AbstractBlockModifier;
import com.evacipated.cardcrawl.mod.stslib.blockmods.BlockInstance;
import com.evacipated.cardcrawl.mod.stslib.blockmods.BlockModifierManager;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.evacipated.cardcrawl.mod.stslib.patches.bothInterfaces.OnReceivePowerPatch;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.BetterOnApplyPowerPower;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CtBehavior;

public class BlockModifierPatches {

    public static BlockInstance specificInstanceToReduce = null;

    @SpirePatch(clz = ModifyPlayerLoseBlock.class, method = "Prefix")
    public static class ModifyStartOfTurnBlockLossPatch {
        @SpirePostfixPatch
        public static void pls(AbstractCreature __instance, int[] amount, boolean noAnimation) {
            if (OnPlayerLoseBlockToggle.isEnabled && amount[0] > 0) {
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

    @SpirePatch(clz = AbstractCreature.class, method = "decrementBlock")
    public static class OnAttackPreBlockDamaged {
        @SpirePrefixPatch()
        public static void OnAttackedAndSaveInfo(AbstractCreature __instance, DamageInfo info, @ByRef int[] damageAmount) {
            BlockModifierManager.onAttacked(__instance, info, damageAmount[0]);
            if (info.type != DamageInfo.DamageType.HP_LOSS && DamageModifierManager.getDamageMods(info).stream().noneMatch(m -> m.ignoresBlock(__instance))) {
                int tmp = damageAmount[0];
                int removedAmount;
                boolean isStartTurnLostBlock = OnPlayerLoseBlockToggle.isEnabled;
                int backupIndex = -1;
                int reduction = 0;
                if (specificInstanceToReduce != null) {
                    backupIndex = BlockModifierManager.blockInstances(__instance).indexOf(specificInstanceToReduce);
                    BlockModifierManager.blockInstances(__instance).remove(backupIndex);
                    BlockModifierManager.blockInstances(__instance).add(0, specificInstanceToReduce);
                }
                if (!isStartTurnLostBlock && !RetainMonsterBlockPatches.monsterStartOfTurn) {
                    for (BlockInstance b : BlockModifierManager.blockInstances(__instance)) {
                        removedAmount = Math.min(tmp, b.getBlockAmount());
                        b.setBlockAmount(b.getBlockAmount() - removedAmount);
                        if (b != specificInstanceToReduce) {
                            for (AbstractBlockModifier m : b.getBlockTypes()) {
                                m.onThisBlockDamaged(info, removedAmount);
                            }
                        }
                        tmp -= removedAmount;
                        if (b.getBlockAmount() <= 0) {
                            int d = tmp;
                            for (AbstractBlockModifier m : b.getBlockTypes()) {
                                d = m.onRemove(false, info, d);
                            }
                            reduction += tmp - d;
                            tmp = d;
                        }
                        if (tmp <= 0 || reduction >= tmp) {
                            break;
                        }
                    }
                }
                if (specificInstanceToReduce != null) {
                    BlockModifierManager.blockInstances(__instance).remove(0);
                    BlockModifierManager.blockInstances(__instance).add(backupIndex, specificInstanceToReduce);
                    specificInstanceToReduce = null;
                }
                BlockModifierManager.removeEmptyBlockInstances(__instance);
                damageAmount[0] -= reduction;
                if (damageAmount[0] < 0) {
                    damageAmount[0] = 0;
                }
            }
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "calculateCardDamage")
    public static class PlayerDamageGivePatches {
        @SpireInsertPatch(locator = PlayerDamageGiveLocator.class, localvars = "tmp")
        public static void singleGive(AbstractCard __instance, AbstractMonster mo, @ByRef float[] tmp) {
            tmp[0] = BlockModifierManager.atDamageGive(AbstractDungeon.player, tmp[0], __instance.damageTypeForTurn, mo, __instance);
        }

        @SpireInsertPatch(locator = PlayerMultiDamageGiveLocator.class, localvars = {"tmp","i"})
        public static void multiGive(AbstractCard __instance, AbstractMonster mo, float[] tmp, int i) {
            tmp[i] = BlockModifierManager.atDamageGive(AbstractDungeon.player, tmp[i], __instance.damageTypeForTurn, AbstractDungeon.getMonsters().monsters.get(i), __instance);
        }

        @SpireInsertPatch(locator = PlayerDamageFinalGiveLocator.class, localvars = "tmp")
        public static void singleFinalGive(AbstractCard __instance, AbstractMonster mo, @ByRef float[] tmp) {
            tmp[0] = BlockModifierManager.atDamageFinalGive(AbstractDungeon.player, tmp[0], __instance.damageTypeForTurn, mo, __instance);
        }

        @SpireInsertPatch(locator = PlayerMultiDamageFinalGiveLocator.class, localvars = {"tmp","i"})
        public static void multiFinalGive(AbstractCard __instance, AbstractMonster mo, float[] tmp, int i) {
            tmp[i] = BlockModifierManager.atDamageFinalGive(AbstractDungeon.player, tmp[i], __instance.damageTypeForTurn, AbstractDungeon.getMonsters().monsters.get(i), __instance);
        }

        @SpireInsertPatch(locator = MonsterDamageReceiveLocator.class, localvars = "tmp")
        public static void singleReceive(AbstractCard __instance, AbstractMonster mo, @ByRef float[] tmp) {
            tmp[0] = BlockModifierManager.atDamageReceive(mo, tmp[0], __instance.damageTypeForTurn, AbstractDungeon.player);
        }

        @SpireInsertPatch(locator = MonsterMultiDamageReceiveLocator.class, localvars = {"tmp","i"})
        public static void multiReceive(AbstractCard __instance, AbstractMonster mo, float[] tmp, int i) {
            tmp[i] = BlockModifierManager.atDamageReceive(AbstractDungeon.getMonsters().monsters.get(i), tmp[i], __instance.damageTypeForTurn, AbstractDungeon.player);
        }

        @SpireInsertPatch(locator = MonsterDamageFinalReceiveLocator.class, localvars = "tmp")
        public static void singleFinalReceive(AbstractCard __instance, AbstractMonster mo, @ByRef float[] tmp) {
            tmp[0] = BlockModifierManager.atDamageFinalReceive(mo, tmp[0], __instance.damageTypeForTurn, AbstractDungeon.player);
        }

        @SpireInsertPatch(locator = MonsterMultiDamageFinalReceiveLocator.class, localvars = {"tmp","i"})
        public static void multiFinalReceive(AbstractCard __instance, AbstractMonster mo, float[] tmp, int i) {
            tmp[i] = BlockModifierManager.atDamageFinalReceive(AbstractDungeon.getMonsters().monsters.get(i), tmp[i], __instance.damageTypeForTurn, AbstractDungeon.player);
        }


    }

    @SpirePatch(clz = AbstractCard.class, method = "applyPowersToBlock")
    public static class ApplyPowersToBlock {
        @SpireInsertPatch(locator = BlockLocator.class, localvars = {"tmp"})
        public static void blockInsert(AbstractCard __instance, @ByRef float[] tmp) {
            for (BlockInstance b : BlockModifierManager.blockInstances(AbstractDungeon.player)) {
                for (AbstractBlockModifier m : b.getBlockTypes()) {
                    tmp[0] = m.onModifyBlock(tmp[0], __instance);
                }
            }
        }

        @SpireInsertPatch(locator = BlockFinalLocator.class, localvars = {"tmp"})
        public static void blockFinalInsert(AbstractCard __instance, @ByRef float[] tmp) {
            for (BlockInstance b : BlockModifierManager.blockInstances(AbstractDungeon.player)) {
                for (AbstractBlockModifier m : b.getBlockTypes()) {
                    tmp[0] = m.onModifyBlockFinal(tmp[0], __instance);
                }
            }
        }
    }

    @SpirePatch(clz = AbstractMonster.class, method = "calculateDamage")
    public static class PlayerDamageReceivePatches {
        @SpireInsertPatch(locator = PlayerDamageReceiveLocator.class, localvars = {"tmp"})
        public static void receive(AbstractMonster __instance, @ByRef float[] tmp) {
            tmp[0] = BlockModifierManager.atDamageReceive(AbstractDungeon.player, tmp[0], DamageInfo.DamageType.NORMAL, __instance);
        }

        @SpireInsertPatch(locator = PlayerDamageFinalReceiveLocator.class, localvars = {"tmp"})
        public static void finalReceive(AbstractMonster __instance, @ByRef float[] tmp) {
            tmp[0] = BlockModifierManager.atDamageFinalReceive(AbstractDungeon.player, tmp[0], DamageInfo.DamageType.NORMAL, __instance);
        }

        @SpireInsertPatch(locator = MonsterDamageGiveLocator.class, localvars = {"tmp"})
        public static void give(AbstractMonster __instance, @ByRef float[] tmp) {
            tmp[0] = BlockModifierManager.atDamageGive(__instance, tmp[0], DamageInfo.DamageType.NORMAL, AbstractDungeon.player, null);
        }

        @SpireInsertPatch(locator = MonsterDamageFinalGiveLocator.class, localvars = {"tmp"})
        public static void finalGive(AbstractMonster __instance, @ByRef float[] tmp) {
            tmp[0] = BlockModifierManager.atDamageFinalGive(__instance, tmp[0], DamageInfo.DamageType.NORMAL, AbstractDungeon.player, null);
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "makeStatEquivalentCopy")
    public static class MakeStatEquivalentCopy {
        public static AbstractCard Postfix(AbstractCard result, AbstractCard self) {
            for (AbstractBlockModifier mod : BlockModifierManager.modifiers(self)) {
                if (!mod.isInherent()) {
                    BlockModifierManager.addModifier(result, mod);
                }
            }
            return result;
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "onVictory")
    public static class ClearMonsterContainersOnVictory {
        @SpirePrefixPatch
        public static void byeByeContainers(AbstractPlayer __instance) {
            for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                BlockModifierManager.removeAllBlockInstances(m);
            }
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "preBattlePrep")
    public static class ClearPlayerContainersOnPrep {
        @SpirePrefixPatch
        public static void byeByeContainers(AbstractPlayer __instance) {
            BlockModifierManager.removeAllBlockInstances(__instance);
        }
    }

    @SpirePatch2(clz = AbstractMonster.class, method = "damage")
    public static class ClearContainerOnDeath {
        @SpireInsertPatch(locator = MonsterBlockLossOnDeathLocator.class)
        public static void byeByeContainers(AbstractMonster __instance) {
            BlockModifierManager.removeAllBlockInstances(__instance);
        }
    }

    @SpirePatch2(clz = AbstractCreature.class, method = "heal", paramtypez = {int.class, boolean.class})
    public static class OnHeal {
        @SpireInsertPatch(locator = CreatureOnHealLocator.class)
        public static void healthTime(AbstractCreature __instance, @ByRef int[] healAmount) {
            healAmount[0] = BlockModifierManager.onHeal(__instance, healAmount[0]);
        }
    }

    @SpirePatch2(clz = MonsterGroup.class, method = "applyEndOfTurnPowers")
    public static class EndOfRound {
        @SpireInsertPatch(locator = PlayerEndOfRoundLocator.class)
        public static void endRoundPlayer(MonsterGroup __instance) {
            BlockModifierManager.atEndOfRound(AbstractDungeon.player);
        }

        @SpireInsertPatch(locator = MonsterEndRoundLocator.class, localvars = "m")
        public static void endRoundMonster(MonsterGroup __instance, AbstractMonster m) {
            BlockModifierManager.atEndOfRound(m);
        }
    }

    @SpirePatch(clz = AbstractMonster.class, method = "damage")
    public static class OnAttackMonster {
        @SpireInsertPatch(locator = PlayerOnAttackLocator.class, localvars = "damageAmount")
        public static void onAttack(AbstractMonster __instance, DamageInfo info, int damageAmount) {
            if (info.owner != null) {
                BlockModifierManager.onAttack(info.owner, info, damageAmount, __instance);
            }
        }
        /*@SpireInsertPatch(locator = MonsterOnAttackedLocator.class, localvars = "damageAmount")
        public static void onAttacked(AbstractMonster __instance, DamageInfo info, int damageAmount) {
            BlockModifierManager.onAttacked(__instance, info, damageAmount);
        }*/
    }
    @SpirePatch(clz = AbstractPlayer.class, method = "damage")
    public static class OnAttackPlayer {
        @SpireInsertPatch(locator = MonsterOnAttackLocator.class, localvars = "damageAmount")
        public static void onAttack(AbstractPlayer __instance, DamageInfo info, int damageAmount) {
            if (info.owner != null) {
                BlockModifierManager.onAttack(info.owner, info, damageAmount, __instance);
            }
        }
        /*@SpireInsertPatch(locator = PlayerOnAttackedLocator.class, localvars = "damageAmount")
        public static void onAttacked(AbstractPlayer __instance, DamageInfo info, int damageAmount) {
            BlockModifierManager.onAttacked(__instance, info, damageAmount);
        }*/
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "draw", paramtypez = int.class)
    public static class OnDrawCard {
        @SpireInsertPatch(locator = OnDrawCardLocator.class, localvars = "c")
        public static void onDraw(AbstractPlayer __instance, AbstractCard c) {
            BlockModifierManager.onCardDraw(__instance, c);
            for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                if (!m.isDeadOrEscaped()) {
                    BlockModifierManager.onCardDraw(m, c);
                }
            }
        }
    }

    @SpirePatch(clz = UseCardAction.class, method = "<ctor>", paramtypez = {AbstractCard.class, AbstractCreature.class})
    private static class OnUseCard {
        @SpireInsertPatch(locator = PlayerOnUseCardLocator.class)
        public static void onUseCardPlayer(UseCardAction __instance, AbstractCard card, AbstractCreature target) {
            if (!card.dontTriggerOnUseCard) {
                BlockModifierManager.onUseCard(AbstractDungeon.player, card, __instance);
            }
        }
        @SpireInsertPatch(locator = MonsterOnUseCardLocator.class, localvars = "m")
        public static void onUseCardMonster(UseCardAction __instance, AbstractCard card, AbstractCreature target, AbstractMonster m) {
            if (!card.dontTriggerOnUseCard) {
                BlockModifierManager.onUseCard(m, card, __instance);
            }
        }
    }

    @SpirePatch(clz = OnReceivePowerPatch.class, method = "CheckPower")
    private static class ApplyAndReceivePowerStuff {
        @SpireInsertPatch(locator = OnApplyPowerLocator.class, localvars = "apply")
        public static void apply(AbstractGameAction action, AbstractCreature target, AbstractCreature source, float[] duration, AbstractPower powerToApply, @ByRef boolean[] apply) {
            apply[0] = BlockModifierManager.onApplyPower(source, powerToApply, target, source);
        }
        @SpireInsertPatch(locator = OnApplyPowerStacksLocator.class)
        public static void applyStacks(AbstractGameAction action, AbstractCreature target, AbstractCreature source, float[] duration, AbstractPower powerToApply) {
            action.amount = BlockModifierManager.onApplyPowerStacks(source, powerToApply, target, source, action.amount);
        }
        @SpireInsertPatch(locator = OnReceivePowerLocator.class, localvars = "apply")
        public static void receive(AbstractGameAction action, AbstractCreature target, AbstractCreature source, float[] duration, AbstractPower powerToApply, @ByRef boolean[] apply) {
            apply[0] = BlockModifierManager.onReceivePower(target, powerToApply, target, source);
        }
        @SpireInsertPatch(locator = OnReceivePowerStacksLocator.class)
        public static void receiveStacks(AbstractGameAction action, AbstractCreature target, AbstractCreature source, float[] duration, AbstractPower powerToApply) {
            action.amount = BlockModifierManager.onReceivePowerStacks(target, powerToApply, target, source, action.amount);
        }
    }

    @SpirePatch(clz = GameActionManager.class, method = "getNextAction")
    public static class PreBlockLossCall {
        @SpireInsertPatch(locator = Locator.class)
        public static void preBlockLoss(GameActionManager __instance) {
            for (BlockInstance b : BlockModifierManager.blockInstances(AbstractDungeon.player)) {
                for (AbstractBlockModifier mod : b.getBlockTypes()) {
                    mod.atStartOfTurnPreBlockLoss();
                }
            }
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasPower");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    private static class BlockFinalLocator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(MathUtils.class, "floor");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }

    private static class BlockLocator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }

    private static class PlayerDamageGiveLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
            int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{tmp[0]};
        }
    }

    private static class PlayerDamageFinalGiveLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
            int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{tmp[1]};
        }
    }

    private static class PlayerMultiDamageGiveLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
            int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{tmp[2]};
        }
    }

    private static class PlayerMultiDamageFinalGiveLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
            int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{tmp[3]};
        }
    }

    private static class PlayerDamageReceiveLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
            int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{tmp[0]};
        }
    }

    private static class PlayerDamageFinalReceiveLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
            int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{tmp[1]};
        }
    }

    private static class MonsterDamageGiveLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractMonster.class, "powers");
            int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{tmp[0]};
        }
    }

    private static class MonsterDamageFinalGiveLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractMonster.class, "powers");
            int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{tmp[1]};
        }
    }

    private static class MonsterDamageReceiveLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractMonster.class, "powers");
            int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{tmp[0]};
        }
    }

    private static class MonsterDamageFinalReceiveLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractMonster.class, "powers");
            int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{tmp[1]};
        }
    }

    private static class MonsterMultiDamageReceiveLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractMonster.class, "powers");
            int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{tmp[2]};
        }
    }

    private static class MonsterMultiDamageFinalReceiveLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractMonster.class, "powers");
            int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{tmp[3]};
        }
    }

    private static class MonsterBlockLossOnDeathLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractMonster.class, "loseBlock");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }

    private static class CreatureOnHealLocator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCreature.class, "powers");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }

    private static class PlayerEndOfRoundLocator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }

    private static class MonsterEndRoundLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractMonster.class, "powers");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }

    private static class PlayerOnAttackLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCreature.class, "powers");
            int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{tmp[1]};
        }
    }

    private static class MonsterOnAttackedLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractMonster.class, "powers");
            int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{tmp[2]};
        }
    }

    private static class PlayerOnAttackedLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
            int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{tmp[1]};
        }
    }

    private static class MonsterOnAttackLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCreature.class, "powers");
            int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{tmp[1]};
        }
    }

    private static class OnDrawCardLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }

    private static class PlayerOnUseCardLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }

    private static class MonsterOnUseCardLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractMonster.class, "powers");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }

    private static class OnApplyPowerLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(BetterOnApplyPowerPower.class, "betterOnApplyPower");
            int [] ret = LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            ret[0]++;
            return ret;
        }
    }

    private static class OnApplyPowerStacksLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(BetterOnApplyPowerPower.class, "betterOnApplyPowerStacks");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }

    private static class OnReceivePowerLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(OnReceivePowerPower.class, "onReceivePower");
            int [] ret = LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            ret[0]++;
            return ret;
        }
    }

    private static class OnReceivePowerStacksLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(OnReceivePowerPower.class, "onReceivePowerStacks");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}

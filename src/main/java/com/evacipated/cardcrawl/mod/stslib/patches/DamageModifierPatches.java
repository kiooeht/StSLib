package com.evacipated.cardcrawl.mod.stslib.patches;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModContainer;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ThornsPower;
import javassist.CtBehavior;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class DamageModifierPatches {

    @SpirePatch(clz = AbstractCard.class, method = "calculateCardDamage")
    public static class ModifyDamage {
        @SpireInsertPatch(locator = DamageLocator.class, localvars = "tmp")
        public static void single(AbstractCard __instance, AbstractMonster mo, @ByRef float[] tmp) {
            for (AbstractDamageModifier mod : DamageModifierManager.modifiers(__instance)) {
                tmp[0] = mod.atDamageGive(tmp[0], __instance.damageTypeForTurn, mo, __instance);
            }
        }

        @SpireInsertPatch(locator = MultiDamageLocator.class, localvars = {"tmp","i","m"})
        public static void multi(AbstractCard __instance, AbstractMonster mo, float[] tmp, int i, ArrayList<AbstractMonster> m) {
            for (AbstractDamageModifier mod : DamageModifierManager.modifiers(__instance)) {
                tmp[i] = mod.atDamageGive(tmp[i], __instance.damageTypeForTurn, m.get(i), __instance);
            }
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "applyPowers")
    public static class ModifyDamage2 {
        @SpireInsertPatch(locator = DamageLocator.class, localvars = "tmp")
        public static void single(AbstractCard __instance, @ByRef float[] tmp) {
            for (AbstractDamageModifier mod : DamageModifierManager.modifiers(__instance)) {
                tmp[0] = mod.atDamageGive(tmp[0], __instance.damageTypeForTurn, null, __instance);
            }
        }

        @SpireInsertPatch(locator = MultiDamageLocator.class, localvars = {"tmp","i"})
        public static void multi(AbstractCard __instance, float[] tmp, int i) {
            for (AbstractDamageModifier mod : DamageModifierManager.modifiers(__instance)) {
                tmp[i] = mod.atDamageGive(tmp[i], __instance.damageTypeForTurn, null, __instance);
            }
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "calculateCardDamage")
    public static class ModifyDamageFinal {
        @SpireInsertPatch(locator = DamageFinalLocator.class, localvars = "tmp")
        public static void single(AbstractCard __instance, AbstractMonster mo, @ByRef float[] tmp) {
            for (AbstractDamageModifier mod : DamageModifierManager.modifiers(__instance)) {
                tmp[0] = mod.atDamageFinalGive(tmp[0], __instance.damageTypeForTurn, mo, __instance);
            }
        }

        @SpireInsertPatch(locator = MultiDamageFinalLocator.class, localvars = {"tmp","i","m"})
        public static void multi(AbstractCard __instance, AbstractMonster mo, float[] tmp, int i, ArrayList<AbstractMonster> m) {
            for (AbstractDamageModifier mod : DamageModifierManager.modifiers(__instance)) {
                tmp[i] = mod.atDamageFinalGive(tmp[i], __instance.damageTypeForTurn, m.get(i), __instance);
            }
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "applyPowers")
    public static class ModifyDamageFinal2 {
        @SpireInsertPatch(locator = DamageFinalLocator.class, localvars = "tmp")
        public static void single(AbstractCard __instance, @ByRef float[] tmp) {
            for (AbstractDamageModifier mod : DamageModifierManager.modifiers(__instance)) {
                tmp[0] = mod.atDamageFinalGive(tmp[0], __instance.damageTypeForTurn, null, __instance);
            }
        }

        @SpireInsertPatch(locator = MultiDamageFinalLocator.class, localvars = {"tmp","i"})
        public static void multi(AbstractCard __instance, float[] tmp, int i) {
            for (AbstractDamageModifier mod : DamageModifierManager.modifiers(__instance)) {
                tmp[i] = mod.atDamageFinalGive(tmp[i], __instance.damageTypeForTurn, null, __instance);
            }
        }
    }

    @SpirePatch(clz = AbstractMonster.class, method = "damage")
    public static class OnAttackMonster {
        @SpireInsertPatch(locator = OnAttackToChangeDamageLocator.class, localvars = "damageAmount")
        public static void toChangeDamage(AbstractMonster __instance, DamageInfo info, @ByRef int[] damageAmount) {
            for (AbstractDamageModifier mod : DamageModifierManager.getDamageMods(info)) {
                damageAmount[0] = mod.onAttackToChangeDamage(info, damageAmount[0], __instance);
            }
        }

        @SpireInsertPatch(locator = OnAttackLocator.class, localvars = "damageAmount")
        public static void onAttack(AbstractMonster __instance, DamageInfo info, int damageAmount) {
            for (AbstractDamageModifier mod : DamageModifierManager.getDamageMods(info)) {
                mod.onAttack(info, damageAmount, __instance);
            }
        }

        @SpireInsertPatch(locator = LastDamageTakenLocator.class, localvars = "damageAmount")
        public static void onLastDamageTakenUpdate(AbstractMonster __instance, DamageInfo info, int damageAmount) {
            int overkill = damageAmount > __instance.lastDamageTaken ? damageAmount - __instance.lastDamageTaken : 0;
            for (AbstractDamageModifier mod : DamageModifierManager.getDamageMods(info)) {
                mod.onLastDamageTakenUpdate(info, __instance.lastDamageTaken, overkill, __instance);
            }
        }

        @SpirePostfixPatch()
        public static void removeModsAfterUse(AbstractMonster __instance, DamageInfo info) {
            Object obj = DamageModifierManager.getInstigator(info);
            if (obj instanceof AbstractCard) {
                DamageModifierManager.modifiers((AbstractCard) obj).removeIf(AbstractDamageModifier::removeWhenActivated);
            }
            if (obj instanceof DamageModContainer) {
                ((DamageModContainer) obj).modifiers().removeIf(AbstractDamageModifier::removeWhenActivated);
            }
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "damage")
    public static class OnAttackPlayer {
        @SpireInsertPatch(locator = OnAttackToChangeDamageLocator.class, localvars = "damageAmount")
        public static void toChangeDamage(AbstractPlayer __instance, DamageInfo info, @ByRef int[] damageAmount) {
            for (AbstractDamageModifier mod : DamageModifierManager.getDamageMods(info)) {
                damageAmount[0] = mod.onAttackToChangeDamage(info, damageAmount[0], __instance);
            }
        }
        @SpireInsertPatch(locator = OnAttackLocator.class, localvars = "damageAmount")
        public static void onAttack(AbstractPlayer __instance, DamageInfo info, int damageAmount) {
            for (AbstractDamageModifier mod : DamageModifierManager.getDamageMods(info)) {
                mod.onAttack(info, damageAmount, __instance);
            }
        }
        @SpireInsertPatch(locator = OwnerIsNullFailsafeLocator.class, localvars = "damageAmount")
        public static void onAttackFailsafe(AbstractPlayer __instance, DamageInfo info, int damageAmount) {
            for (AbstractDamageModifier mod : DamageModifierManager.getDamageMods(info)) {
                mod.onAttack(info, damageAmount, __instance);
            }
        }
        @SpireInsertPatch(locator = LastDamageTakenLocator.class, localvars = "damageAmount")
        public static void onLastDamageTakenUpdate(AbstractPlayer __instance, DamageInfo info, int damageAmount) {
            int overkill = damageAmount > __instance.lastDamageTaken ? damageAmount - __instance.lastDamageTaken : 0;
            for (AbstractDamageModifier mod : DamageModifierManager.getDamageMods(info)) {
                mod.onLastDamageTakenUpdate(info, __instance.lastDamageTaken, overkill, __instance);
            }
        }
        @SpirePostfixPatch()
        public static void removeModsAfterUse(AbstractPlayer __instance, DamageInfo info) {
            Object obj = DamageModifierManager.getInstigator(info);
            if (obj instanceof AbstractCard) {
                DamageModifierManager.modifiers((AbstractCard) obj).removeIf(AbstractDamageModifier::removeWhenActivated);
            }
            if (obj instanceof DamageModContainer) {
                ((DamageModContainer) obj).modifiers().removeIf(AbstractDamageModifier::removeWhenActivated);
            }
        }
    }

    @SpirePatch(clz = AbstractCreature.class, method = "decrementBlock")
    public static class BlockStuff {
        @SpirePrefixPatch
        public static SpireReturn<?> block(AbstractCreature __instance, DamageInfo info, int damageAmount) {
            boolean bypass = false;
            for (AbstractDamageModifier mod : DamageModifierManager.getDamageMods(info)) {
                mod.onDamageModifiedByBlock(info, Math.max(0, damageAmount-__instance.currentBlock), Math.min(damageAmount, __instance.currentBlock), __instance);
                if (mod.ignoresBlock(__instance)) {
                    bypass = true;
                }
            }
            if (bypass) {
                return SpireReturn.Return(damageAmount);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = ThornsPower.class, method = "onAttacked")
    public static class ThornsBypass {
        @SpirePrefixPatch
        public static SpireReturn<?> noDamagePls(ThornsPower __instance, DamageInfo info, int damageAmount) {
            for (AbstractDamageModifier mod : DamageModifierManager.getDamageMods(info)) {
                if (mod.ignoresThorns()) {
                    return SpireReturn.Return(damageAmount);
                }
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "makeStatEquivalentCopy")
    public static class MakeStatEquivalentCopy {
        public static AbstractCard Postfix(AbstractCard result, AbstractCard self) {
            for (AbstractDamageModifier mod : DamageModifierManager.modifiers(self)) {
                if (!mod.isInherent()) {
                    DamageModifierManager.addModifier(result, mod);
                }
            }
            //DamageModifierManager.addModifiers(result, DamageModifierManager.modifiers(self).stream().filter(m -> !m.inInnate()).collect(Collectors.toCollection(ArrayList::new)));
            return result;
        }
    }

    private static class MultiDamageFinalLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
            int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{tmp[3]};
        }
    }

    private static class DamageFinalLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
            int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{tmp[1]};
        }
    }

    private static class MultiDamageLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
            int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{tmp[2]};
        }
    }

    private static class DamageLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
            int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{tmp[0]};
        }
    }

    private static class OnAttackToChangeDamageLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPower.class, "onAttackToChangeDamage");
            int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{tmp[0]+2};
        }
    }

    private static class OnAttackLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPower.class, "onAttack");
            int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{tmp[0]+2};
        }
    }

    private static class LastDamageTakenLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(Math.class, "min");
            int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{tmp[0]+1};
        }
    }

    private static class OwnerIsNullFailsafeLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(Logger.class, "info");
            int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{tmp[0]};
        }
    }
}

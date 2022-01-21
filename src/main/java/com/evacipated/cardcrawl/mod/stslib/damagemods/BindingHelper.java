package com.evacipated.cardcrawl.mod.stslib.damagemods;

import com.evacipated.cardcrawl.mod.stslib.patches.BindingPatches;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

import java.util.List;

public class BindingHelper {

    public static DamageInfo makeInfo(List<AbstractDamageModifier> mods, AbstractCreature damageSource, int base, DamageInfo.DamageType type) {
        //Set the directly bound mods before creating the damage info
        BindingPatches.directlyBoundDamageMods.addAll(mods);
        DamageInfo di = new DamageInfo(damageSource, base, type);
        //Unset them now that they have been loaded into the damage info
        BindingPatches.directlyBoundDamageMods.clear();
        return di;
    }

    public static DamageInfo makeInfo(AbstractCard o, AbstractCreature damageSource, int base, DamageInfo.DamageType type) {
        //Set the directly bound instigator before creating the damage info
        BindingPatches.directlyBoundInstigator = o;
        DamageInfo di = makeInfo(DamageModifierManager.modifiers(o), damageSource, base, type);
        //Unset it once we are done, as it has already been loaded into the damage info
        BindingPatches.directlyBoundInstigator = null;
        return di;
    }

    public static DamageInfo makeInfo(DamageModContainer o, AbstractCreature damageSource, int base, DamageInfo.DamageType type) {
        //Set the directly bound instigator before creating the damage info
        BindingPatches.directlyBoundInstigator = o.instigator();
        DamageInfo di = makeInfo(o.modifiers(), damageSource, base, type);
        //Unset it once we are done, as it has already been loaded into the damage info
        BindingPatches.directlyBoundInstigator = null;
        return di;
    }

    public static AbstractGameAction makeAction(List<AbstractDamageModifier> mods, AbstractGameAction action) {
        BindingPatches.BoundGameActionFields.actionDelayedDamageMods.get(action).addAll(mods);
        return action;
    }

    public static AbstractGameAction makeAction(AbstractCard o, AbstractGameAction action) {
        BindingPatches.BoundGameActionFields.actionDelayedDirectlyBoundInstigator.set(action, o);
        return makeAction(DamageModifierManager.modifiers(o), action);
    }

    public static AbstractGameAction makeAction(DamageModContainer o, AbstractGameAction action) {
        BindingPatches.BoundGameActionFields.actionDelayedDirectlyBoundInstigator.set(action, o.instigator());
        return makeAction(o.modifiers(), action);
    }

    public static void bindAction(List<AbstractDamageModifier> mods, AbstractGameAction action) {
        BindingPatches.BoundGameActionFields.actionDelayedDamageMods.get(action).addAll(mods);
    }

    public static void bindAction(AbstractCard o, AbstractGameAction action) {
        BindingPatches.BoundGameActionFields.actionDelayedDirectlyBoundInstigator.set(action, o);
        bindAction(DamageModifierManager.modifiers(o), action);
    }

    public static void bindAction(DamageModContainer o, AbstractGameAction action) {
        BindingPatches.BoundGameActionFields.actionDelayedDirectlyBoundInstigator.set(action, o.instigator());
        bindAction(o.modifiers(), action);
    }

/*    public static DamageAllEnemiesAction makeModifiedDamageAllEnemiesAction(AbstractCard o, AbstractCreature source, int[] amount, DamageInfo.DamageType type, AbstractGameAction.AttackEffect effect, boolean isFast) {
        DamageAllEnemiesAction action = new DamageAllEnemiesAction(source, amount, type, effect, isFast);
        bindAction(o, action);
        return action;
    }

    public static DamageAllEnemiesAction makeModifiedDamageAllEnemiesAction(AbstractCard o, AbstractCreature source, int[] amount, DamageInfo.DamageType type, AbstractGameAction.AttackEffect effect) {
        DamageAllEnemiesAction action = new DamageAllEnemiesAction(source, amount, type, effect);
        bindAction(o, action);
        return action;
    }

    public static DamageAllEnemiesAction makeModifiedDamageAllEnemiesAction(AbstractCard o, AbstractPlayer player, int baseDamage, DamageInfo.DamageType type, AbstractGameAction.AttackEffect effect) {
        DamageAllEnemiesAction action = new DamageAllEnemiesAction(player, baseDamage, type, effect);
        bindAction(o, action);
        return action;
    }

    public static DamageAllButOneEnemyAction makeModifiedDamageAllButOneEnemyAction(AbstractCard o, AbstractCreature source, AbstractCreature target, int[] amount, DamageInfo.DamageType type, AbstractGameAction.AttackEffect effect, boolean isFast) {
        DamageAllButOneEnemyAction action = new DamageAllButOneEnemyAction(source, target, amount, type, effect, isFast);
        bindAction(o, action);
        return action;
    }

    public static DamageAllButOneEnemyAction makeModifiedDamageAllButOneEnemyAction(AbstractCard o, AbstractCreature source, AbstractCreature target, int[] amount, DamageInfo.DamageType type, AbstractGameAction.AttackEffect effect) {
        DamageAllButOneEnemyAction action = new DamageAllButOneEnemyAction(source, target, amount, type, effect);
        bindAction(o, action);
        return action;
    }

    public static VampireDamageAllEnemiesAction makeModifiedVampireDamageAllEnemiesAction(AbstractCard o, AbstractCreature source, int[] amount, DamageInfo.DamageType type, AbstractGameAction.AttackEffect effect) {
        VampireDamageAllEnemiesAction action = new VampireDamageAllEnemiesAction(source, amount, type, effect);
        bindAction(o, action);
        return action;
    }

    public static DamageAllEnemiesAction makeModifiedDamageAllEnemiesAction(DamageModContainer o, AbstractCreature source, int[] amount, DamageInfo.DamageType type, AbstractGameAction.AttackEffect effect, boolean isFast) {
        DamageAllEnemiesAction action = new DamageAllEnemiesAction(source, amount, type, effect, isFast);
        bindAction(o, action);
        return action;
    }

    public static DamageAllEnemiesAction makeModifiedDamageAllEnemiesAction(DamageModContainer o, AbstractCreature source, int[] amount, DamageInfo.DamageType type, AbstractGameAction.AttackEffect effect) {
        DamageAllEnemiesAction action = new DamageAllEnemiesAction(source, amount, type, effect);
        bindAction(o, action);
        return action;
    }

    public static DamageAllEnemiesAction makeModifiedDamageAllEnemiesAction(DamageModContainer o, AbstractPlayer player, int baseDamage, DamageInfo.DamageType type, AbstractGameAction.AttackEffect effect) {
        DamageAllEnemiesAction action = new DamageAllEnemiesAction(player, baseDamage, type, effect);
        bindAction(o, action);
        return action;
    }

    public static DamageAllButOneEnemyAction makeModifiedDamageAllButOneEnemyAction(DamageModContainer o, AbstractCreature source, AbstractCreature target, int[] amount, DamageInfo.DamageType type, AbstractGameAction.AttackEffect effect, boolean isFast) {
        DamageAllButOneEnemyAction action = new DamageAllButOneEnemyAction(source, target, amount, type, effect, isFast);
        bindAction(o, action);
        return action;
    }

    public static DamageAllButOneEnemyAction makeModifiedDamageAllButOneEnemyAction(DamageModContainer o, AbstractCreature source, AbstractCreature target, int[] amount, DamageInfo.DamageType type, AbstractGameAction.AttackEffect effect) {
        DamageAllButOneEnemyAction action = new DamageAllButOneEnemyAction(source, target, amount, type, effect);
        bindAction(o, action);
        return action;
    }

    public static VampireDamageAllEnemiesAction makeModifiedVampireDamageAllEnemiesAction(DamageModContainer o, AbstractCreature source, int[] amount, DamageInfo.DamageType type, AbstractGameAction.AttackEffect effect) {
        VampireDamageAllEnemiesAction action = new VampireDamageAllEnemiesAction(source, amount, type, effect);
        bindAction(o, action);
        return action;
    }*/
}

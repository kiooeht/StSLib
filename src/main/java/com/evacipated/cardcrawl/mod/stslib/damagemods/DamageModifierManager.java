package com.evacipated.cardcrawl.mod.stslib.damagemods;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DamageModifierManager {

    @SpirePatch(clz = DamageInfo.class, method = SpirePatch.CLASS)
    public static class BoundDamageInfoFields {
        public static final SpireField<List<AbstractDamageModifier>> boundDamageMods = new SpireField<>(ArrayList::new);
        public static final SpireField<Object> instigatingObject = new SpireField<>(() -> null);
    }

    public static class DamageModifierFields {
        @SpirePatch(clz = AbstractCard.class, method = SpirePatch.CLASS)
        public static class CardField {
            public static final SpireField<List<AbstractDamageModifier>> damageModifiers = new SpireField<>(ArrayList::new);
        }

//        @SpirePatch(clz = AbstractCardModifier.class, method = SpirePatch.CLASS)
//        public static class CardModField {
//            public static final SpireField<List<AbstractDamageModifier>> damageModifiers = new SpireField<>(ArrayList::new);
//        }
//
//        @SpirePatch(clz = AbstractCreature.class, method = SpirePatch.CLASS)
//        public static class CreatureField {
//            public static final SpireField<List<AbstractDamageModifier>> damageModifiers = new SpireField<>(ArrayList::new);
//        }
//
//        @SpirePatch(clz = AbstractOrb.class, method = SpirePatch.CLASS)
//        public static class OrbField {
//            public static final SpireField<List<AbstractDamageModifier>> damageModifiers = new SpireField<>(ArrayList::new);
//        }
//
//        @SpirePatch(clz = AbstractPotion.class, method = SpirePatch.CLASS)
//        public static class PotionField {
//            public static final SpireField<List<AbstractDamageModifier>> damageModifiers = new SpireField<>(ArrayList::new);
//        }
//
//        @SpirePatch(clz = AbstractPower.class, method = SpirePatch.CLASS)
//        public static class PowerField {
//            public static final SpireField<List<AbstractDamageModifier>> damageModifiers = new SpireField<>(ArrayList::new);
//        }
//
//        @SpirePatch(clz = AbstractRelic.class, method = SpirePatch.CLASS)
//        public static class RelicField {
//            public static final SpireField<List<AbstractDamageModifier>> damageModifiers = new SpireField<>(ArrayList::new);
//        }
    }


    public static List<AbstractDamageModifier> getDamageMods(DamageInfo info) {
        return BoundDamageInfoFields.boundDamageMods.get(info);
    }

    public static Object getInstigator(DamageInfo info) {
        return BoundDamageInfoFields.instigatingObject.get((info));
    }

    public static void bindInstigator(DamageInfo info, Object o) {
        BoundDamageInfoFields.instigatingObject.set(info, o);
    }

    public static void bindDamageMods(DamageInfo info, List<AbstractDamageModifier> list) {
        for (AbstractDamageModifier m : list) {
            if (!BoundDamageInfoFields.boundDamageMods.get(info).contains(m)) {
                BoundDamageInfoFields.boundDamageMods.get(info).add(m);
            }
        }
    }

    public static void bindDamageMods(DamageInfo info, AbstractCard card) {
        bindDamageMods(info, modifiers(card));
    }

    public static void bindDamageMods(DamageInfo info, DamageModContainer container) {
        bindDamageMods(info, container.modifiers());
    }

    public static void addModifier(AbstractCard card, AbstractDamageModifier damageMod) {
        modifiers(card).add(damageMod);
        Collections.sort(modifiers(card));
    }

    public static void addModifiers(AbstractCard card, List<AbstractDamageModifier> damageMods) {
        modifiers(card).addAll(damageMods);
        Collections.sort(modifiers(card));
    }

    public static void removeModifier(AbstractCard card, AbstractDamageModifier damageMod) {
        modifiers(card).remove(damageMod);
    }

    public static void removeModifiers(AbstractCard card, ArrayList<AbstractDamageModifier> damageMods) {
        modifiers(card).removeAll(damageMods);
    }

    public static void clearModifiers(AbstractCard card) {
        modifiers(card).clear();
    }

    public static List<AbstractDamageModifier> modifiers(AbstractCard card) {
        return DamageModifierFields.CardField.damageModifiers.get(card);
    }

//    public static List<AbstractDamageModifier> modifiers(AbstractCardModifier o) {
//        return DamageModifierFields.CardModField.damageModifiers.get(o);
//    }
//
//    public static List<AbstractDamageModifier> modifiers(AbstractCreature o) {
//        return DamageModifierFields.CreatureField.damageModifiers.get(o);
//    }
//
//    public static List<AbstractDamageModifier> modifiers(AbstractOrb o) {
//        return DamageModifierFields.OrbField.damageModifiers.get(o);
//    }
//
//    public static List<AbstractDamageModifier> modifiers(AbstractPotion o) {
//        return DamageModifierFields.PotionField.damageModifiers.get(o);
//    }
//
//    public static List<AbstractDamageModifier> modifiers(AbstractPower o) {
//        return DamageModifierFields.PowerField.damageModifiers.get(o);
//    }
//
//    public static List<AbstractDamageModifier> modifiers(AbstractRelic o) {
//        return DamageModifierFields.RelicField.damageModifiers.get(o);
//    }

}

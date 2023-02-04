package com.evacipated.cardcrawl.mod.stslib.patches.powerInterfaces;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.PowerBuffEffect;
import com.megacrit.cardcrawl.vfx.combat.PowerDebuffEffect;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.ArrayList;

public class InvisiblePowerPatch
{
    @SpirePatch(
            clz=AbstractCreature.class,
            method="renderPowerIcons"
    )
    public static class RenderPowerIcons
    {
        public static ExprEditor Instrument()
        {
            return new ExprEditor()
            {
                @Override
                public void edit(MethodCall m) throws CannotCompileException
                {
                    if (m.getMethodName().equals("renderIcons") || m.getMethodName().equals("renderAmount")) {
                        m.replace("if (p instanceof " + InvisiblePower.class.getName() + ") {" +
                                "offset -= POWER_ICON_PADDING_X;" +
                                "} else {" +
                                "$proceed($$);" +
                                "}");
                    }
                }
            };
        }
    }

    @SpirePatch(
            clz=AbstractCreature.class,
            method="renderPowerTips"
    )
    @SpirePatch(
            clz=AbstractPlayer.class,
            method="renderPowerTips"
    )
    @SpirePatch(
            clz=AbstractMonster.class,
            method="renderTip"
    )
    public static class RenderPowerTips
    {
        public static ExprEditor Instrument()
        {
            return new ExprEditor()
            {
                private int count = 0;

                @Override
                public void edit(MethodCall m) throws CannotCompileException
                {
                    if (m.getFileName().equals("AbstractMonster.java") || m.getFileName().equals("AbstractPlayer.java")) {
                        if (m.getClassName().equals(ArrayList.class.getName()) && m.getMethodName().equals("add")) {
                            // Skip first instance of tips.add()
                            if (count > 0) {
                                m.replace("if (!(p instanceof " + InvisiblePower.class.getName() + ")) {" +
                                        "$_ = $proceed($$);" +
                                        "}");
                            }
                            ++count;
                        }
                    } else {
                        if (m.getClassName().equals(ArrayList.class.getName()) && m.getMethodName().equals("add")) {
                            m.replace("if (!(p instanceof " + InvisiblePower.class.getName() + ")) {" +
                                    "$_ = $proceed($$);" +
                                    "}");
                        }
                    }
                }
            };
        }
    }

    //Prevent power application effect from appearing for Invisible powers
    @SpirePatch(clz = ApplyPowerAction.class, method = "update")
    public static class RemoveApplicationEffectsForInvisiblePowers {
        @SpireInsertPatch(locator = Locator.class)
        public static void antiApplicationEffect(ApplyPowerAction __instance, AbstractPower ___powerToApply) {
            if (___powerToApply instanceof InvisiblePower) {
                for (int i = AbstractDungeon.effectList.size() - 1; i > -1; i--) {
                    if (___powerToApply.type == AbstractPower.PowerType.DEBUFF) {
                        if (AbstractDungeon.effectList.get(i) instanceof PowerDebuffEffect) {
                            AbstractDungeon.effectList.remove(i);
                        }
                    } else if (___powerToApply.type == AbstractPower.PowerType.BUFF) {
                        if (AbstractDungeon.effectList.get(i) instanceof PowerBuffEffect) {
                            AbstractDungeon.effectList.remove(i);
                        }
                    }
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "onModifyPower");
                return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            }
        }

        @SpireInstrumentPatch
        public static ExprEditor DontFlashInvisiblePowers() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(AbstractPower.class.getName()) && m.getMethodName().equals("flash")) {
                        m.replace("if (!(powerToApply instanceof " + InvisiblePower.class.getName() + ")) {" +
                                "$_ = $proceed($$);" +
                                "}");
                    }
                }
            };
        }
    }
    @SpirePatch(
            clz=RemoveSpecificPowerAction.class,
            method="update"
    )
    public static class HideExpireText
    {
        public static ExprEditor Instrument()
        {
            return new ExprEditor()
            {
                @Override
                public void edit(MethodCall m) throws CannotCompileException
                {
                    if (m.getClassName().equals(ArrayList.class.getName()) && m.getMethodName().equals("add")) {
                        m.replace("if (!(removeMe instanceof " + InvisiblePower.class.getName()+ ")) {" +
                                "$_ = $proceed($$);" +
                                "}");
                    }
                }
            };
        }
    }
}

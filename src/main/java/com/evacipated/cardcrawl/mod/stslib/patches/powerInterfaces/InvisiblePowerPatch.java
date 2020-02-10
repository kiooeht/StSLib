package com.evacipated.cardcrawl.mod.stslib.patches.powerInterfaces;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CannotCompileException;
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
}

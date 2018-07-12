package com.evacipated.cardcrawl.mod.stslib.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

@SpirePatch(
        cls="com.megacrit.cardcrawl.actions.GameActionManager",
        method="getNextAction"
)
public class StunMonsterPatch
{
    public static ExprEditor Instrument()
    {
        return new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException
            {
                if (m.getClassName().equals("com.megacrit.cardcrawl.monsters.AbstractMonster")
                        && m.getMethodName().equals("takeTurn")) {
                    m.replace("if (!m.hasPower(com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower.POWER_ID)) {" +
                            "$_ = $proceed($$);" +
                            "}");
                }
            }
        };
    }
}

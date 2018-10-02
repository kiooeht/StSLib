package com.evacipated.cardcrawl.mod.stslib.patches.relicInterfaces;

import com.evacipated.cardcrawl.mod.stslib.relics.BetterOnLoseHpRelic;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

@SpirePatch(
        clz=AbstractPlayer.class,
        method="damage"
)
public class BetterOnLoseHpPatch
{
    public static ExprEditor Instrument()
    {
        return new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException
            {
                if (m.getClassName().equals(AbstractRelic.class.getName()) && m.getMethodName().equals("onLoseHp")) {
                    m.replace("{" +
                            "damageAmount = " + BetterOnLoseHpPatch.class.getName() + ".Do(info, r, damageAmount);" +
                            "$proceed(damageAmount);" +
                            "}");
                }
            }
        };
    }

    @SuppressWarnings("unused")
    public static int Do(DamageInfo info, AbstractRelic r, int damageAmount)
    {
        if (r instanceof BetterOnLoseHpRelic) {
            return ((BetterOnLoseHpRelic) r).betterOnLoseHp(info, damageAmount);
        }
        return damageAmount;
    }
}

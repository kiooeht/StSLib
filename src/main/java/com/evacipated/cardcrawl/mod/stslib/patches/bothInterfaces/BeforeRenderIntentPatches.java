package com.evacipated.cardcrawl.mod.stslib.patches.bothInterfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.BeforeRenderIntentPower;
import com.evacipated.cardcrawl.mod.stslib.relics.BeforeRenderIntentRelic;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.NeowsLament;
import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

public class BeforeRenderIntentPatches {
    @SpirePatch(
            clz = AbstractMonster.class,
            method = "render",
            paramtypez = {
                    SpriteBatch.class
            }
    )
    public static class BeforeRenderIntentPatch {
        @SpireInstrumentPatch
        public static ExprEditor patch() {
            return new ExprEditor() {
                boolean first = true;
                @Override
                public void edit(FieldAccess f) throws CannotCompileException {
                    if (f.getFieldName().equals("hideCombatElements") && first) {
                        f.replace(String.format("$_ = $proceed($$) || %s.check(this);", BeforeRenderIntentPatches.class.getName()));
                        first = false;
                    }
                }
            };
        }
    }

    @SpirePatch(clz = AbstractMonster.class, method = "renderTip")
    public static class AbstractMonsterDomePatch {
        @SpireInstrumentPatch
        public static ExprEditor patch() {
            return new ExprEditor() {
                boolean first = true;

                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("hasRelic") && first) {
                        first = false;
                        m.replace(String.format("$_ = $proceed($$) || %s.check(this);", BeforeRenderIntentPatches.class.getName()));
                    }
                }
            };
        }
    }

    public static boolean check(AbstractMonster m) {
        boolean hide = false;
        for(AbstractPower p : m.powers) {
            if(p instanceof BeforeRenderIntentPower) {
                if(!((BeforeRenderIntentPower) p).beforeRenderIntent(m)) {
                    hide = true;
                }
            }
        }

        for(AbstractPower p : AbstractDungeon.player.powers) {
            if(p instanceof BeforeRenderIntentPower) {
                if(!((BeforeRenderIntentPower) p).beforeRenderIntent(m)) {
                    hide = true;
                }
            }
        }

        for(AbstractRelic r : AbstractDungeon.player.relics) {
            if(r instanceof BeforeRenderIntentRelic) {
                if(!((BeforeRenderIntentRelic) r).beforeRenderIntent(m)) {
                    hide = true;
                }
            }
        }

        return hide;
    }
}

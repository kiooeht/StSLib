package com.evacipated.cardcrawl.mod.stslib.patches.relicInterfaces;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.evacipated.cardcrawl.mod.stslib.relics.OnReceivePowerRelic;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CtBehavior;

@SpirePatch(
        clz=ApplyPowerAction.class,
        method="update"
)
public class OnReceivePowerPatch
{
    @SpireInsertPatch(
            locator=Locator.class,
            localvars={"powerToApply"}
    )
    public static SpireReturn<Void> Insert(ApplyPowerAction __instance, AbstractPower powerToApply)
    {
        if (__instance.target != null) {
            for (AbstractPower power : __instance.target.powers) {
                if (power instanceof OnReceivePowerPower) {
                    boolean apply = ((OnReceivePowerPower)power).onReceivePower(powerToApply, __instance.target, __instance.source);
                    if (!apply) {
                        AbstractDungeon.actionManager.addToTop(new TextAboveCreatureAction(__instance.target, ApplyPowerAction.TEXT[0]));
                        __instance.isDone = true;
                        CardCrawlGame.sound.play("NULLIFY_SFX");
                        return SpireReturn.Return(null);
                    }
                }
            }
            if (__instance.target.isPlayer) {
                for (AbstractRelic relic : AbstractDungeon.player.relics) {
                    if (relic instanceof OnReceivePowerRelic) {
                        boolean apply = ((OnReceivePowerRelic) relic).onReceivePower(powerToApply, __instance.source);
                        if (!apply) {
                            AbstractDungeon.actionManager.addToTop(new TextAboveCreatureAction(__instance.target, ApplyPowerAction.TEXT[0]));
                            __instance.isDone = true;
                            CardCrawlGame.sound.play("NULLIFY_SFX");
                            return SpireReturn.Return(null);
                        }
                    }
                }
            }
        }
        return SpireReturn.Continue();
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasRelic");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}

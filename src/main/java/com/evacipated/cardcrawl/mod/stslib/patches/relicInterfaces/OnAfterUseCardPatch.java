package com.evacipated.cardcrawl.mod.stslib.patches.relicInterfaces;

import com.evacipated.cardcrawl.mod.stslib.relics.OnAfterUseCardRelic;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(
        clz=UseCardAction.class,
        method="update"
)
public class OnAfterUseCardPatch
{
    @SpireInsertPatch(
            locator=Locator.class,
            localvars={"targetCard"}
    )
    public static void Insert(UseCardAction __instance, AbstractCard targetCard)
    {
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic instanceof OnAfterUseCardRelic) {
                ((OnAfterUseCardRelic) relic).onAfterUseCard(targetCard, __instance);
            }
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "getMonsters");
            return LineFinder.findInOrder(ctBehavior, new ArrayList<>(), finalMatcher);
        }
    }
}

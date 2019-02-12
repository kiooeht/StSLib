package com.evacipated.cardcrawl.mod.stslib.patches.powerInterfaces;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.BetterOnExhaustPower;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CtBehavior;

@SpirePatch(
        clz=CardGroup.class,
        method="moveToExhaustPile"
)
public class BetterOnExhaustPatch
{
    @SpireInsertPatch(
            locator=Locator.class
    )
    public static void Insert(CardGroup __instance, AbstractCard c)
    {
        for (AbstractPower p : AbstractDungeon.player.powers) {
            if (p instanceof BetterOnExhaustPower) {
                ((BetterOnExhaustPower) p).betterOnExhaust(__instance, c);
            }
        }

        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            for (AbstractPower p : monster.powers) {
                if (p instanceof BetterOnExhaustPower) {
                    ((BetterOnExhaustPower) p).betterOnExhaust(__instance, c);
                }
            }
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "triggerOnExhaust");
            return LineFinder.findInOrder(ctBehavior, finalMatcher);
        }
    }
}

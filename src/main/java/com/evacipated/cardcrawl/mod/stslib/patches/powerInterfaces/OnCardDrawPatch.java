package com.evacipated.cardcrawl.mod.stslib.patches.powerInterfaces;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnCardDrawPower;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(
        clz= AbstractPlayer.class,
        method="draw",
        paramtypez={int.class}
)
public class OnCardDrawPatch
{
    @SpireInsertPatch(
            locator=Locator.class,
            localvars={"c"}
    )
    public static void Insert(AbstractPlayer __instance, int numCards, AbstractCard drawnCard)
    {
        for (AbstractPower p : __instance.powers) {
            if (p instanceof OnCardDrawPower) {
                ((OnCardDrawPower) p).onCardDraw(drawnCard);
            }
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception
        {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "relics");
            return LineFinder.findAllInOrder(ctBehavior, new ArrayList<>(), finalMatcher);
        }
    }
}

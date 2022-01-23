package com.evacipated.cardcrawl.mod.stslib.patches.cardInterfaces;

import com.evacipated.cardcrawl.mod.stslib.cards.interfaces.SpawnModificationCard;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch2(clz = AbstractDungeon.class, method = "getRewardCards")
public class CardSpawnModificationPatch {
    //Patches into 1838 |  if (c.cardID.equals(card.cardID))
    @SpireInsertPatch(locator = Locator.class, localvars = {"c", "containsDupe", "retVal"})
    public static void patch(@ByRef AbstractCard[] c, @ByRef boolean[] containsDupe, ArrayList<AbstractCard> retVal) {
        if(c[0] instanceof SpawnModificationCard) {
            if(!((SpawnModificationCard) c[0]).canSpawn(retVal)) {
                containsDupe[0] = true;
                return;
            }

            c[0] = ((SpawnModificationCard) c[0]).replaceWith(c[0], retVal);
        }
    }

    @SpirePostfixPatch
    public static void patch(ArrayList<AbstractCard> __result) {
        for(AbstractCard c : __result) {
            if(c instanceof SpawnModificationCard) {
                ((SpawnModificationCard) c).onRewardListCreated(__result);
            }
        }
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(String.class, "equals");
            return LineFinder.findInOrder(ctBehavior, new ArrayList<>(), finalMatcher);
        }
    }
}

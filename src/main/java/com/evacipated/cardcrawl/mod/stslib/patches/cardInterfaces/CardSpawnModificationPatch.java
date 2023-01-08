package com.evacipated.cardcrawl.mod.stslib.patches.cardInterfaces;

import com.evacipated.cardcrawl.mod.stslib.cards.interfaces.SpawnModificationCard;
import com.evacipated.cardcrawl.mod.stslib.patches.relicInterfaces.BetterOnLoseHpPatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.shop.Merchant;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

import java.util.ArrayList;

public class CardSpawnModificationPatch {
    @SpirePatch2(clz = AbstractDungeon.class, method = "getRewardCards")
    public static class CardRewardModificationPatches {
        //Patches into 1837 |  for (AbstractCard c : retVal) {
        @SpireInsertPatch(rloc = 45, localvars = {"card", "containsDupe", "retVal"})
        public static void patch(@ByRef AbstractCard[] card, @ByRef boolean[] containsDupe, ArrayList<AbstractCard> retVal) {
            if (card[0] instanceof SpawnModificationCard) {
                if (!((SpawnModificationCard) card[0]).canSpawn(retVal)) {
                    containsDupe[0] = true;
                    return;
                }

                card[0] = ((SpawnModificationCard) card[0]).replaceWith(retVal);
            }
        }

        @SpirePostfixPatch
        public static void patch(ArrayList<AbstractCard> __result) {
            for (AbstractCard c : __result) {
                if (c instanceof SpawnModificationCard) {
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

    @SpirePatch2(clz = Merchant.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {float.class, float.class, int.class})
    public static class ShopModificationPatches {
        //Colored cards
        @SpireInstrumentPatch
        public static ExprEditor checkForCanSpawn() {
            return new ExprEditor() {
                @Override
                public void edit(FieldAccess f) throws CannotCompileException
                { // "$_ = $proceed($$) || " + ShopModificationPatches.class.getName() +".canSpawnInShop(c, cards1);"+
                    if (f.getClassName().equals(AbstractCard.class.getName()) && f.getFieldName().equals("color")) {
                        f.replace("{" +
                                "$_ = " + ShopModificationPatches.class.getName() +".canSpawnInShop(c, cards1);"+
                                "}");
                    }
                }
            };
        }

        public static AbstractCard.CardColor canSpawnInShop(AbstractCard c, ArrayList<AbstractCard> cards1) {
            if(c instanceof SpawnModificationCard && !((SpawnModificationCard) c).canSpawnShop(cards1)) {
                //Makes the while loop true, thus generates a new card
                return AbstractCard.CardColor.COLORLESS;
            }
            return c.color;
        }

        //Colorless cards
        static int counter = 0;
        @SpireInstrumentPatch
        public static ExprEditor checkForColorlessCanSpawn() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall f) throws CannotCompileException
                {
                    if (f.getClassName().equals(ArrayList.class.getName()) && f.getMethodName().equals("add")) {
                        if(counter++ > 4) {
                            f.replace("do {" +
                                    "$_ = $proceed($$);" +
                                    "} while(" + ShopModificationPatches.class.getName() + ".checkCanSpawnAndRemoveIfNecessary(cards2));");
                        }
                    }
                }
            };
        }

        public static boolean checkCanSpawnAndRemoveIfNecessary(ArrayList<AbstractCard> cards2) {
            AbstractCard c = cards2.get(cards2.size()-1);
            if(c instanceof SpawnModificationCard && !((SpawnModificationCard) c).canSpawn(cards2)) {
                //Removes the added card and returns true to the do-while loop so it generates a new card
                cards2.remove(c);
                return true;
            }
            return false;
        }
    }
}

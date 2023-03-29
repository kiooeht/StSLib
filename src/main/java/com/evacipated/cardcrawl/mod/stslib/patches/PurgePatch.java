package com.evacipated.cardcrawl.mod.stslib.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.PurgeField;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.HandCheckAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch2(
        clz=UseCardAction.class,
        method="update"
)
public class PurgePatch {

    @SpireInsertPatch(
            locator = Locator.class
    )
    public static SpireReturn<Void> poofCard(UseCardAction __instance, AbstractCard ___targetCard) {
        //at this point, the card has already been removed from "cardInUse", so if appropriate, we simply end the action before it handles any card manipulation
        if (PurgeField.purge.get(___targetCard)) {
            AbstractDungeon.effectList.add(new ExhaustCardEffect(___targetCard));
            AbstractDungeon.actionManager.addToBottom(new HandCheckAction());
            ReflectionHacks.privateMethod(AbstractGameAction.class, "tickDuration").invoke(__instance);
            return SpireReturn.Return();
        }
        return SpireReturn.Continue();
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(UseCardAction.class, "exhaustCard");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
        }
    }

}

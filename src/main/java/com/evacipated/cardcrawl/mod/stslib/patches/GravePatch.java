package com.evacipated.cardcrawl.mod.stslib.patches;

import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.GraveField;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.DiscardSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

@SpirePatch(
        cls="com.megacrit.cardcrawl.cards.CardGroup",
        method="initializeDeck"
)
public class GravePatch
{
    @SpireInsertPatch(
            rloc=4,
            localvars={"copy"}
    )
    public static void Insert(CardGroup __instance, CardGroup masterDeck, CardGroup copy)
    {
        ArrayList<AbstractCard> moveToDiscard = new ArrayList<>();
        for (AbstractCard c : copy.group) {
            if (GraveField.grave.get(c)) {
                moveToDiscard.add(c);
            }
        }

        for (AbstractCard c : moveToDiscard) {
            //copy.moveToDiscardPile(c);
            AbstractDungeon.actionManager.addToTop(new DiscardSpecificCardAction(c, AbstractDungeon.player.drawPile));
        }
    }
}

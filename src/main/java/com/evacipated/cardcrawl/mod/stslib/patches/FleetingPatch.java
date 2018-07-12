package com.evacipated.cardcrawl.mod.stslib.patches;

import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.FleetingField;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

@SpirePatch(
        cls="com.megacrit.cardcrawl.actions.utility.UseCardAction",
        method=SpirePatch.CONSTRUCTOR,
        paramtypes={"com.megacrit.cardcrawl.cards.AbstractCard", "com.megacrit.cardcrawl.core.AbstractCreature"}
)
public class FleetingPatch
{
    public static void Prefix(UseCardAction __instance, @ByRef AbstractCard[] card, AbstractCreature target)
    {
        if (FleetingField.fleeting.get(card[0])) {
            card[0].purgeOnUse = true;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                if (c.cardID.equals(card[0].cardID) && c.timesUpgraded == card[0].timesUpgraded && c.misc == card[0].misc && FleetingField.fleeting.get(c)) {
                    AbstractDungeon.player.masterDeck.removeCard(c);
                    break;
                }
            }
        }
    }
}

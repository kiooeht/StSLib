package com.evacipated.cardcrawl.mod.stslib.patches;

import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.FleetingField;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.PurgeField;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

@SpirePatch(
        clz=UseCardAction.class,
        method=SpirePatch.CONSTRUCTOR,
        paramtypez={AbstractCard.class, AbstractCreature.class}
)
public class FleetingPatch
{
    public static void Prefix(UseCardAction __instance, AbstractCard card, AbstractCreature target)
    {
        if (FleetingField.fleeting.get(card)) {
            PurgeField.purge.set(card, true);
            AbstractCard c = StSLib.getMasterDeckEquivalent(card);
            if (c != null) {
                AbstractDungeon.player.masterDeck.removeCard(c);
            }
        }
    }
}

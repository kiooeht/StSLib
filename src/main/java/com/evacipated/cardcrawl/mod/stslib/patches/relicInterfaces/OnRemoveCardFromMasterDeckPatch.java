package com.evacipated.cardcrawl.mod.stslib.patches.relicInterfaces;

import com.evacipated.cardcrawl.mod.stslib.relics.OnRemoveCardFromMasterDeckRelic;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

@SpirePatch(
        clz=CardGroup.class,
        method="removeCard",
        paramtypez=AbstractCard.class
)
public class OnRemoveCardFromMasterDeckPatch
{
    public static void Postfix(CardGroup __instance, AbstractCard c)
    {
        if (__instance.type == CardGroup.CardGroupType.MASTER_DECK) {
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (r instanceof OnRemoveCardFromMasterDeckRelic) {
                    ((OnRemoveCardFromMasterDeckRelic) r).onRemoveCardFromMasterDeck(c);
                }
            }
        }
    }
}

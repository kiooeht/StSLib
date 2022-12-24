package com.evacipated.cardcrawl.mod.stslib.patches;

import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.PersistFields;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnPersistPower;
import com.evacipated.cardcrawl.mod.stslib.relics.OnPersistRelic;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.List;

public class PersistPatch
{
    @SpirePatch2(
            clz = UseCardAction.class,
            method = "update"
    )
    public static class OnPlay
    {
        @SpireInsertPatch(
                locator = Locator1.class
        )
        public static void Insert1(AbstractCard ___targetCard)
        {
            if (PersistFields.persist.get(___targetCard) > 1) {
                ___targetCard.returnToHand = true;
            }
        }


        @SpireInsertPatch(
                locator = Locator2.class
        )
        public static void Insert2(AbstractCard ___targetCard)
        {
            if (PersistFields.persist.get(___targetCard) > 1) {
                ___targetCard.returnToHand = false;
                PersistFields.decrement(___targetCard);

                for (AbstractRelic r : AbstractDungeon.player.relics) {
                    if (r instanceof OnPersistRelic) {
                        ((OnPersistRelic) r).onPersist(___targetCard, PersistFields.persist.get(___targetCard));
                    }
                }
                for (AbstractPower p : AbstractDungeon.player.powers) {
                    if (p instanceof OnPersistPower) {
                        ((OnPersistPower) p).onPersist(___targetCard, PersistFields.persist.get(___targetCard));
                    }
                }
            }
        }

        private static class Locator1 extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "freeToPlayOnce");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }

        private static class Locator2 extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "exhaustOnUseOnce");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(
            clz = AbstractRoom.class,
            method = "applyEndOfTurnPreCardPowers"
    )
    public static class ResetCounter
    {
        public static void Prefix()
        {
            List<CardGroup> groups = new ArrayList<>();
            groups.add(AbstractDungeon.player.hand);
            groups.add(AbstractDungeon.player.drawPile);
            groups.add(AbstractDungeon.player.discardPile);
            groups.add(AbstractDungeon.player.exhaustPile);

            for (CardGroup group : groups) {
                for (AbstractCard card : group.group) {
                    if (PersistFields.basePersist.get(card) >= 0) {
                        PersistFields.persist.set(card, PersistFields.basePersist.get(card));
                    }
                }
            }
        }
    }
}

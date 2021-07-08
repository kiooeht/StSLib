package com.evacipated.cardcrawl.mod.stslib.swappables;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;

public class SwapperCardPatch {

    @SpirePatch(
            clz = AbstractCard.class,
            method = "update"
    )
    public static class AbstractCardUpdatePatch {

        public static void Prefix(AbstractCard __instance) {
            if (AbstractDungeon.player != null && (AbstractDungeon.player.isDraggingCard || AbstractDungeon.player.inSingleTargetMode) && __instance == AbstractDungeon.player.hoveredCard && SwapperHelper.isCardRegistered(__instance) && AbstractDungeon.actionManager.isEmpty()) {
                boolean clicked = SwapperHelper.handleMiddleClick(); //I blatantly stole this from blank
                if (clicked) {
                    System.out.println("SWAPPER CARD CHECKPOINT REACHED");
                    System.out.println("CARD TO SWAP: " + __instance.name);
                    int index = -1;
                    for (int i = 0; i < AbstractDungeon.player.hand.group.size(); i++) {
                        if (__instance == AbstractDungeon.player.hand.group.get(i)) {
                            index = i;
                        }
                    }
                    if (index != -1) {
                        if (__instance instanceof SwappableCard) {
                            SwappableCard swappableCard = (SwappableCard)__instance;
                            if (swappableCard.canSwap()) {
                                AbstractDungeon.actionManager.addToBottom(new SwapCardAction(__instance, SwapperHelper.getPairedCard(__instance), index));
                            } else {
                                AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0f, swappableCard.getUnableToSwapString(), true));
                            }
                        } else {
                            AbstractDungeon.actionManager.addToBottom(new SwapCardAction(__instance, SwapperHelper.getPairedCard(__instance), index));
                        }
                    } else {
                        System.out.println("How is clicked/hovered card not in hand?");
                    }
                }
            }
        }
    }

    @SpirePatch(
            clz = CardGroup.class,
            method = "initializeDeck"
    )
    public static class CardGroupInitializeDeckPatch {

        public static void Postfix(CardGroup __instance, CardGroup group) {
            SwapperHelper.initializeCombatList();
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "makeSameInstanceOf"
    )
    public static class AbstractCardMakeSameInstanceOfPatch {

        public static AbstractCard Postfix(AbstractCard __result, AbstractCard __instance) {
            if (SwapperHelper.isCardRegistered(__instance)) {
                AbstractCard card = SwapperHelper.getPairedCard(__instance).makeStatEquivalentCopy();
                card.uuid = __result.uuid;
                SwapperHelper.registerPair(__result, card);
                System.out.println("Swapper card detected as duplicated by make same instance of: duplicate pairing created");
            }
            return __result;
        }
    }

    @SpirePatch(
            clz = CardGroup.class,
            method = "addToTop"
    )
    public static class CardGroupAddToTopMasterDeckPatch {

        public static void Postfix(CardGroup __instance, AbstractCard card) {
            if (__instance == AbstractDungeon.player.masterDeck) {
                System.out.println("addToTop postfix: group is masterDeck");
                System.out.println("isCardRegistered: " + SwapperHelper.isCardRegistered(card));
                System.out.println("asMasterPair: " + SwapperHelper.isCardRegisteredAsMasterPair(card));
                if (SwapperHelper.isCardRegistered(card) && !SwapperHelper.isCardRegisteredAsMasterPair(card)) {
                    SwapperHelper.registerMasterDeckPair(card, SwapperHelper.getPairedCard(card));
                    System.out.println("Swapper card detected being added to master deck; registering pair.");
                }
            }
        }
    }
}

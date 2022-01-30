package com.evacipated.cardcrawl.mod.stslib.cards.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public interface SpawnModificationCard {
    /**
     * Get's called when the card is rolled and allows you to roll a new card.
     * Note: Does not remove the card from the possible rolls, so it can be rolled again in the same reward.
     * @param currentRewardCards List of the cards that have already been rolled and accepted.
     * @return If true, the card will be checked against currentRewardCards to see whether it's already in there, if not, the card gets added to it.
     *          If false, will be rerolled.
     */
    default boolean canSpawn(ArrayList<AbstractCard> currentRewardCards) {
        return true;
    }

    /**
     * If the card is rolled and passes its canSpawn check, you can return a new card instance that gets spawned instead.
     * @param currentRewardCards List of the cards that have already been rolled and accepted.
     * @return The instance of the card that will be checked against currentRewardCards to see whether it's already in there, if not, the card gets added to it.
     */
    default AbstractCard replaceWith(ArrayList<AbstractCard> currentRewardCards) {
        return (AbstractCard) this;
    }

    /**
     * Hook for final modifications once the card reward generation is over.
     * @param rewardCards The arraylist of the cards that would show up in the card reward.
     */
    default void onRewardListCreated(ArrayList<AbstractCard> rewardCards) {}
}

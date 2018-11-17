package com.evacipated.cardcrawl.mod.stslib.relics;

import com.megacrit.cardcrawl.rewards.RewardItem;

public interface OnSkipCardRelic
{
    void onSkipSingingBowl(RewardItem skippedItem);
    void onSkipCard(RewardItem skippedItem);
}

package com.evacipated.cardcrawl.mod.stslib.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public interface ModifyDamageRelic
{
    default int calculateCardDamageRelic(AbstractCard card, AbstractMonster target, int damage) {return damage;}

    default int calculateCardDamageFinalRelic(AbstractCard card, AbstractMonster target, int damage) {return damage;}

    default int applyPowersRelic(AbstractCard card, int damage) {return damage;}

    default int applyPowersFinalRelic(AbstractCard card, int damage) {return damage;}
}

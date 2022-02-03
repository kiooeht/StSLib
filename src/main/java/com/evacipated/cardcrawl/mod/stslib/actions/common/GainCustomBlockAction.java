package com.evacipated.cardcrawl.mod.stslib.actions.common;

import com.evacipated.cardcrawl.mod.stslib.blockmods.AbstractBlockModifier;
import com.evacipated.cardcrawl.mod.stslib.blockmods.BlockModContainer;
import com.evacipated.cardcrawl.mod.stslib.blockmods.BlockModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

import java.util.List;

public class GainCustomBlockAction extends AbstractGameAction {

    private final List<AbstractBlockModifier> mods;
    private final Object instigator;

    public GainCustomBlockAction(AbstractCard card, AbstractCreature target, int amount) {
        this.mods = BlockModifierManager.modifiers(card);
        this.instigator = card;
        this.target = target;
        this.amount = amount;
        this.duration = Settings.ACTION_DUR_XFAST;
        this.startDuration = Settings.ACTION_DUR_XFAST;
    }

    public GainCustomBlockAction(BlockModContainer container, AbstractCreature target, int amount) {
        this.mods = container.modifiers();
        this.instigator = container.instigator();
        this.target = target;
        this.amount = amount;
        this.duration = Settings.ACTION_DUR_XFAST;
        this.startDuration = Settings.ACTION_DUR_XFAST;
    }

    @Override
    public void update() {
        if (!this.target.isDying && !this.target.isDead && this.duration == this.startDuration) {
            AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, AttackEffect.SHIELD));
            BlockModifierManager.addCustomBlock(instigator, mods, target, amount);
            for (AbstractCard c : AbstractDungeon.player.hand.group) {
                c.applyPowers();
            }
        }
        tickDuration();
    }
}

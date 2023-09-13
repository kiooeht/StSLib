package com.evacipated.cardcrawl.mod.stslib.extraeffects;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.mod.stslib.dynamicdynamic.DynamicDynamicVariable;
import com.evacipated.cardcrawl.mod.stslib.dynamicdynamic.DynamicProvider;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.IronWave;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;
import java.util.UUID;

public abstract class ExtraEffectModifier extends AbstractCardModifier implements DynamicProvider {
    protected static final String STRING_ID = "stslib:ExtraEffectModifier";
    protected static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(STRING_ID);
    private final UUID uuid;
    private final VariableType type;
    private Proxy proxy;
    protected int amount;
    public int baseValue;

    public ExtraEffectModifier(VariableType type, int value) {
        this(type, value, 1);
    }

    public ExtraEffectModifier(VariableType type, int value, int times) {
        this.type = type;
        baseValue = value;
        amount = times;
        uuid = UUID.randomUUID();
    }

    /**
     * to be used the same way you would use any effects in a card's {@link AbstractCard#use(AbstractPlayer, AbstractMonster) use} method. if the effect is {@link ExtraEffectModifier#isMultiInstanced(AbstractCard) multi instanced}, this will be called a number of times equal to {@link ExtraEffectModifier#amount amount}.
     * @param card the attached card.
     * @param p the player.
     * @param m the target, if any.
     */
    public abstract void doExtraEffects(AbstractCard card, AbstractPlayer p, AbstractCreature m);

    /**
     * will be formatted with this object's dynamic variable key, then added to the start of the card's text if {@link AbstractCardModifier#priority priority} is less than 0, else it is added to the end. If the effect is {@link ExtraEffectModifier#isMultiInstanced(AbstractCard) multi instanced} and {@link ExtraEffectModifier#amount amount} is greater than 1, the text will automatically be modified with the number of times the effect will take place.
     * @param card the attached card.
     * @return the string to be added, which contains "%s" wherever you want the dynamic variable to appear, if anywhere.
     */
    public abstract String getExtraText(AbstractCard card);

    /**
     * same as AbstractCardModifier's {@link AbstractCardModifier#identifier(AbstractCard) identifier} method, made abstract to require implementation. A unique identifier is required for some functions of ExtraEffectModifier.
     * @param card the attached card.
     * @return a unique string representing this effect.
     */
    public abstract String getEffectId(AbstractCard card);

    /**
     * controls whether this will be combined with another effect with the same {@link AbstractCardModifier#identifier(AbstractCard) identifier} when being applied to a card.
     * @param card the card to be attached to.
     * @return true if the effects can stack.
     */
    protected boolean canStack(AbstractCard card) {
        return true;
    }

    /**
     * when this effect {@link ExtraEffectModifier#canStack(AbstractCard) can stack}, this method controls whether its {@link ExtraEffectModifier#amount amount} or {@link ExtraEffectModifier#baseValue value} will increase when stacking.
     * @param card the card attached or to be attached to.
     * @return true to increment {@link ExtraEffectModifier#amount amount} or false to add {@link ExtraEffectModifier#baseValue value}
     */
    protected boolean isMultiInstanced(AbstractCard card) {
        return true;
    }

    /**
     * To make it so that no AbstractCard is ever stored directly on one of these modifiers to make it so these modifiers can be serialized,
     * a proxy iron wave is used to calculate the damage and block of the modifier. Override this method to add additional properties changes
     * (something similar to retain, for example) that your damage calculation might depend on. Note that the base damage and block values of the
     * proxy are already set to this effect's assigned values.
     * @param card the card to set values from
     * @param proxy the card to set values to
     */
    protected void inheritValues(AbstractCard card, IronWave proxy) {
        proxy.isEthereal = card.isEthereal;
        proxy.exhaust = card.exhaust;
        proxy.color = card.color;
        proxy.type = card.type;
        proxy.target = card.target;
        proxy.rarity = card.rarity;
        proxy.timesUpgraded = card.timesUpgraded;
        proxy.upgraded = card.upgraded;
        proxy.cardID = card.cardID;
        proxy.cardsToPreview = card.cardsToPreview;
    }

    @Override
    public void onApplyPowers(AbstractCard card) {
        proxy = Proxy.of(card).setValueFor(type, baseValue).calculate(null, this);
    }

    @Override
    public void onCalculateCardDamage(AbstractCard card, AbstractMonster mo) {
        proxy = Proxy.of(card).setValueFor(type, baseValue).calculate(mo, this);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (isMultiInstanced(card)) {
            for (int i = 0; i < amount; ++i) {
                doExtraEffects(card, AbstractDungeon.player, target);
            }
        } else {
            doExtraEffects(card, AbstractDungeon.player, target);
        }
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        String addition = String.format(getExtraText(card), "!" + DynamicProvider.generateKey(card, this) + "!");
        if (isMultiInstanced(card)) {
            addition = applyTimes(addition);
        }
        if (priority < 0) {
            return addition + " NL " + rawDescription;
        } else {
            return rawDescription + " NL " + addition;
        }
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        if (canStack(card)) {
            ArrayList<AbstractCardModifier> list = CardModifierManager.getModifiers(card, getEffectId(card));
            if (!list.isEmpty()) {
                boolean changed = false;
                for (AbstractCardModifier mod : list) {
                    ExtraEffectModifier effect = (ExtraEffectModifier) mod;
                    if (isMultiInstanced(card)) {
                        if (effect.baseValue == baseValue) {
                            effect.amount += amount;
                            changed = true;
                            break;
                        }
                    } else {
                        effect.baseValue += baseValue;
                        changed = true;
                        break;
                    }
                }
                if (changed) {
                    card.applyPowers();
                    card.initializeDescription();
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String identifier(AbstractCard card) {
        return getEffectId(card);
    }

    @Override
    public int baseValue(AbstractCard card) {
        return baseValue;
    }

    @Override
    public int value(AbstractCard card) {
        return proxy.getValueFor(type);
    }

    @Override
    public boolean isModified(AbstractCard card) {
        return proxy.getValueFor(type) != baseValue;
    }

    @Override
    public UUID getDynamicUUID() {
        return uuid;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        DynamicDynamicVariable.registerVariable(card, this);
        proxy = Proxy.of(card).setValueFor(type, baseValue).calculate(null, this);
    }

    protected String applyTimes(String s) {
        if (amount > 1) {
            if (s.endsWith(LocalizedStrings.PERIOD)) {
                s = s.substring(0, s.length() - 1);
            }
            s = String.format(cardStrings.DESCRIPTION, s, amount);
        }
        return s;
    }

    protected enum VariableType {
        DAMAGE,
        BLOCK,
        MAGIC
    }

    protected static class Proxy {
        private final int damage;
        private final int block;
        private final int magicNumber;

        private Proxy(int damage, int block, int magicNumber) {
            this.damage = damage;
            this.block = block;
            this.magicNumber = magicNumber;
        }

        protected int getDamage() {
            return damage;
        }

        protected int getBlock() {
            return block;
        }

        protected int getMagicNumber() {
            return magicNumber;
        }

        protected int getValueFor(VariableType type) {
            switch (type) {
                default: return getDamage();
                case BLOCK: return getBlock();
                case MAGIC: return getMagicNumber();
            }
        }

        protected static Builder of(AbstractCard card) {
            return new Builder(card);
        }

        private static class Builder {
            private static final IronWave DUMMY = new IronWave();
            private final AbstractCard card;
            private int baseDamage;
            private int baseBlock;
            private int baseMagicNumber;

            private Builder(AbstractCard card) {
                this.card = card;
                baseDamage = card.baseDamage;
                baseBlock = card.baseBlock;
                baseMagicNumber = card.baseMagicNumber;
            }

            protected Builder baseDamage(int damage) {
                this.baseDamage = damage;
                return this;
            }

            protected Builder baseBlock(int block) {
                this.baseBlock = block;
                return this;
            }

            protected Builder baseMagicNumber(int magicNumber) {
                this.baseMagicNumber = magicNumber;
                return this;
            }

            protected Builder setValueFor(VariableType type, int value) {
                switch (type) {
                    default: return baseDamage(value);
                    case BLOCK: return baseBlock(value);
                    case MAGIC: return baseMagicNumber(value);
                }
            }

            protected Proxy calculate(AbstractMonster m, ExtraEffectModifier effect) {
                IronWave proxy = DUMMY;
                proxy.baseDamage = proxy.damage = baseDamage;
                proxy.baseBlock = proxy.block = baseBlock;
                proxy.baseMagicNumber = proxy.magicNumber = baseMagicNumber;
                effect.inheritValues(card, proxy);
                CardModifierManager.removeAllModifiers(proxy, true);
                for (AbstractCardModifier mod : CardModifierManager.modifiers(card)) {
                    if (!(mod instanceof ExtraEffectModifier)) {
                        CardModifierManager.addModifier(card, mod.makeCopy());
                    }
                }
                if (AbstractDungeon.player != null) {
                    if (m == null) {
                        proxy.applyPowers();
                    } else {
                        proxy.calculateCardDamage(m);
                    }
                }
                return new Proxy(proxy.damage, proxy.block, proxy.magicNumber);
            }
        }
    }
}

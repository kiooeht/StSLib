package com.evacipated.cardcrawl.mod.stslib.cards.targeting;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.patches.CustomTargeting;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public abstract class TargetingHandler<T> {
    /* Assigns the current target to a card.
     * Can be stored in a SpireField on the card, or if this targeting is exclusively for your own cards, you could store it in your card class.
     * After this, you just need to access it in the use method.
     */
    public void lockTarget(AbstractCard card) {
        T target = getHovered();
        if (isValidTarget(target)) {
            CustomTargeting.setCardTarget(card, getHovered());
        }
        else {
            CustomTargeting.setCardTarget(card, null);
        }
    }

    public abstract void updateHovered(); //Update/check what valid target is currently hovered
    public abstract T getHovered(); //Get currently hovered target.
    public abstract void clearHovered(); //Clear current target when leaving targeting mode
    public abstract boolean hasTarget(); //Whether a valid target is hovered
    public boolean isValidTarget(T target) { //Used for things like double play to ensure the target is still valid.
        return true;
    }

    //Render reticle on the current target
    public void renderReticle(SpriteBatch sb) {
    }

    //Update keyboard targeting logic. Optional to implement.
    public void updateKeyboardTarget() {

    }
    //Assigns target to the hovered variable
    public void setDefaultTarget() { }
    //Sets targeting position when card is initially selected while in keyboard mode
    public int getDefaultTargetX() {
        return InputHelper.mX;
    }
    public int getDefaultTargetY() {
        return InputHelper.mY;
    }

    //Returns position where card stays while targeting arrow is visible
    public float cardTargetingX() {
        return Settings.WIDTH / 2.0F;
    }
    public float cardTargetingY() {
        return AbstractCard.IMG_HEIGHT * 0.75F / 2.5F;
    }
}

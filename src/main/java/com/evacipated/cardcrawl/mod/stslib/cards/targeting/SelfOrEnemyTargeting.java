package com.evacipated.cardcrawl.mod.stslib.cards.targeting;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.patches.CustomTargeting;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class SelfOrEnemyTargeting extends TargetingHandler<AbstractCreature> {
    @SpireEnum
    public static AbstractCard.CardTarget SELF_OR_ENEMY;

    public static AbstractCreature getTarget(AbstractCard card) {
        return CustomTargeting.getCardTarget(card);
    }

    private AbstractCreature hovered = null;

    @Override
    public boolean hasTarget() {
        return hovered != null;
    }

    @Override
    public void updateHovered() {
        hovered = null;

        AbstractDungeon.player.hb.update();
        if (AbstractDungeon.player.hb.hovered) {
            hovered = AbstractDungeon.player;
        }
        else {
            for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                if (!m.isDeadOrEscaped()) {
                    m.hb.update();
                    if (m.hb.hovered) {
                        hovered = m;
                        break;
                    }
                }
            }
        }
    }

    @Override
    public AbstractCreature getHovered() {
        return hovered;
    }

    @Override
    public void clearHovered() {
        hovered = null;
    }

    @Override
    public void renderReticle(SpriteBatch sb) {
        if (hovered != null) {
            hovered.renderReticle(sb);
        }
    }

    //Keyboard support is entirely optional, but this is an example based on how the basegame implements it
    @Override
    public void setDefaultTarget() {
        hovered = AbstractDungeon.player;
    }
    @Override
    public int getDefaultTargetX() {
        return (int) AbstractDungeon.player.hb.cX;
    }
    @Override
    public int getDefaultTargetY() {
        return (int) AbstractDungeon.player.hb.cY;
    }

    @Override
    public void updateKeyboardTarget() {
        int directionIndex = 0;

        if (InputActionSet.left.isJustPressed() || CInputActionSet.left.isJustPressed() || CInputActionSet.altLeft.isJustPressed()) {
            --directionIndex;
        }

        if (InputActionSet.right.isJustPressed() || CInputActionSet.right.isJustPressed() || CInputActionSet.altRight.isJustPressed()) {
            ++directionIndex;
        }

        if (directionIndex != 0) {
            ArrayList<AbstractMonster> sortedMonsters = new ArrayList<>(AbstractDungeon.getCurrRoom().monsters.monsters);

            sortedMonsters.removeIf(AbstractCreature::isDeadOrEscaped);

            AbstractCreature newTarget = null;

            if (sortedMonsters.isEmpty()) {
                if (hovered == null) {
                    newTarget = AbstractDungeon.player;
                }
                else {
                    return;
                }
            }
            else {
                sortedMonsters.sort(AbstractMonster.sortByHitbox);

                if (this.hovered == null) {
                    if (directionIndex == 1) {
                        newTarget = sortedMonsters.get(0);
                    } else {
                        newTarget = AbstractDungeon.player;
                    }
                } else {
                    if (hovered == AbstractDungeon.player) {
                        if (directionIndex == 1) {
                            newTarget = sortedMonsters.get(0);
                        } else {
                            newTarget = sortedMonsters.get(sortedMonsters.size() - 1);
                        }
                    }
                    else if (hovered instanceof AbstractMonster) {
                        int currentTargetIndex = sortedMonsters.indexOf(hovered);
                        int newTargetIndex = currentTargetIndex + directionIndex;

                        if (newTargetIndex == -1) {
                            newTarget = AbstractDungeon.player;
                        }
                        else {
                            newTargetIndex = (newTargetIndex + sortedMonsters.size()) % sortedMonsters.size();
                            newTarget = sortedMonsters.get(newTargetIndex);
                        }
                    }
                }
            }


            if (newTarget != null) {
                Hitbox target = newTarget.hb;
                Gdx.input.setCursorPosition((int)target.cX, Settings.HEIGHT - (int)target.cY); //cursor y position is inverted for some reason :)
                hovered = newTarget;
                ReflectionHacks.setPrivate(AbstractDungeon.player, AbstractPlayer.class, "isUsingClickDragControl", true);
                AbstractDungeon.player.isDraggingCard = true;
            }

            if (hovered instanceof AbstractMonster && hovered.halfDead) {
                hovered = null;
            }
        }
    }
}

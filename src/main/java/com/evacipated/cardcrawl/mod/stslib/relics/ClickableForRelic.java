package com.evacipated.cardcrawl.mod.stslib.relics;

import basemod.ClickableUIElement;
import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.evacipated.cardcrawl.mod.stslib.vfx.combat.FlashClickRelicEffect;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.util.ArrayList;

import static com.megacrit.cardcrawl.core.Settings.xScale;

public class ClickableForRelic extends ClickableUIElement
{
    public static final float CE_Y = 132.0F;
    public static final float CE_W = 64f;
    public static final float CE_H = 48f;
    public static final float Y_INCREMENT = 56f;

    private AbstractRelic relic;
    private RelicWithButton relicUI;

    private static ArrayList<ClickableForRelic> clickableList;

    private static final ShaderProgram shade = new ShaderProgram(
            Gdx.files.internal("shaders/stslib/light.vs"),
            Gdx.files.internal("shaders/stslib/light.fs")
    );

    private boolean grayscale;
    public boolean firstBattle;

    public ClickableForRelic(RelicWithButton relicUI, float x, float y, float width, float height)
    {
        super(relicUI.getTexture(), x, y, width, height);

        this.relicUI = relicUI;
        if (relicUI instanceof AbstractRelic) {
            relic = (AbstractRelic) relicUI;
        }

        firstBattle = true;
        grayscale = relicUI.isButtonDisabled();
    }

    protected void addToBot(AbstractGameAction action)
    {
        AbstractDungeon.actionManager.addToBottom(action);
    }

    @Override
    protected void onHover()
    {
        if (relic == null) {
            return;
        }

        float y = TipHelper.calculateToAvoidOffscreen(relicUI.getHoverTips(), InputHelper.mY);
        TipHelper.queuePowerTips(
                (float) InputHelper.mX + 60.0F * Settings.scale,
                InputHelper.mY + y,
                relicUI.getHoverTips()
        );
    }

    @Override
    protected void onClick()
    {
        if (relic == null) {
            return;
        }
        if (!AbstractDungeon.actionManager.turnHasEnded && !AbstractDungeon.isScreenUp &&
                AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT &&
                !AbstractDungeon.actionManager.usingCard &&
                !relicUI.isButtonDisabled()) {
            relicUI.onButtonPress();
        }
    }

    public void firstBattleFlash()
    {
        if (relic == null) {
            return;
        }
        AbstractGameEffect effect = new FlashClickRelicEffect(this);
        addToBot(new AbstractGameAction()
        {
            @Override
            public void update()
            {
                if (duration == startDuration) {
                    firstBattle = false;
                }
                isDone = true;
            }
        });
        addToBot(new VFXAction(effect));
    }

    @Override
    protected void onUnhover()
    {
    }

    @Override
    public void update()
    {
        if (AbstractDungeon.overlayMenu == null || AbstractDungeon.overlayMenu.energyPanel == null || relic == null) {
            return;
        }
        int orbWidth = ReflectionHacks.getPrivate(AbstractDungeon.overlayMenu.energyPanel, EnergyPanel.class, "RAW_W");
        float orbWidthFloat = orbWidth * 1f;
        setX(AbstractDungeon.overlayMenu.energyPanel.current_x - (orbWidthFloat * 0.4f + CE_W / 2f) * xScale);
        grayscale = relicUI.isButtonDisabled();
        super.update();
    }

    @Override
    public void render(SpriteBatch sb)
    {
        if (firstBattle) {
            return;
        }
        if (grayscale) {
            super.render(sb, Color.GRAY.cpy());
        } else {
            if (!hitbox.hovered) {
                render(sb, Color.WHITE.cpy());
            } else {
                ShaderProgram oldShade = sb.getShader();
                sb.setShader(shade);
                super.render(sb, Color.WHITE.cpy());
                sb.setShader(oldShade);
            }
        }
    }

    public static ArrayList<ClickableForRelic> getClickableList()
    {
        if (clickableList == null) {
            clickableList = new ArrayList<>();
        }

        return clickableList;
    }

    public static void updateClickableList()
    {
        if (clickableList == null) {
            clickableList = new ArrayList<>();
        }

        clickableList.removeIf(clicky -> clicky.getRelic() == null);

        if (AbstractDungeon.player != null) {
            clickableList.removeIf(clicky -> !AbstractDungeon.player.relics.contains(clicky.relic));
        } else {
            clickableList.clear();
            return;
        }

        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic instanceof RelicWithButton) {
                boolean existingClicky = false;
                for (ClickableForRelic clicky : clickableList) {
                    if (clicky.relic == relic) {
                        existingClicky = true;
                        break;
                    }
                }
                if (!existingClicky) {
                    ClickableForRelic newClicky = new ClickableForRelic(
                            (RelicWithButton) relic,
                            0f,
                            ClickableForRelic.CE_Y + (1 + clickableList.size()) * Y_INCREMENT,
                            ClickableForRelic.CE_W,
                            ClickableForRelic.CE_H
                    );
                    clickableList.add(newClicky);
                }
            }
        }
    }

    public AbstractRelic getRelic()
    {
        return relic;
    }
}
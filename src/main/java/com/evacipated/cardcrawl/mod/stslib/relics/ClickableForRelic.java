package com.evacipated.cardcrawl.mod.stslib.relics;

import basemod.ClickableUIElement;
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
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.util.ArrayList;

public class ClickableForRelic extends ClickableUIElement {
    public static final float CE_X = 64.0F;
    public static final float CE_Y = 132.0F*Settings.yScale/Settings.scale;
    public static final float CE_W = 64f;
    public static final float CE_H = 48f;
    public static final float Y_INCREMENT = 56f;

    private AbstractRelic relic;
    private ClickableRelicWithUI relicUI;

    private static ArrayList<ClickableForRelic> clickableList;

    private static final String vertexLightShader = "attribute vec4 a_position;\n"
            + "attribute vec4 a_color;\n"
            + "attribute vec2 a_texCoord0;\n"
            + "uniform mat4 u_projTrans;\n"
            + "varying vec4 v_color;\n"
            + "varying vec2 v_texCoords;\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "   v_color = a_color;\n"
            + "   v_color.r = v_color.r * 1.15;\n"
            + "   v_color.g = v_color.g * 1.15;\n"
            + "   v_color.b = v_color.b * 1.15;\n"
            + "   v_texCoords = a_texCoord0;\n"
            + "   v_color.a = pow(v_color.a * (255.0/254.0) + 0.5, 1.709);\n"
            + "   gl_Position =  u_projTrans * a_position;\n"
            + "}\n";

    private static final String fragmentLightShader =
            "#ifdef GL_ES\n" +
                    "#define LOWP lowp\n" +
                    "   precision mediump float;\n" +
                    "#else\n" +
                    "   #define LOWP\n" +
                    "#endif\n" +

                    "varying LOWP vec4 v_color;\n" +
                    "varying vec2 v_texCoords;\n" +

                    "uniform sampler2D u_texture;\n" +

                    "void main()\n" +
                    "{\n" +
                    "   gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" +
                    "   gl_FragColor.r = gl_FragColor.r * 1.15;\n" +
                    "   gl_FragColor.g = gl_FragColor.g * 1.15;\n" +
                    "   gl_FragColor.b = gl_FragColor.b * 1.15;\n" +
                    "}\n";

    private static final ShaderProgram shade = new ShaderProgram(vertexLightShader, fragmentLightShader);

    private boolean grayscale;
    public boolean firstBattle;
    private float hoverTimer;

    public ClickableForRelic(ClickableRelicWithUI relicUI, float x, float y, float width, float height) {
        super(relicUI.getTexture(), x, y, width, height);
            this.relicUI = relicUI;
            if (relicUI instanceof AbstractRelic)
            relic = (AbstractRelic) relicUI;

            firstBattle = true;
            grayscale = relicUI.isButtonDisabled();
            hoverTimer = 0f;
    }

    protected void addToBot(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToBottom(action);
    }

    @Override
    protected void onHover() {
        if (relic == null)
            return;
        hoverTimer += Gdx.graphics.getDeltaTime();
        if (hoverTimer > 0.2f) {
            float y = TipHelper.calculateToAvoidOffscreen(relicUI.getHoverTips(), InputHelper.mY);
            TipHelper.queuePowerTips((float) InputHelper.mX + 60.0F * Settings.scale, InputHelper.mY + y,
                    relicUI.getHoverTips());
        }
    }

    @Override
    protected void onClick() {
        if (relic == null)
            return;
        if (!AbstractDungeon.actionManager.turnHasEnded && !AbstractDungeon.isScreenUp &&
                AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT &&
                !AbstractDungeon.actionManager.usingCard &&
                !relicUI.isButtonDisabled()) {
            relicUI.onButtonPress();
        }
    }

    public void firstBattleFlash() {
        if (relic == null)
            return;
        AbstractGameEffect effect = new FlashClickRelicEffect(this);
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (duration == startDuration)
                    firstBattle = false;
                isDone = true;
            }
        });
        addToBot(new VFXAction(effect));
    }

    @Override
    protected void onUnhover() {
        hoverTimer = 0f;
    }

    @Override
    public void update() {
        if (AbstractDungeon.overlayMenu == null || AbstractDungeon.overlayMenu.energyPanel == null)
            return;
        if (relic == null)
            return;
        float deltaX = AbstractDungeon.overlayMenu.energyPanel.show_x -
                AbstractDungeon.overlayMenu.energyPanel.current_x;
        setX(CE_X*Settings.scale - deltaX);
        grayscale = relicUI.isButtonDisabled();
        super.update();
    }

    @Override
    public void render(SpriteBatch sb) {
        if (firstBattle)
            return;
        if (grayscale)
            super.render(sb, Color.GRAY.cpy());
        else {
            if (!hitbox.hovered)
                render(sb, Color.WHITE.cpy());
            else {
                ShaderProgram oldShade = sb.getShader();
                sb.setShader(shade);
                super.render(sb, Color.WHITE.cpy());
                sb.setShader(oldShade);
            }
        }
    }

    public static ArrayList<ClickableForRelic> getClickableList() {
        if (clickableList == null)
            clickableList = new ArrayList<>();

        return clickableList;
    }

    public static void updateClickableList() {
        if (clickableList == null)
            clickableList = new ArrayList<>();

        clickableList.removeIf(clicky -> clicky.getRelic() == null);

        if (AbstractDungeon.player != null)
            clickableList.removeIf(clicky -> !AbstractDungeon.player.relics.contains(clicky.relic));
        else
            clickableList.clear();

        if (AbstractDungeon.player == null)
            return;

        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic instanceof ClickableRelicWithUI) {
                boolean existingClicky = false;
                for (ClickableForRelic clicky : clickableList) {
                    if (clicky.relic == relic) {
                        existingClicky = true;
                        break;
                    }
                }
                if (!existingClicky) {
                    ClickableForRelic newClicky = new ClickableForRelic((ClickableRelicWithUI) relic,
                            ClickableForRelic.CE_X, ClickableForRelic.CE_Y + (1 + clickableList.size()) * Y_INCREMENT,
                            ClickableForRelic.CE_W, ClickableForRelic.CE_H);
                    clickableList.add(newClicky);
                }
            }
        }
    }

    public AbstractRelic getRelic() {return relic;}
}
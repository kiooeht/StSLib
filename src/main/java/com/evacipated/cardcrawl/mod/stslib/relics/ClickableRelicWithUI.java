package com.evacipated.cardcrawl.mod.stslib.relics;

import basemod.AutoAdd;
import basemod.BaseMod;
import basemod.ClickableUIElement;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.evacipated.cardcrawl.mod.stslib.vfx.combat.FlashClickRelicEffect;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

@AutoAdd.Ignore
public abstract class ClickableRelicWithUI extends CustomRelic {
    private RelicClickable element;

    // Constructor of ClickableUIElement scales its inputs with Settings.scale
    private static final float CE_X = 64.0F;
    private static final float CE_Y = 132.0F*Settings.yScale/Settings.scale;
    private static final float CE_W = 64f;
    private static final float CE_H = 48f;
    private static final float Y_INCREMENT = 56f;
    private int order = 0;

    public static final String vertexLightShader = "attribute vec4 a_position;\n"
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

    public static final String fragmentLightShader =
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

    protected static final ShaderProgram shade = new ShaderProgram(vertexLightShader, fragmentLightShader);

    protected boolean firstBattle = true;

    public ClickableRelicWithUI(String ID, AbstractRelic.RelicTier tier, AbstractRelic.LandingSound sound,
                                Texture relicTexture, Texture elementTexture) {
        super(ID, relicTexture, tier, sound);
        if (AbstractDungeon.player != null) {
            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (relic instanceof ClickableRelicWithUI)
                    order++;
            }
        }
        element = new RelicClickable(elementTexture);
    }

    @Override
    public void atTurnStartPostDraw() {
        if (firstBattle && !grayscale)
            element.firstBattleFlash();
    }

    @Override
    public void update() {
        super.update();
        element.update();
    }

    public RelicClickable getElement() {return element;}

    public abstract void buttonPress();

    public class RelicClickable extends ClickableUIElement {
        public boolean elementGrayscale = false;

        public RelicClickable(Texture texture) {
            super(texture, CE_X, CE_Y + order * Y_INCREMENT, CE_W, CE_H);
            BaseMod.addSaveField(relicId, new CustomSavable<Boolean>() {
                @Override
                public void onLoad(Boolean savedValue)
                {
                    firstBattle = savedValue;
                }

                @Override
                public Boolean onSave()
                {
                    return firstBattle;
                }
            });
        }

        @Override
        protected void onHover() {
            if (firstBattle)
                return;
            float y = TipHelper.calculateToAvoidOffscreen(tips, InputHelper.mY);
            TipHelper.queuePowerTips((float)InputHelper.mX + 60.0F * Settings.scale, InputHelper.mY + y, tips);
        }

        @Override
        protected void onClick() {
            if (!AbstractDungeon.actionManager.turnHasEnded && !AbstractDungeon.isScreenUp &&
                    AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT &&
                    !AbstractDungeon.actionManager.usingCard && !grayscale) {
                buttonPress();
            }
        }

        protected void firstBattleFlash() {
            AbstractGameEffect effect = new FlashClickRelicEffect(ClickableRelicWithUI.this);
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    if (duration == startDuration)
                        firstBattle = false;
                    isDone = true;
                }
            });
            addToBot(new VFXAction(effect, 0.5f));
        }

        @Override
        protected void onUnhover() {
        }

        // Because of course it can't be consistent, setX of ClickableUIElement does NOT scale its input
        // with Settings.scale
        @Override
        public void update() {
            if (AbstractDungeon.overlayMenu == null || AbstractDungeon.overlayMenu.energyPanel == null)
                return;
            float deltaX = AbstractDungeon.overlayMenu.energyPanel.show_x -
                    AbstractDungeon.overlayMenu.energyPanel.current_x;
            setX(CE_X*Settings.scale - deltaX);
            super.update();
        }
    }

    public void doRender(SpriteBatch sb) {
        if (firstBattle)
            return;
        if (grayscale || element.elementGrayscale)
            element.render(sb, Color.GRAY.cpy());
        else {
            Hitbox hitbox = ReflectionHacks.getPrivate(element, ClickableUIElement.class, "hitbox");
            if (!hitbox.hovered)
                getElement().render(sb, Color.WHITE.cpy());
            else {
                sb.setColor(Color.WHITE.cpy());
                ShaderProgram oldShade = sb.getShader();
                sb.setShader(shade);
                element.render(sb);
                sb.setShader(oldShade);
            }
        }
    }
}

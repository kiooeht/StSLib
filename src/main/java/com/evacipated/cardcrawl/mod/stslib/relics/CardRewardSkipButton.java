package com.evacipated.cardcrawl.mod.stslib.relics;

import basemod.ClickableUIElement;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.CardRewardScreen;

public class CardRewardSkipButton extends ClickableUIElement {

    private static final float HITBOX_OFFSET_X = ImageMaster.REWARD_SCREEN_TAKE_BUTTON.getWidth() * 0.2f;
    private static final float HITBOX_OFFSET_Y = ImageMaster.REWARD_SCREEN_TAKE_BUTTON.getHeight() * 0.3f;

    private final CardRewardSkipButtonRelic relic;

    private final String label;

    boolean lighten = false;


    public CardRewardSkipButton(CardRewardSkipButtonRelic relic) {
        super(relic.getTexture(), 0, 0, ImageMaster.REWARD_SCREEN_TAKE_BUTTON.getWidth()*0.6f,ImageMaster.REWARD_SCREEN_TAKE_BUTTON.getHeight()*0.4f);
        this.relic = relic;
        label = relic.getButtonLabel();
        this.hitbox = new Hitbox(hb_w, hb_h);

    }

    @Override
    protected void onHover() {
        if (hitbox.justHovered) CardCrawlGame.sound.play("UI_HOVER");
        lighten = true;
    }

    @Override
    protected void onUnhover() {
        lighten = false;
    }

    @Override
    protected void onClick() {
        relic.onClickedButton();
        closeReward();
    }

    private void closeReward() {
        ReflectionHacks.privateMethod(CardRewardScreen.class, "takeReward").invoke(AbstractDungeon.cardRewardScreen);
        AbstractDungeon.closeCurrentScreen();
    }

    public void move(float newX, float newY) {
        setX(newX);
        setY(newY);
    }

    @Override
    public void setX(float x) {
        this.x = x;
        hitbox.x = x + HITBOX_OFFSET_X;
    }

    @Override
    public void setY(float y) {
        this.y = y;
        hitbox.y = y + HITBOX_OFFSET_Y;
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        sb.draw(relic.getTexture(), x, y);
        if (lighten) {
            sb.setBlendFunction(770, 1);
            sb.setColor(new Color(1f,1f,1f,0.3f));
            sb.draw(relic.getTexture(),x , y);
            sb.setBlendFunction(770, 771);
        }

        float textX = x + relic.getTexture().getWidth()/2f;
        float textY = y + relic.getTexture().getHeight()/2f;
        FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, label, textX, textY, Color.WHITE, 1F);
        renderHitbox(sb);
    }


}

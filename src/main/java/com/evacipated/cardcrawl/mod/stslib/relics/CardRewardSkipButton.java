package com.evacipated.cardcrawl.mod.stslib.relics;

import basemod.ClickableUIElement;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.CardRewardScreen;

public class CardRewardSkipButton extends ClickableUIElement {

    private static final float HITBOX_OFFSET_X = ImageMaster.REWARD_SCREEN_TAKE_BUTTON.getWidth() * 0.2f * Settings.scale;
    private static final float HITBOX_OFFSET_Y = ImageMaster.REWARD_SCREEN_TAKE_BUTTON.getHeight() * 0.3f * Settings.scale;

    private final CardRewardSkipButtonRelic relic;
    private final TextureRegion texture;

    private final String label;

    boolean lighten = false;


    public CardRewardSkipButton(CardRewardSkipButtonRelic relic) {
        super(relic.getTexture(), 0, 0, ImageMaster.REWARD_SCREEN_TAKE_BUTTON.getWidth()*0.6f,ImageMaster.REWARD_SCREEN_TAKE_BUTTON.getHeight()*0.35f);
        this.relic = relic;
        label = relic.getButtonLabel();
        texture = new TextureRegion(relic.getTexture());
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
        sb.draw(texture, x, y, texture.getRegionWidth()/2f * Settings.scale, texture.getRegionHeight()/2f * Settings.scale, texture.getRegionWidth()* Settings.scale, texture.getRegionHeight()* Settings.scale, 1f, 1f, 0f);
        if (lighten) {
            sb.setBlendFunction(770, 1);
            sb.setColor(new Color(1f,1f,1f,0.3f));
            sb.draw(texture, x, y, texture.getRegionWidth()/2f * Settings.scale, texture.getRegionHeight()/2f * Settings.scale, texture.getRegionWidth()* Settings.scale, texture.getRegionHeight()* Settings.scale, 1f, 1f, 0f);
            sb.setBlendFunction(770, 771);
        }

        float textX = x + texture.getRegionWidth()/2f * Settings.scale;
        float textY = y + texture.getRegionHeight()/2f * Settings.scale;
        FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, label, textX, textY, Color.WHITE, 1F);
        renderHitbox(sb);
    }


}

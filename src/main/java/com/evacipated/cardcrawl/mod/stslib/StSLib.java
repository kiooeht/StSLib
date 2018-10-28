package com.evacipated.cardcrawl.mod.stslib;

import basemod.BaseMod;
import basemod.interfaces.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.variables.ExhaustiveVariable;
import com.evacipated.cardcrawl.mod.stslib.variables.RefundVariable;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.Keyword;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@SpireInitializer
public class StSLib implements
        PostInitializeSubscriber,
        EditKeywordsSubscriber,
        EditStringsSubscriber,
        EditCardsSubscriber
{
    public static Texture TEMP_HP_ICON;

    public static void initialize()
    {
        BaseMod.subscribe(new StSLib());
    }

    @Override
    public void receivePostInitialize()
    {
        TEMP_HP_ICON = ImageMaster.loadImage("images/stslib/ui/tempHP.png");
    }

    @Override
    public void receiveEditKeywords()
    {
        Gson gson = new Gson();
        String json = Gdx.files.internal("localization/stslib/keywords.json").readString(String.valueOf(StandardCharsets.UTF_8));
        Keyword[] keywords = gson.fromJson(json, Keyword[].class);

        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(keyword.NAMES, keyword.DESCRIPTION);
            }
        }
    }

    @Override
    public void receiveEditStrings()
    {
        BaseMod.loadCustomStringsFile(PowerStrings.class, "localization/stslib/powers.json");
        BaseMod.loadCustomStringsFile(RelicStrings.class, "localization/stslib/relics.json");
    }

    @Override
    public void receiveEditCards()
    {
    	BaseMod.addDynamicVariable(new ExhaustiveVariable());
    	BaseMod.addDynamicVariable(new RefundVariable());
    }

    public static AbstractCard getMasterDeckEquivalent(AbstractCard playingCard)
    {
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.uuid.equals(playingCard.uuid)) {
                return c;
            }
        }
        return null;
    }
}

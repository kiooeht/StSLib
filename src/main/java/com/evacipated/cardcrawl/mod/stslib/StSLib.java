package com.evacipated.cardcrawl.mod.stslib;

import basemod.BaseMod;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.Keyword;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.rooms.MonsterRoom;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@SpireInitializer
public class StSLib implements
        PostInitializeSubscriber,
        EditKeywordsSubscriber,
        EditStringsSubscriber,
        EditCardsSubscriber,
        OnStartBattleSubscriber
{
    private static Map<AbstractCard, AbstractCard> playingToMasterDeckMap = new HashMap<>();

    public static Texture TEMP_HP_ICON;

    public static void initialize()
    {
        BaseMod.subscribe(new StSLib());
    }

    @Override
    public void receivePostInitialize()
    {
        TEMP_HP_ICON = ImageMaster.loadImage("images/ui/tempHP.png");
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
        BaseMod.loadCustomStrings(PowerStrings.class,
                Gdx.files.internal("localization/stslib/powers.json").readString(String.valueOf(StandardCharsets.UTF_8)));
    }

    @Override
    public void receiveEditCards()
    {
    }

    @Override
    public void receiveOnBattleStart(MonsterRoom monsterRoom)
    {
        playingToMasterDeckMap.clear();
    }

    public static void mapPlayingCardToMasterDeck(AbstractCard playingCard, AbstractCard masterCard)
    {
        playingToMasterDeckMap.put(playingCard, masterCard);
    }

    public static AbstractCard getMasterDeckEquivalent(AbstractCard playingCard)
    {
        return playingToMasterDeckMap.get(playingCard);
    }
}

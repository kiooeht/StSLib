package com.evacipated.cardcrawl.mod.stslib;

import basemod.BaseMod;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.evacipated.cardcrawl.mod.stslib.cards.interfaces.StartupCard;
import com.evacipated.cardcrawl.mod.stslib.patches.CommonKeywordIconsPatches;
import com.evacipated.cardcrawl.mod.stslib.variables.ExhaustiveVariable;
import com.evacipated.cardcrawl.mod.stslib.variables.RefundVariable;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.nio.charset.StandardCharsets;

@SpireInitializer
public class StSLib implements
        PostInitializeSubscriber,
        EditKeywordsSubscriber,
        EditStringsSubscriber,
        EditCardsSubscriber,
        OnStartBattleSubscriber
{
    public static Texture TEMP_HP_ICON;
    public static Texture BADGE_EXHAUST;
    public static Texture BADGE_ETHEREAL;
    public static Texture BADGE_INNATE;
    public static Texture BADGE_PURGE;
    public static Texture BADGE_RETAIN;

    public static void initialize()
    {
        BaseMod.subscribe(new StSLib());
    }

    @Override
    public void receivePostInitialize()
    {
        TEMP_HP_ICON = ImageMaster.loadImage("images/stslib/ui/tempHP.png");
        BADGE_EXHAUST = ImageMaster.loadImage("images/stslib/ui/keywordIcons/Exhaust.png");
        BADGE_ETHEREAL = ImageMaster.loadImage("images/stslib/ui/keywordIcons/Ethereal.png");
        BADGE_INNATE = ImageMaster.loadImage("images/stslib/ui/keywordIcons/Innate.png");
        BADGE_PURGE = ImageMaster.loadImage("images/stslib/ui/keywordIcons/Purge.png");
        BADGE_RETAIN = ImageMaster.loadImage("images/stslib/ui/keywordIcons/Retain.png");
    }

    private void loadLangKeywords(String language)
    {
        String path = "localization/stslib/" + language + "/";

        Gson gson = new Gson();
        String json = Gdx.files.internal(path + "keywords.json").readString(String.valueOf(StandardCharsets.UTF_8));
        Keyword[] keywords = gson.fromJson(json, Keyword[].class);

        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);

                if(keyword.NAMES.length > 0 && keyword.NAMES[0].equalsIgnoreCase("purge")) {
                    if(keyword.NAMES.length > 1) {
                        CommonKeywordIconsPatches.purgeName = keyword.NAMES[1];
                    } else {
                        CommonKeywordIconsPatches.purgeName = keyword.NAMES[0];
                    }
                }
            }
        }
    }

    @Override
    public void receiveEditKeywords()
    {
        String language = "eng";
        switch (Settings.language) {
            case RUS:
                language = "rus";
                break;
            case ZHS:
                language = "zhs";
                break;
            case ZHT:
                language = "zht";
                break;
            case JPN:
                language = "jpn";
                break;
        }

        loadLangKeywords("eng");
        loadLangKeywords(language);
    }

    private void loadLangStrings(String language)
    {
        String path = "localization/stslib/" + language + "/";

        BaseMod.loadCustomStringsFile(PowerStrings.class, path + "powers.json");
        BaseMod.loadCustomStringsFile(RelicStrings.class, path + "relics.json");
        try {
            BaseMod.loadCustomStringsFile(UIStrings.class, path + "ui.json");
        } catch (GdxRuntimeException ignored) {}
    }

    @Override
    public void receiveEditStrings()
    {
        String language = "eng";
        switch (Settings.language) {
            case RUS:
                language = "rus";
                break;
            case ZHS:
                language = "zhs";
                break;
            case ZHT:
                language = "zht";
                break;
            case JPN:
                language = "jpn";
                break;
        }

        loadLangStrings("eng");
        loadLangStrings(language);
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

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom)
    {
        CardGroup[] cardGroups = new CardGroup[] {
                AbstractDungeon.player.drawPile,
                AbstractDungeon.player.hand,
                AbstractDungeon.player.discardPile,
                AbstractDungeon.player.exhaustPile
        };

        for (CardGroup cardGroup : cardGroups) {
            for (AbstractCard c : cardGroup.group) {
                if (c instanceof StartupCard) {
                    if (((StartupCard) c).atBattleStartPreDraw()) {
                        AbstractDungeon.effectList.add(0, new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                    }
                }
            }
        }
    }
}

package com.evacipated.cardcrawl.mod.stslib;

import basemod.BaseMod;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.evacipated.cardcrawl.mod.stslib.cards.interfaces.StartupCard;
import com.evacipated.cardcrawl.mod.stslib.cards.targeting.SelfOrEnemyTargeting;
import com.evacipated.cardcrawl.mod.stslib.patches.CommonKeywordIconsPatches;
import com.evacipated.cardcrawl.mod.stslib.patches.CustomTargeting;
import com.evacipated.cardcrawl.mod.stslib.patches.bothInterfaces.OnCreateCardInterface;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableForRelic;
import com.evacipated.cardcrawl.mod.stslib.variables.ExhaustiveVariable;
import com.evacipated.cardcrawl.mod.stslib.variables.PersistVariable;
import com.evacipated.cardcrawl.mod.stslib.variables.RefundVariable;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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

        CustomTargeting.registerCustomTargeting(SelfOrEnemyTargeting.SELF_OR_ENEMY, new SelfOrEnemyTargeting());
    }

    private void loadLangKeywords(String language)
    {
        String path = "localization/stslib/" + language + "/";

        Gson gson = new Gson();
        Keyword[] keywords = null;
        // Register legacy keywords with no modid prefix
        try {
            String json = Gdx.files.internal(path + "legacyKeywords.json").readString(String.valueOf(StandardCharsets.UTF_8));
            keywords = gson.fromJson(json, Keyword[].class);
        } catch (GdxRuntimeException e) {
            // Ignore file not found
            if (!e.getMessage().startsWith("File not found:")) {
                throw e;
            }
        }

        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
                BaseMod.addKeyword("stslib", keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);

                if(keyword.NAMES.length > 0 && keyword.ID.equalsIgnoreCase("purge")) {
                    if(keyword.NAMES.length > 1) {
                        CommonKeywordIconsPatches.purgeName = keyword.NAMES[1];
                    } else {
                        CommonKeywordIconsPatches.purgeName = keyword.NAMES[0];
                    }
                }
            }
        }

        // Register keywords with modid prefix
        try {
            String json = Gdx.files.internal(path + "keywords.json").readString(String.valueOf(StandardCharsets.UTF_8));
            keywords = gson.fromJson(json, Keyword[].class);
        } catch (GdxRuntimeException e) {
            // Ignore file not found
            if (!e.getMessage().startsWith("File not found:")) {
                throw e;
            }
        }

        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword("stslib", keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
            }
        }
    }

    @Override
    public void receiveEditKeywords()
    {
        String language = Settings.language.name().toLowerCase();
        loadLangKeywords("eng");
        loadLangKeywords(language);
    }

    private void loadLangStrings(String language)
    {
        String path = "localization/stslib/" + language + "/";

        tryLoadStringsFile(PowerStrings.class, path + "powers.json");
        tryLoadStringsFile(RelicStrings.class, path + "relics.json");
        tryLoadStringsFile(UIStrings.class, path + "ui.json");
    }

    private void tryLoadStringsFile(Class<?> stringType, String filepath)
    {
        try {
            BaseMod.loadCustomStringsFile(stringType, filepath);
        } catch (GdxRuntimeException e) {
            // Ignore file not found
            if (!e.getMessage().startsWith("File not found:")) {
                throw e;
            }
        }
    }

    @Override
    public void receiveEditStrings()
    {
        String language = Settings.language.name().toLowerCase();
        loadLangStrings("eng");
        loadLangStrings(language);
    }

    @Override
    public void receiveEditCards()
    {
    	BaseMod.addDynamicVariable(new ExhaustiveVariable());
    	BaseMod.addDynamicVariable(new RefundVariable());
        BaseMod.addDynamicVariable(new PersistVariable());
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

    public static RewardItem generateCardReward(List<AbstractCard> rewardCards, boolean shiny) {
        //This is patched to call an added constructor of RewardItem.
        //rewardCards will be the cards in the reward, and shiny causes the reward to be gold colored (like a boss reward)
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

        int order = 0;
        ClickableForRelic.updateClickableList();
        ArrayList<ClickableForRelic> clickList = ClickableForRelic.getClickableList();
        for (ClickableForRelic clicky: clickList) {
            clicky.setY((ClickableForRelic.CE_Y + order * ClickableForRelic.Y_INCREMENT)*Settings.yScale);
            order++;
            if (clicky.firstBattle) {
                clicky.firstBattleFlash();
            }
        }
    }

    public static void onCreateCard(AbstractCard c) {
        AbstractDungeon.player.relics.stream().filter(r -> r instanceof OnCreateCardInterface).forEach(r -> ((OnCreateCardInterface) r).onCreateCard(c));
        AbstractDungeon.player.powers.stream().filter(r -> r instanceof OnCreateCardInterface).forEach(r -> ((OnCreateCardInterface) r).onCreateCard(c));
        AbstractDungeon.player.hand.group.stream().filter(card -> card instanceof OnCreateCardInterface).forEach(card -> ((OnCreateCardInterface) card).onCreateCard(c));
        AbstractDungeon.player.discardPile.group.stream().filter(card -> card instanceof OnCreateCardInterface).forEach(card -> ((OnCreateCardInterface) card).onCreateCard(c));
        AbstractDungeon.player.drawPile.group.stream().filter(card -> card instanceof OnCreateCardInterface).forEach(card -> ((OnCreateCardInterface) card).onCreateCard(c));
        AbstractDungeon.getMonsters().monsters.stream().filter(mon -> !mon.isDeadOrEscaped()).forEach(m -> m.powers.stream().filter(pow -> pow instanceof OnCreateCardInterface).forEach(pow -> ((OnCreateCardInterface) pow).onCreateCard(c)));
        if (c instanceof OnCreateCardInterface) {
            ((OnCreateCardInterface) c).onCreateThisCard();
        }
        // Postfix here for custom hooks, I guess?
    }
}

package com.evacipated.cardcrawl.mod.stslib;

import basemod.BaseMod;
import basemod.interfaces.EditKeywordsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;

@SpireInitializer
public class StSLib implements
        EditKeywordsSubscriber,
        EditStringsSubscriber
{
    public static void initialize()
    {
        BaseMod.subscribe(new StSLib());
    }

    @Override
    public void receiveEditKeywords()
    {

    }

    @Override
    public void receiveEditStrings()
    {

    }
}

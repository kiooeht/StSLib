package com.evacipated.cardcrawl.mod.stslib.patches.intents;

import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.abstracts.CustomIntent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.monsters.AbstractMonster;


@SpirePatch(
        clz = AbstractMonster.class,
        method = "getIntentImg"
)
public class AddCustomIntents {
    public static SpireReturn<Texture> Prefix(AbstractMonster __instance)
    {
        if(CustomIntent.intents.containsKey(__instance.intent)) {
            return SpireReturn.Return(CustomIntent.intents.get(__instance.intent).display);
        } else {
            return SpireReturn.Continue();
        }
    }
}
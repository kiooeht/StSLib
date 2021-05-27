package com.evacipated.cardcrawl.mod.stslib.patches.intents;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.mod.stslib.abstracts.CustomIntent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

@SpirePatch(
        clz = AbstractMonster.class,
        method = "updateIntentTip"
)
public class AddCustomTips {
    public static SpireReturn Prefix(AbstractMonster __instance)
    {
        if(CustomIntent.intents.containsKey(__instance.intent)) {
            CustomIntent ci = CustomIntent.intents.get(__instance.intent);
            PowerTip target = (PowerTip)ReflectionHacks.getPrivate(__instance, AbstractMonster.class, "intentTip");

            target.header = ci.header;
            target.body = ci.description(__instance);
            target.img = ci.tip;

            return SpireReturn.Return(null);
        } else {
            return SpireReturn.Continue();
        }
    }
}

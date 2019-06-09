package com.evacipated.cardcrawl.mod.stslib.patches.intents;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.abstracts.CustomIntent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.BobEffect;


@SpirePatch(
        clz = AbstractMonster.class,
        method = "renderDamageRange"
)
public class AddCustomIntentString {
    public static SpireReturn Prefix(AbstractMonster __instance, SpriteBatch sb)
    {
        if(CustomIntent.intents.containsKey(__instance.intent)) {
            CustomIntent ci = CustomIntent.intents.get(__instance.intent);
            String damageNumber = ci.damageNumber(__instance);
            if(damageNumber == null) {
                return SpireReturn.Continue();
            }

            BobEffect be = (BobEffect) ReflectionHacks.getPrivate(__instance, AbstractMonster.class, "bobEffect");

            int lineindex = 0;
            String[] msg = damageNumber.split("\\sNL\\s");

            do {
                float x = __instance.intentHb.cX - 30.0F * Settings.scale;
                if (msg[lineindex].length() > 5) {
                    x -= (msg[lineindex].length() - 5) * 6.0F * Settings.scale;
                }

                FontHelper.renderFontLeftTopAligned(sb, FontHelper.topPanelInfoFont, msg[lineindex], x,
                        __instance.intentHb.cY + be.y - (12.0F + 30 * lineindex) * Settings.scale,
                        (Color) ReflectionHacks.getPrivate(__instance, AbstractMonster.class, "intentColor"));
            } while(++lineindex < msg.length);
            return SpireReturn.Return(null);
        } else {
            return SpireReturn.Continue();
        }
    }
}
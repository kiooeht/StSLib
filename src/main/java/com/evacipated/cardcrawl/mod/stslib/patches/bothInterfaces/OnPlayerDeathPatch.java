package com.evacipated.cardcrawl.mod.stslib.patches.bothInterfaces;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnPlayerDeathPower;
import com.evacipated.cardcrawl.mod.stslib.relics.OnPlayerDeathRelic;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CtBehavior;

@SpirePatch(
        clz=AbstractPlayer.class,
        method="damage",
        paramtypez={DamageInfo.class}
)
public class OnPlayerDeathPatch
{
    @SpireInsertPatch(
            locator=Locator.class
    )
    public static SpireReturn Insert(AbstractPlayer __instance, DamageInfo info)
    {
        for (AbstractPower power : __instance.powers) {
            if (power instanceof OnPlayerDeathPower) {
                if (!((OnPlayerDeathPower) power).onPlayerDeath(__instance, info)) {
                    return SpireReturn.Return(null);
                }
            }
        }

        for (AbstractRelic relic : __instance.relics) {
            if (relic instanceof OnPlayerDeathRelic) {
                if (!((OnPlayerDeathRelic) relic).onPlayerDeath(__instance, info)) {
                    return SpireReturn.Return(null);
                }
            }
        }
        return SpireReturn.Continue();
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception
        {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "isDead");

            return LineFinder.findInOrder(ctBehavior, finalMatcher);
        }
    }
}

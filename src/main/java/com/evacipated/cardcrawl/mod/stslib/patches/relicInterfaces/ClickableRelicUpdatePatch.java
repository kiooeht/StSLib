package com.evacipated.cardcrawl.mod.stslib.patches.relicInterfaces;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.OverlayMenu;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CtBehavior;

@SpirePatch(
        clz=OverlayMenu.class,
        method="update"
)
public class ClickableRelicUpdatePatch
{
    @SpireInsertPatch(
            locator=Locator.class,
            localvars={"r"}
    )
    public static void Insert(OverlayMenu __instance, AbstractRelic relic)
    {
        if (relic instanceof ClickableRelic) {
            ((ClickableRelic) relic).clickUpdate();
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractRelic.class, "update");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}

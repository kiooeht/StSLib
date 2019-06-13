package com.evacipated.cardcrawl.mod.stslib.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import javassist.*;

@SuppressWarnings("unused")
@SpirePatch(clz = CardRewardScreen.class, method = SpirePatch.CONSTRUCTOR)
public class DiscoveryPatch
{
    public static void Raw(CtBehavior ctMethodToPatch) throws NotFoundException, CannotCompileException
    {
        CtClass ctClass = ctMethodToPatch.getDeclaringClass();
        CtMethod method = CtNewMethod.make(
            "public void customDiscovery(" + CardGroup.class.getName() + " cardGroup, boolean allowSkip) {" +
                "rItem = null;" +
                "codex = false;" +
                "discovery = true;" +
                "discoveryCard = null;" +
                "draft = false;" +
                "codexCard = null;" +
                "bowlButton.hide();" +
                "if (!allowSkip) { skipButton.hide(); } else { skipButton.show(); }" +
                "onCardSelect = true;" +
                    AbstractDungeon.class.getName() + ".topPanel.unhoverHitboxes();" +
                "rewardGroup = cardGroup.group;" +
                AbstractDungeon.class.getName() + ".isScreenUp = true;" +
                AbstractDungeon.class.getName() + ".screen = " + AbstractDungeon.CurrentScreen.class.getName() + ".CARD_REWARD;" +
                AbstractDungeon.class.getName() + ".dynamicBanner.appear(TEXT[1]);" +
                AbstractDungeon.class.getName() + ".overlayMenu.showBlackScreen();" +
                "placeCards((float)" + Settings.class.getName() + ".WIDTH / 2.0F, CARD_TARGET_Y);" +
                "}",
                ctClass);



        ctClass.addMethod(method);
    }
}

package com.evacipated.cardcrawl.mod.stslib.patches;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireRawPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.rewards.RewardItem;
import javassist.*;

import java.util.ArrayList;
import java.util.List;

public class CustomCardReward {
    @SpirePatch(
            clz = StSLib.class,
            method = "generateCardReward"

    )
    public static class UseCustomConstructor {
        @SpireRawPatch
        public static void raw(CtBehavior ctBehavior) throws NotFoundException, CannotCompileException {
            if (ctBehavior instanceof CtMethod) {
                CtMethod generateReward = (CtMethod) ctBehavior;

                CtClass rewardItemClass = ctBehavior.getDeclaringClass().getClassPool().get(RewardItem.class.getName());

                String settings = Settings.class.getName();

                CtConstructor customConstructor = CtNewConstructor.make(
                            "public RewardItem(" + List.class.getName() + " rewardCards, boolean shiny) {" +
                                "outlineImg = null;" +
                                "img = null;" +
                                "goldAmt = 0;" +
                                "bonusGold = 0;" +
                                "effects = new " + ArrayList.class.getName() + "();" +

                                "hb = new " + Hitbox.class.getName() + "(460.0F * " + settings + ".xScale, 90.0F * " + settings + ".yScale);" +
                                "flashTimer = 0.0F;" +
                                "isDone = false;" +
                                "ignoreReward = false;" +
                                "redText = false;" +
                                "reticleColor = new " + Color.class.getName() + "(1.0F, 1.0F, 1.0F, 0.0F);" +
                                "type = " + RewardItem.RewardType.class.getName() + ".CARD;" +
                                "isBoss = shiny;" +
                                "cards = new " + ArrayList.class.getName() + "(rewardCards);" +
                                "text = TEXT[2];" +
                            "}",
                        rewardItemClass
                );

                rewardItemClass.addConstructor(customConstructor);

                generateReward.setBody(
                        "return new " + RewardItem.class.getName() + "($1, $2);"
                );
            }
        }
    }
}

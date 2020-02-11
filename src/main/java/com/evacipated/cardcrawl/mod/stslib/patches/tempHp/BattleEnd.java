package com.evacipated.cardcrawl.mod.stslib.patches.tempHp;

import com.evacipated.cardcrawl.mod.stslib.patches.core.AbstractCreature.TempHPField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.lang.reflect.Field;

@SpirePatch(
        clz=AbstractRoom.class,
        method="endBattle"
)
public class BattleEnd
{
    public static void Prefix(AbstractRoom __instance)
    {
        TempHPField.tempHp.set(AbstractDungeon.player, 0);
    }
}

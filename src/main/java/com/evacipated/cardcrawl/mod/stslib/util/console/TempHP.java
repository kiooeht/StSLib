package com.evacipated.cardcrawl.mod.stslib.util.console;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import basemod.helpers.ConvertHelper;
import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class TempHP extends ConsoleCommand {

    public TempHP() {
        requiresPlayer = true;
        minExtraTokens = 0;
        maxExtraTokens = 2;
    }

    @Override
    protected void execute(String[] tokens, int depth) {
        String target = tokens.length > 1 ? tokens[1] : "player";
        String amt = tokens.length > 2 ? tokens[2] : "5";

        if (!target.equalsIgnoreCase("player") && !target.equalsIgnoreCase("enemies")) {
            DevConsole.log("Invalid target");
            return;
        }

        int amount = ConvertHelper.tryParseInt(amt, 5);
        if (target.equalsIgnoreCase("player")) {
            AbstractDungeon.actionManager.addToBottom(new AddTemporaryHPAction(AbstractDungeon.player, AbstractDungeon.player, amount));
        } else if (target.equalsIgnoreCase("enemies")) {
            for (AbstractMonster mon : AbstractDungeon.getMonsters().monsters) {
                if (mon != null && !mon.isDead && !mon.isDying && !mon.isEscaping) {
                    AbstractDungeon.actionManager.addToBottom(new AddTemporaryHPAction(mon, AbstractDungeon.player, amount));
                }
            }
        }
    }

    @Override
    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> options = new ArrayList<>();
        options.add("player");
        options.add("enemies");
        if (tokens.length == 1 || (tokens.length == 2 && !options.contains(tokens[1].trim()))) {
            return options;
        } else if (tokens.length == 2 || (tokens.length == 3 && tokens[2].trim().isEmpty())) {
            return smallNumbers();
        }
        complete = true;
        return new ArrayList<>();
    }
}

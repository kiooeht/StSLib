package com.evacipated.cardcrawl.mod.stslib.actions.defect;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

import java.util.*;

public class TriggerPassiveAction extends AbstractGameAction
{
	
	private AbstractOrb targorb;
	private int amount;
	
	
	public TriggerPassiveAction() {
		this(AbstractDungeon.player.orbs.get(0), 1);
	}
	
	public TriggerPassiveAction(AbstractOrb targorb) {
		this(targorb, 1);
	}
	
	public TriggerPassiveAction(int amount) {
		this(AbstractDungeon.player.orbs.get(0), amount);
	}
	
	public TriggerPassiveAction(int targindex, int amount) {
		this(AbstractDungeon.player.orbs.get(targindex), amount);
	}
	
    public TriggerPassiveAction(AbstractOrb targorb, int amount) {
        this.duration = Settings.ACTION_DUR_FAST;
		this.targorb = targorb;
		this.amount = amount;
		if (AbstractDungeon.player.hasRelic("Cables") && AbstractDungeon.player.orbs.get(0) == targorb) {
			this.amount++;
		}
    }
    
    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST && !AbstractDungeon.player.orbs.isEmpty()) {
			for (int i = 0; i < this.amount; i++) {
				this.targorb.onStartOfTurn();
				this.targorb.onEndOfTurn();
			}
        }
        this.tickDuration();
    }
}

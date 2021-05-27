package com.evacipated.cardcrawl.mod.stslib.abstracts;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class CustomIntent {
    public static Map<AbstractMonster.Intent, CustomIntent> intents = new HashMap<>();

    public AbstractMonster.Intent intent;
    public Texture display;
    public Texture tip;
    public String header;
    public String simpledescription;

    public CustomIntent(AbstractMonster.Intent intent, String header, String display, String tip) {
        this(intent, header, display, tip, null);
    }
    public CustomIntent(AbstractMonster.Intent intent, String header, Texture display, String tip) {
        this(intent, header, display, ImageMaster.loadImage(tip), null);
    }
    public CustomIntent(AbstractMonster.Intent intent, String header, String display, Texture tip) {
        this(intent, header, ImageMaster.loadImage(display), tip, null);
    }
    public CustomIntent(AbstractMonster.Intent intent, String header, Texture display, Texture tip) {
        this(intent, header, display, tip, null);
    }
    public CustomIntent(AbstractMonster.Intent intent, String header, String display, String tip, String simpledescription) {
        this(intent, header, ImageMaster.loadImage(display), ImageMaster.loadImage(tip), simpledescription);
    }
    public CustomIntent(AbstractMonster.Intent intent, String header, Texture display, String tip, String simpledescription) {
        this(intent, header, display, ImageMaster.loadImage(tip), simpledescription);
    }
    public CustomIntent(AbstractMonster.Intent intent, String header, String display, Texture tip, String simpledescription) {
        this(intent, header, ImageMaster.loadImage(display), tip, simpledescription);
    }
    public CustomIntent(AbstractMonster.Intent intent, String header, Texture display, Texture tip, String simpledescription) {
        this.header = header;
        this.display = display;
        this.tip = tip;
        this.simpledescription = simpledescription;
        this.intent = intent;
    }

    public String description (AbstractMonster am) {
        return simpledescription;
    }

    public String damageNumber(AbstractMonster am) {
        return null;
    }

    public void updateVFX(AbstractMonster am) {
        if (am.intentAlpha > 0.0F) {
            float intentParticleTimer = (float) ReflectionHacks.getPrivate(am, AbstractMonster.class, "intentParticleTimer");
            intentParticleTimer -= Gdx.graphics.getDeltaTime();
            if (intentParticleTimer <= 0.0F) {
                intentParticleTimer = this.updateVFXInInterval(am, (ArrayList<AbstractGameEffect>)ReflectionHacks.getPrivate(am, AbstractMonster.class, "intentVfx"));
            }
            ReflectionHacks.setPrivate(am, AbstractMonster.class, "intentParticleTimer", intentParticleTimer);
        }
    }
    public float updateVFXInInterval(AbstractMonster am, ArrayList<AbstractGameEffect> intentVfx) {
        return 1.0F;
    }

    public static void add(CustomIntent ci) {
        CustomIntent.intents.put(ci.intent, ci);
    }
}

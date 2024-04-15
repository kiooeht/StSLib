package com.evacipated.cardcrawl.mod.stslib.blockmods;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.ArrayList;

public class BlockInstance implements Comparable<BlockInstance> {
    //private static final String ID = ("stslib:BlockContainer");
    //private static final UIStrings strings = CardCrawlGame.languagePack.getUIString(ID);
    private final ArrayList<AbstractBlockModifier> containedBlockTypes;
    private int blockAmount;
    private final AbstractCreature owner;
    private Color blockColor;
    private Color textColor;
    private Texture blockImage = ImageMaster.BLOCK_ICON;

    public BlockInstance(AbstractCreature owner, int blockAmount, ArrayList<AbstractBlockModifier> blockTypes) {
        this.owner = owner;
        setBlockAmount(blockAmount);
        this.containedBlockTypes = blockTypes;
        for (AbstractBlockModifier m : containedBlockTypes) {
            m.setOwner(owner);
            m.setInstance(this);
            if (m.blockImageColor() != null) {
                blockColor = m.blockImageColor();
            }
            if (m.blockTextColor() != null) {
                textColor = m.blockTextColor();
            }
            if (m.customBlockImage() != null) {
                blockImage = m.customBlockImage();
            }
        }
    }

    public AbstractCreature getOwner() {
        return owner;
    }

    public int getBlockAmount() {
        return blockAmount;
    }

    public void setBlockAmount(int blockAmount) {
        this.blockAmount = blockAmount;
        if (this.blockAmount > 999) {
            this.blockAmount = 999;
        }
    }

    public ArrayList<AbstractBlockModifier> getBlockTypes() {
        return containedBlockTypes;
    }

    public boolean defaultBlock() {
        return containedBlockTypes.size() == 0;
    }

    public int computeStartTurnBlockLoss() {
        int ret = blockAmount;
        for (AbstractBlockModifier m : containedBlockTypes) {
            if (m.amountLostAtStartOfTurn() < ret) {
                ret = m.amountLostAtStartOfTurn();
            }
        }
        return ret;
    }

    public String makeName() {
        StringBuilder sb = new StringBuilder();
        sb.append(blockAmount).append(" ");
        if (defaultBlock()) {
            sb.append(TipHelper.capitalize(GameDictionary.BLOCK.NAMES[0]));
        } else if (containedBlockTypes.size() == 1) {
            sb.append(containedBlockTypes.get(0).getName());
        } else {
            //sb.append(strings.TEXT[0]);
            sb.append(TipHelper.capitalize(GameDictionary.BLOCK.NAMES[0]));
        }
        return sb.toString();
    }

    public String makeDescription() {
        StringBuilder sb = new StringBuilder();
        if (defaultBlock()) {
            sb.append(GameDictionary.BLOCK.DESCRIPTION);
        } else if (containedBlockTypes.size() == 1) {
            sb.append(containedBlockTypes.get(0).getDescription());
        } else {
            int i = 0;
            for (AbstractBlockModifier m : containedBlockTypes) {
                sb.append(m.getName()).append(" - ").append(m.getDescription());
                if (++i < containedBlockTypes.size()) {
                    sb.append(" NL ");
                }
            }
        }
        return sb.toString();
    }

    public int computePriority() {
        int ret = 0;
        for (AbstractBlockModifier m : containedBlockTypes) {
            if (m.priority() == AbstractBlockModifier.Priority.TOP) {
                ret--;
            } else if (m.priority() == AbstractBlockModifier.Priority.BOTTOM) {
                ret++;
            }
        }
        return ret;
    }

    public boolean containsSameBlockTypes(BlockInstance b) {
        ArrayList<Class<?>> comp = new ArrayList<>();
        for (AbstractBlockModifier m : containedBlockTypes) {
            comp.add(m.getClass());
        }
        for (AbstractBlockModifier m : b.containedBlockTypes) {
            if (!comp.remove(m.getClass())) {
                return false;
            }
        }
        return comp.isEmpty();
    }

    public boolean shouldStack() {
        boolean ret = true;
        for (AbstractBlockModifier m : containedBlockTypes) {
            ret &= m.shouldStack();
        }
        return ret;
    }

    public Texture getBlockImage() {
        return blockImage;
    }

    public Color getBlockColor() {
        return blockColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    @Override
    public int compareTo(BlockInstance o) {
        return this.computePriority() - o.computePriority();
    }
}

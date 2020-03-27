package com.evacipated.cardcrawl.mod.stslib.cards.interfaces;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.patches.cardInterfaces.BranchingUpgradesPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;

public interface BranchingUpgradesCard {

    enum UpgradeType {
        RANDOM_UPGRADE,
        NORMAL_UPGRADE,
        BRANCH_UPGRADE
    }

    default void doNormalUpgrade() {
        if (this instanceof AbstractCard) {
            setUpgradeType(UpgradeType.NORMAL_UPGRADE);
            ((AbstractCard) this).upgrade();
        }
    }

    default void doBranchUpgrade() {
        if (this instanceof AbstractCard) {
            setUpgradeType(UpgradeType.BRANCH_UPGRADE);
            ((AbstractCard) this).upgrade();
        }
    }

    default void doRandomUpgrade() {
        if (this instanceof AbstractCard) {
            setUpgradeType(UpgradeType.RANDOM_UPGRADE);
            ((AbstractCard) this).upgrade();
        }
    }

    default float chanceForBranchUpgrade() {
        return 0.5f;
    }

    // If branch upgrade path hasn't been decided yet, decides and upgrade type becomes concrete
    default boolean isBranchUpgrade() {
        UpgradeType upgradeType = getUpgradeType();
        if (upgradeType == UpgradeType.RANDOM_UPGRADE) {
            boolean ret = MathUtils.randomBoolean(chanceForBranchUpgrade());
            if (ret) {
                setUpgradeType(UpgradeType.BRANCH_UPGRADE);
            } else {
                setUpgradeType(UpgradeType.NORMAL_UPGRADE);
            }
            return ret;
        }
        return upgradeType == UpgradeType.BRANCH_UPGRADE;
    }

    default UpgradeType getUpgradeType() {
        if (this instanceof AbstractCard) {
            return BranchingUpgradesPatch.BranchingUpgradeField.upgradeType.get(this);
        }
        return UpgradeType.NORMAL_UPGRADE;
    }

    default void setUpgradeType(UpgradeType upgradeType) {
        if (this instanceof AbstractCard) {
            BranchingUpgradesPatch.BranchingUpgradeField.upgradeType.set(this, upgradeType);
        }
    }
}

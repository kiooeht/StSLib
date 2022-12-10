package com.evacipated.cardcrawl.mod.stslib.util;

import java.util.ArrayList;

public class UpgradeData {
    public boolean applied = false;
    public boolean strict;
    public int index;
    public UpgradeRunnable upgradeRunnable;
    public ArrayList<Integer> dependencies = new ArrayList<>(), exclusions = new ArrayList<>();

    public UpgradeData(UpgradeRunnable runnable, int index) {
        this(runnable, index, true, new int[]{}, new int[]{});
    }

    public UpgradeData(UpgradeRunnable runnable, int index, int[] dependencies) {
        this(runnable, index, true, dependencies, new int[]{});
    }

    public UpgradeData(UpgradeRunnable runnable, int index, boolean strict, int[] dependencies) {
        this(runnable, index, strict, dependencies, new int[]{});
    }

    public UpgradeData(UpgradeRunnable runnable, int index, boolean strict, int[] dependencies, int[] exclusions) {
        this.upgradeRunnable = runnable;
        this.index = index;
        this.strict = strict;
        for (int i : dependencies) {
            this.dependencies.add(i);
        }
        for (int i : exclusions) {
            this.exclusions.add(i);
        }
    }

    public boolean canUpgrade(ArrayList<UpgradeData> upgrades) {
        if (applied) {
            return false;
        }

        boolean dependencyCheck = false;

        for (int i : dependencies) {

            if (strict && (upgrades.size() <= i || !upgrades.get(i).applied))
                return false;

            if (upgrades.get(i).applied)
                dependencyCheck = true;
        }
        if (dependencies.size() == 0)
            dependencyCheck = true;

        for (int i : exclusions) {
            if (upgrades.get(i).applied)
                return false;
        }

        return dependencyCheck;
    }

    public void upgrade() {
        upgradeRunnable.doUpgrade();
        applied = true;
    }
}

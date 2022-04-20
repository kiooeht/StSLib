package com.evacipated.cardcrawl.mod.stslib.patches;

import basemod.Pair;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.actions.common.MultiGroupMoveAction;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import javassist.CtBehavior;

import java.util.ArrayList;

public class MultiGroupGridSelectPatches {
    private static final float TITLE_SPACING = 90 * Settings.scale;
    private static final float TITLE_X = 300.0f * Settings.scale;
    private static final float TITLE_Y = AbstractCard.IMG_HEIGHT * 0.75f / 2 + TITLE_SPACING / 2 + 20 * Settings.scale;

    @SpirePatch(
            clz = CardGroup.class,
            method = SpirePatch.CLASS
    )
    public static class Fields {
        public static SpireField<ArrayList<Pair<CardGroup.CardGroupType, Integer>>> groupIndexes = new SpireField<>(() -> null);
    }

    @SpirePatch(
            clz = GridCardSelectScreen.class,
            method = "updateCardPositionsAndHoverLogic"
    )
    public static class AlternatePositions {
        @SpirePrefixPatch
        public static SpireReturn<?> altPos(GridCardSelectScreen __instance, float ___drawStartX, float ___drawStartY, float ___currentDiffY, float ___padX, float ___padY, @ByRef AbstractCard[] ___hoveredCard) {
            ArrayList<Pair<CardGroup.CardGroupType, Integer>> groupIndexes = Fields.groupIndexes.get(__instance.targetGroup);
            if (groupIndexes != null) {
                if (DisplayGroupTitles.titles.size() != groupIndexes.size()) {
                    DisplayGroupTitles.titles.clear();
                }

                int lineNum = 0, group = 0;
                ArrayList<AbstractCard> cards = __instance.targetGroup.group;

                int cardIndex = 0;
                float groupOffset = 0;

                for (Pair<CardGroup.CardGroupType, Integer> groupIndex : groupIndexes) {
                    groupOffset += TITLE_SPACING;

                    int firstGroupCardIndex = cardIndex;

                    for (int i = 0; cardIndex < groupIndex.getValue(); ++cardIndex) {
                        int mod = i % 5;
                        if (mod == 0 && i != 0) {
                            ++lineNum;
                        }

                        cards.get(cardIndex).target_x = ___drawStartX + (float)mod * ___padX;
                        cards.get(cardIndex).target_y = ___drawStartY + ___currentDiffY - (float)lineNum * ___padY - groupOffset;
                        cards.get(cardIndex).fadingOut = false;
                        if (groupIndex.getKey() != CardGroup.CardGroupType.HAND)
                            cards.get(cardIndex).update();
                        cards.get(cardIndex).updateHoverLogic();

                        ___hoveredCard[0] = null;

                        for (AbstractCard c : cards) {
                            if (c.hb.hovered) {
                                ___hoveredCard[0] = c;
                                break;
                            }
                        }

                        ++i;
                    }

                    while (DisplayGroupTitles.titles.size() <= group)
                        DisplayGroupTitles.titles.add(new GroupTitle());
                    DisplayGroupTitles.titles.get(group).set(groupIndex.getKey(), TITLE_X, cards.get(firstGroupCardIndex).current_y + TITLE_Y);

                    ++lineNum;
                    ++group;
                }

                return SpireReturn.Return();
            }
            else {
                DisplayGroupTitles.titles.clear();
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = GridCardSelectScreen.class,
            method = "render"
    )
    public static class DisplayGroupTitles {
        private static final ArrayList<GroupTitle> titles = new ArrayList<>();
        private static final Color render = Color.WHITE.cpy();

        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void renderTitles(GridCardSelectScreen __instance, SpriteBatch sb) {
            for (GroupTitle title : titles) {
                FontHelper.renderFontLeft(sb, FontHelper.losePowerFont, title.title, title.x, title.y, render);
            }

            titles.clear();
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(GridCardSelectScreen.class, "hoveredCard");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }

    private static class GroupTitle {
        public String title = "";
        public float x = 0, y = 0;

        public void set(CardGroup.CardGroupType type, float x, float y){
            switch (type) {
                case HAND:
                    title = MultiGroupMoveAction.HAND;
                    break;
                case DRAW_PILE:
                    title = MultiGroupMoveAction.DRAW_PILE;
                    break;
                case DISCARD_PILE:
                    title = MultiGroupMoveAction.DISCARD_PILE;
                    break;
                case EXHAUST_PILE:
                    title = MultiGroupMoveAction.EXHAUST_PILE;
                    break;
                default:
                    title = MultiGroupMoveAction.UNKNOWN;
                    break;
            }

            this.x = x;
            this.y = y;
        }
    }


    @SpirePatch(
            clz = GridCardSelectScreen.class,
            method = "calculateScrollBounds"
    )
    public static class AltScrollBounds {
        @SpirePrefixPatch
        public static SpireReturn<?> altPos(GridCardSelectScreen __instance, float ___padY, @ByRef float[] ___scrollUpperBound) {
            ArrayList<Pair<CardGroup.CardGroupType, Integer>> groupIndexes = Fields.groupIndexes.get(__instance.targetGroup);
            if (groupIndexes != null) {
                int lineNum = -2;

                int startIndex = 0;
                for (Pair<CardGroup.CardGroupType, Integer> groupIndex : groupIndexes) {
                    int size = groupIndex.getValue() - startIndex;

                    startIndex = groupIndex.getValue();

                    lineNum += size / 5;
                    if (size % 5 != 0) {
                        ++lineNum;
                    }
                }

                float bonusScroll = (float)lineNum * ___padY + groupIndexes.size() * TITLE_SPACING;
                ___scrollUpperBound[0] = Settings.DEFAULT_SCROLL_LIMIT + Math.max(0, bonusScroll);

                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }
}

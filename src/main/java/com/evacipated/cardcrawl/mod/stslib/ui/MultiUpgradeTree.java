package com.evacipated.cardcrawl.mod.stslib.ui;

import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.mod.stslib.cards.interfaces.MultiUpgradeCard;
import com.evacipated.cardcrawl.mod.stslib.patches.cardInterfaces.MultiUpgradePatches;
import com.evacipated.cardcrawl.mod.stslib.util.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.ShaderHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.ArrayList;

public class MultiUpgradeTree {
    public static AbstractCard mainCard;
    public static boolean manualMainCard;
    public static ArrayList<AbstractCard> cardList = new ArrayList<>();
    public static ArrayList<AbstractCard> takenList = new ArrayList<>();
    public static ArrayList<AbstractCard> lockedList = new ArrayList<>();
    public static CardGraph cardGraph = new CardGraph();
    public static float grabX, grabY;
    public static float deltaX, deltaY;
    public static float minX, minY;
    public static float maxX, maxY;
    public static boolean allowX, allowY;
    public static boolean dragging;
    public static float renderScale;
    private static final float DEFAULT_ZOOM = 1.0f;
    private static final float MIN_ZOOM = 0.3f;
    private static final float MAX_ZOOM = 1.1f;
    private static final float X_PAD = 400F * Settings.scale;
    private static final float Y_PAD = 220F * Settings.scale;
    private static final float LINE_SPACING = 20F * Settings.scale;
    public static Texture upgradeAndLine = ImageMaster.loadImage("images/stslib/ui/andLine.png");
    public static Texture exclusionLine = ImageMaster.loadImage("images/stslib/ui/andLine.png");

    public static void open(AbstractCard c) throws Exception {
        open(c, false);
    }

    public static void open(AbstractCard c, boolean manualMainCard) throws Exception {
        if (manualMainCard) {
            mainCard = c.makeStatEquivalentCopy();
        } else {
            mainCard = c;
        }
        MultiUpgradeTree.manualMainCard = manualMainCard;
        resetScrollState();
        prepTree(mainCard);
    }

    public static void render(SpriteBatch sb) {
        renderArrows(sb);
        renderCards(sb);
    }

    public static void update() {
        updateScrolling();
        updateCards();
    }

    public static void selectCard(AbstractCard card) {
        if (card != mainCard && cardList.contains(card) && !takenList.contains(card) && !lockedList.contains(card)) {
            MultiUpgradePatches.MultiUpgradeFields.glowRed.set(card, false);
            card.beginGlowing();
            cardList.forEach((c) -> {
                if (c != card) {
                    MultiUpgradePatches.MultiUpgradeFields.glowRed.set(c, false);
                    c.stopGlowing();
                }

            });
            CardVertex v = cardGraph.getVertexByCard(card);
            if (v != null) {
                for (CardVertex exclude : v.exclusions) {
                    markExclusions(exclude);
                }

            }

            MultiUpgradePatches.MultiSelectFields.chosenIndex.set(AbstractDungeon.gridSelectScreen, MultiUpgradePatches.MultiUpgradeFields.upgradeIndex.get(card));
            MultiUpgradePatches.MultiSelectFields.waitingForUpgradeSelection.set(AbstractDungeon.gridSelectScreen, false);
        }
    }

    private static void markExclusions(CardVertex v) {
        MultiUpgradePatches.MultiUpgradeFields.glowRed.set(v.card, true);
        v.card.beginGlowing();
        v.children.forEach(c -> {
            if (c.strict) {
                markExclusions(c);
            }
        });
    }

    private static void prepTree(AbstractCard c) throws Exception {
        resetScrollState();
        for (UpgradeData u : ((MultiUpgradeCard) c).getUpgrades()) {
            for (int i : u.dependencies) {
                if (i > u.index) {
                    throw new Exception("Illegal forward dependency: Upgrade Index "+u.index+" requires Upgrade Index "+i);
                }
                if (i == u.index) {
                    throw new Exception("Illegal self dependency: Upgrade Index "+u.index+" requires itself");
                }
            }
        }
        //Prep the cards into the array
        cardList.clear();
        takenList.clear();
        lockedList.clear();
        cardGraph.clear();
        if (manualMainCard) {
            cardList.add(c);
        }
        CardVertex root = new CardVertex(c, -1);
        root.move(-1, 0);
        cardGraph.addVertex(root);
        for (UpgradeData u : ((MultiUpgradeCard) c).getUpgrades()) {
            AbstractCard copy;
            if (u.applied) {
                copy = makeSimpleCopy(c);
            } else {
                copy = c.makeStatEquivalentCopy();
            }
            prepUpgradePreview(copy, u);

            MultiUpgradePatches.MultiUpgradeFields.upgradeIndex.set(copy, u.index); //Gets set back to -1 when completed, so we need to set it again

            cardList.add(copy);

            if (u.applied) {
                takenList.add(copy);
            } else if (!u.canUpgrade(((MultiUpgradeCard) c).getUpgrades())) {
                lockedList.add(copy);
            }
            CardVertex v = new CardVertex(copy, u.index, u.strict);
            cardGraph.addVertex(v);
            if (u.dependencies.isEmpty()) {
                cardGraph.addDependence(v, root);
            } else {
                for (int i : u.dependencies) {
                    //Dependency directed graphs
                    cardGraph.addDependence(v, cardGraph.vertices.get(i + 1));
                }
            }

        }
        for (UpgradeData u : ((MultiUpgradeCard) c).getUpgrades()) {
            if (u.exclusions.size() > 0) {
                for (int i : u.exclusions) {
                    cardGraph.addExclusion(cardGraph.vertices.get(u.index+1), cardGraph.vertices.get(i+1));
                }
            }
        }

        c.current_x = c.target_x = (float)Settings.WIDTH * 1/3F;
        c.current_y = c.target_y = (float)Settings.HEIGHT / 2F;

        //Balance all cards in the X direction
        for (CardVertex v : cardGraph.vertices) {
            for (CardVertex dependency : v.parents) {
                if (dependency.x >= v.x) {
                    v.move(dependency.x + 1, v.y);
                }
                v.card.current_x = v.card.target_x = Settings.WIDTH * 2/3F + (v.x * X_PAD);
            }
        }

        for (int i = 0 ; i <= cardGraph.depth() ; i++) {
            int finalI = i;
            final int[] yIndex = {(int) cardGraph.vertices.stream().filter(v -> v.x == finalI).count() - 1};
            cardGraph.vertices.stream().filter(v -> v.x == finalI).forEach(v -> {
                v.move(v.x, yIndex[0]);
                v.card.current_y = v.card.target_y = Settings.HEIGHT / 2F + (v.y * Y_PAD);
                if (manualMainCard) {
                    v.card.update();
                }
                yIndex[0] -= 2;
            });
        }

        //Define the scroll limits
        float left, right, up, down;
        left = right = c.current_x;
        up = down = c.current_y;
        for (AbstractCard card : cardList) {
            if (card.current_x < left) {
                left = card.current_x;
            } else if (card.current_x > right) {
                right = card.current_x;
            }
            if (card.current_y < down) {
                down = card.current_y;
            } else if (card.current_y > up) {
                up = card.current_y;
            }
        }

        //Add some padding
        left -= 200F;
        right += 200F;
        up += 260F;
        down -= 260F;

        if (left < 0) {
            maxX = -left + 200F * Settings.scale;
            allowX = true;
        }
        if (right > Settings.WIDTH) {
            minX = Settings.WIDTH - right - 200F * Settings.scale;
            allowX = true;
        }
        if (down < 0) {
            maxY = -down + 260F * Settings.scale;
            allowY = true;
        }
        if (up > Settings.HEIGHT) {
            minY = Settings.HEIGHT - up - 260F * Settings.scale;
            allowY = true;
        }

        if (cardGraph.height() > 2) {
            renderScale = 0.8F;
        }
    }

    private static void renderCards(SpriteBatch sb) {
        AbstractCard c = mainCard;
        float dx = deltaX;
        float dy = deltaY;

        c.target_x = (float)Settings.WIDTH * 1/3F + dx;
        c.target_y = (float)Settings.HEIGHT / 2F + dy;
        c.drawScale = renderScale;
        c.render(sb);

        for (CardVertex v : cardGraph.vertices) {
            if (v.x != -1) {
                if (v.card.hb.hovered) {
                    v.card.drawScale = renderScale;
                } else {
                    v.card.drawScale = 0.9F * renderScale;
                }
                v.card.target_x = c.target_x + (Settings.WIDTH / 3F * renderScale) + (v.x * X_PAD * renderScale);
                v.card.target_y = c.target_y + (v.y * Y_PAD * renderScale);

                if (lockedList.contains(v.card) || MultiUpgradePatches.MultiUpgradeFields.glowRed.get(v.card)) {
                    sb.end();
                    sb.setShader(Grayscale.program);
                    sb.begin();
                } else if (takenList.contains(v.card)) {
                    sb.end();
                    sb.setShader(Greenify.program);
                    sb.begin();
                }
                v.card.render(sb);
                ShaderHelper.setShader(sb, ShaderHelper.Shader.DEFAULT);
                v.card.renderCardTip(sb);
            }
        }
    }

    private static void renderArrows(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        AbstractCard hovered = null;
        AbstractCard selected = null;
        for (CardVertex v : cardGraph.vertices) {
            if (v.card.hb.hovered) {
                hovered = v.card;
            }
            if (AbstractDungeon.gridSelectScreen != null && v.index != -1 && v.index == MultiUpgradePatches.MultiSelectFields.chosenIndex.get(AbstractDungeon.gridSelectScreen)) {
                selected = v.card;
            }
        }
        if (selected != null && hovered == null) {
            hovered = selected;
        }
        for (CardVertex v : cardGraph.vertices) {
            for (CardVertex child : v.children) {
                if (hovered != null) {
                    if (child.card == hovered) {
                        if (v.index == -1 || takenList.contains(v.card)) {
                            sb.setColor(Color.GREEN);
                        } else if (!lockedList.contains(v.card)) {
                            sb.setColor(Color.ORANGE);
                        } else {
                            sb.setColor(Color.RED);
                        }
                    } else {
                        sb.setColor(Settings.QUARTER_TRANSPARENT_WHITE_COLOR);
                    }
                }

                Vector2 vec2 = (new Vector2((child.card.current_x),child.card.current_y)).sub(new Vector2((v.card.current_x), v.card.current_y));
                float length = vec2.len();
                for (float i = 0; i < length; i += LINE_SPACING) {
                    vec2.clamp(length - i, length - i);
                    Texture texture = child.strict ? upgradeAndLine : ImageMaster.MAP_DOT_1;
                    float width = texture.getWidth();
                    sb.draw(texture, (v.card.current_x) + vec2.x - width / 2, v.card.current_y + vec2.y - width / 2, width / 2, width / 2, width, width, Settings.scale, Settings.scale, (new Vector2((v.card.current_x) - (child.card.current_x), v.card.current_y - child.card.current_y)).nor().angle() + 90.0F, 0, 0, (int) width, (int) width, false, false);
                }
                sb.setColor(Color.WHITE);
            }

            sb.setColor(Color.RED);
            for (CardVertex exclusion : v.exclusions) {
                if (exclusion.index > v.index) {
                    continue;
                }
                Vector2 vec2 = (new Vector2((exclusion.card.current_x), exclusion.card.current_y)).sub(new Vector2((v.card.current_x), v.card.current_y));
                float length = vec2.len();
                int mod = 0;
                for (float i = 0; i < length; i += LINE_SPACING) {
                    vec2.clamp(length - i, length - i);
                    Texture texture = exclusionLine;
                    float width = texture.getWidth();
                    sb.draw(exclusionLine, (v.card.current_x) + vec2.x - width / 2, v.card.current_y + vec2.y - width / 2, width / 2, width / 2, width, width, Settings.scale, Settings.scale, (new Vector2((v.card.current_x) - (exclusion.card.current_x), v.card.current_y - exclusion.card.current_y)).nor().angle() + 90.0F, 0, 0, (int) width, (int) width, false, false);
                }
            }
            sb.setColor(Color.WHITE);
        }
    }

    private static AbstractCard makeSimpleCopy(AbstractCard c) {
        AbstractCard copy = c.makeCopy();
        CardModifierManager.copyModifiers(c, copy, false, false, false);
        return copy;
    }

    private static void prepUpgradePreview(AbstractCard card, UpgradeData u) {
        doUpgrade(card, u);
        card.displayUpgrades();
    }

    private static void doUpgrade(AbstractCard card, UpgradeData u) {
        if (u.strict) {
            for (int i : u.dependencies) {
                UpgradeData dep = ((MultiUpgradeCard)card).getUpgrades().get(i);
                if (!dep.applied) {
                    doUpgrade(card, dep);
                }
            }
        }
        MultiUpgradePatches.MultiUpgradeFields.upgradeIndex.set(card, u.index);
        card.upgrade();
    }

    private static void updateScrolling() {
        int x = InputHelper.mX;
        int y = InputHelper.mY;
        if (!dragging) {
            if (InputHelper.justClickedLeft) {
                dragging = true;
                grabX = x - deltaX;
                grabY = y - deltaY;
            }
        } else if (InputHelper.isMouseDown) {
            if (allowX) {
                deltaX = x - grabX;
            }
            if (allowY) {
                deltaY = y - grabY;
            }

        } else {
            dragging = false;
        }

        if (deltaX < minX) {
            deltaX = MathHelper.scrollSnapLerpSpeed(deltaX, minX);
        } else if (deltaX > maxX) {
            deltaX = MathHelper.scrollSnapLerpSpeed(deltaX, maxX);
        }
        if (deltaY < minY) {
            deltaY = MathHelper.scrollSnapLerpSpeed(deltaY, minY);
        } else if (deltaY > maxY) {
            deltaY = MathHelper.scrollSnapLerpSpeed(deltaY, maxY);
        }
        if (InputHelper.scrolledDown && renderScale > MIN_ZOOM) {
            renderScale -= 0.1f;
        } else if (InputHelper.scrolledUp && renderScale < MAX_ZOOM) {
            renderScale += 0.1f;
        }
    }

    private static void updateCards() {
        for (AbstractCard c : cardList) {
            if (c != null) {
                c.update();
                c.updateHoverLogic();
            }
        }
    }

    private static void resetScrollState() {
        deltaX = 0;
        deltaY = 0;
        minX = 0;
        maxX = 0;
        minY = 0;
        maxY = 0;
        allowX = false;
        allowY = false;
        renderScale = DEFAULT_ZOOM;
    }
}

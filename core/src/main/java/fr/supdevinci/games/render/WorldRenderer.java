package fr.supdevinci.games.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import fr.supdevinci.games.assets.GameAssets;
import fr.supdevinci.games.config.ColorPalette;
import fr.supdevinci.games.world.GameWorld;
import fr.supdevinci.games.world.KeyPickup;
import fr.supdevinci.games.world.LevelDefinition;
import fr.supdevinci.games.world.LevelId;
import fr.supdevinci.games.world.Obstacle;
import fr.supdevinci.games.world.TransitionZone;

/**
 * Renders the world using themed placeholders so the prototype stays readable without final textures.
 */
public final class WorldRenderer {
    private final ShapeRenderer shapeRenderer;

    public WorldRenderer(GameAssets assets) {
        this.shapeRenderer = assets.getShapeRenderer();
    }

    /**
     * Draws the current map, its blocking areas, transitions and the player.
     *
     * @param gameWorld current world state
     * @param camera world camera
     */
    public void render(GameWorld gameWorld, OrthographicCamera camera) {
        LevelDefinition level = gameWorld.getCurrentLevel();
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawThemedBackground(level);
        drawTransitions(level);
        drawObstacles(level);
        drawSpecialDoors(level, gameWorld);
        drawKeyPickups(level, gameWorld);

        // Draw player
        shapeRenderer.setColor(Color.valueOf("FFD166"));
        shapeRenderer.rect(
            gameWorld.getPlayer().getX(),
            gameWorld.getPlayer().getY(),
            gameWorld.getPlayer().getWidth(),
            gameWorld.getPlayer().getHeight()
        );
        shapeRenderer.end();

        // Draw world bounds
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(level.getAccentColor());
        shapeRenderer.rect(0f, 0f, level.getWidth(), level.getHeight());
        shapeRenderer.end();
    }

    private void drawSpecialDoors(LevelDefinition level, GameWorld gameWorld) {
        if (level.getId() != LevelId.HOUSE) {
            return;
        }

        // Porte cave visuelle : prend la même hitbox que la transition HOUSE -> CELLAR
        for (TransitionZone transitionZone : level.getTransitionZones()) {
            if (transitionZone.getTargetLevelId() != LevelId.CELLAR) {
                continue;
            }

            boolean unlocked = transitionZone.getRequiredKeys().isEmpty()
                || gameWorld.getInventory().hasAllKeys(transitionZone.getRequiredKeys());

            Color doorBody = unlocked ? ColorPalette.DOOR_OPEN : ColorPalette.DOOR_LOCKED;
            Color doorFrame = ColorPalette.DOOR_FRAME;

            // Cadre
            shapeRenderer.setColor(doorFrame);
            shapeRenderer.rect(
                transitionZone.getArea().x - 3f,
                transitionZone.getArea().y - 3f,
                transitionZone.getArea().width + 6f,
                transitionZone.getArea().height + 6f
            );

            // Porte
            shapeRenderer.setColor(doorBody);
            shapeRenderer.rect(
                transitionZone.getArea().x,
                transitionZone.getArea().y,
                transitionZone.getArea().width,
                transitionZone.getArea().height
            );

            break;
        }
    }

    private void drawThemedBackground(LevelDefinition level) {
        LevelId levelId = level.getId();

        if (levelId == LevelId.HUB) {
            // Route centrale + trottoirs
            shapeRenderer.setColor(ColorPalette.HUB_SIDEWALK);
            shapeRenderer.rect(0f, 200f, level.getWidth(), 140f);
            shapeRenderer.rect(360f, 0f, 240f, level.getHeight());

            shapeRenderer.setColor(ColorPalette.HUB_ROAD);
            shapeRenderer.rect(0f, 230f, level.getWidth(), 80f);
            shapeRenderer.rect(390f, 0f, 180f, level.getHeight());

            // Bâtiments placeholder (non-collision visuelle)
            shapeRenderer.setColor(ColorPalette.BUILDING_HOUSE);
            shapeRenderer.rect(380f, 20f, 200f, 100f);
            shapeRenderer.setColor(ColorPalette.BUILDING_LIBRARY);
            shapeRenderer.rect(20f, 180f, 180f, 180f);
            shapeRenderer.setColor(ColorPalette.BUILDING_CEMETERY);
            shapeRenderer.rect(760f, 180f, 180f, 180f);
            shapeRenderer.setColor(ColorPalette.BUILDING_PORT);
            shapeRenderer.rect(350f, 390f, 260f, 130f);
            return;
        }

        if (levelId == LevelId.HOUSE) {
            shapeRenderer.setColor(Color.valueOf("6E4B37"));
            shapeRenderer.rect(0f, 0f, level.getWidth(), level.getHeight());
            shapeRenderer.setColor(Color.valueOf("8A6248"));
            shapeRenderer.rect(90f, 70f, 780f, 430f);
            return;
        }

        if (levelId == LevelId.LIBRARY) {
            shapeRenderer.setColor(Color.valueOf("2E241C"));
            shapeRenderer.rect(0f, 0f, level.getWidth(), level.getHeight());
            shapeRenderer.setColor(Color.valueOf("3C2F24"));
            shapeRenderer.rect(120f, 80f, 760f, 360f);
            return;
        }

        if (levelId == LevelId.PORT) {
            shapeRenderer.setColor(Color.valueOf("5F4A3B"));
            shapeRenderer.rect(0f, 0f, level.getWidth(), 360f);
            shapeRenderer.setColor(Color.valueOf("233744"));
            shapeRenderer.rect(0f, 360f, level.getWidth(), 180f);
            shapeRenderer.setColor(Color.valueOf("8B6A45"));
            shapeRenderer.rect(420f, 160f, 120f, 190f);
            return;
        }

        if (levelId == LevelId.CEMETERY) {
            shapeRenderer.setColor(Color.valueOf("2D3430"));
            shapeRenderer.rect(0f, 0f, level.getWidth(), level.getHeight());
            shapeRenderer.setColor(Color.valueOf("3A433E"));
            shapeRenderer.rect(120f, 90f, 760f, 300f);
            return;
        }

        // Cellar / default
        shapeRenderer.setColor(level.getBackgroundColor());
        shapeRenderer.rect(0f, 0f, level.getWidth(), level.getHeight());
    }

    private void drawTransitions(LevelDefinition level) {
        Color accent = level.getAccentColor();
        shapeRenderer.setColor(accent.r, accent.g, accent.b, 0.35f);
        for (TransitionZone transitionZone : level.getTransitionZones()) {
            shapeRenderer.rect(
                transitionZone.getArea().x,
                transitionZone.getArea().y,
                transitionZone.getArea().width,
                transitionZone.getArea().height
            );
        }
    }

    private void drawObstacles(LevelDefinition level) {
        if (level.getId() == LevelId.HUB) {
            drawHubObstacles(level);
            return;
        }

        shapeRenderer.setColor(getObstacleColor(level.getId()));
        for (Obstacle obstacle : level.getObstacles()) {
            shapeRenderer.rect(
                obstacle.getBounds().x,
                obstacle.getBounds().y,
                obstacle.getBounds().width,
                obstacle.getBounds().height
            );
        }
    }

    private void drawHubObstacles(LevelDefinition level) {
        int index = 0;
        for (Obstacle obstacle : level.getObstacles()) {
            Color color;
            if (index == 0) {
                color = ColorPalette.BUILDING_HOUSE;
            } else if (index == 1) {
                color = ColorPalette.BUILDING_LIBRARY;
            } else if (index == 2) {
                color = ColorPalette.BUILDING_CEMETERY;
            } else if (index == 3) {
                color = ColorPalette.BUILDING_PORT;
            } else {
                color = getObstacleColor(LevelId.HUB);
            }

            shapeRenderer.setColor(color);
            shapeRenderer.rect(
                obstacle.getBounds().x,
                obstacle.getBounds().y,
                obstacle.getBounds().width,
                obstacle.getBounds().height
            );
            index++;
        }
    }

    private Color getObstacleColor(LevelId levelId) {
        if (levelId == LevelId.HUB) {
            return new Color(0f, 0f, 0f, 0.55f);
        }
        if (levelId == LevelId.LIBRARY) {
            return Color.valueOf("6A4A2A"); // rayons/comptoir
        }
        if (levelId == LevelId.PORT) {
            return Color.valueOf("4D3A2D"); // bois et cabanes
        }
        if (levelId == LevelId.CEMETERY) {
            return Color.valueOf("757575"); // pierre
        }
        if (levelId == LevelId.HOUSE) {
            return Color.valueOf("2B1E17"); // murs intérieurs
        }
        return new Color(0f, 0f, 0f, 0.4f);
    }

    private void drawKeyPickups(LevelDefinition level, GameWorld gameWorld) {
        shapeRenderer.setColor(Color.valueOf("F4D35E"));
        for (KeyPickup keyPickup : level.getKeyPickups()) {
            if (!gameWorld.isPickupCollected(keyPickup)) {
                shapeRenderer.rect(
                    keyPickup.getArea().x,
                    keyPickup.getArea().y,
                    keyPickup.getArea().width,
                    keyPickup.getArea().height
                );
            }
        }
    }
}

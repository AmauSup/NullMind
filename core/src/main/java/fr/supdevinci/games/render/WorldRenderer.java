package fr.supdevinci.games.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import fr.supdevinci.games.assets.GameAssets;
import fr.supdevinci.games.config.ColorPalette;
import fr.supdevinci.games.world.GameWorld;
import fr.supdevinci.games.world.InteractableObject;
import fr.supdevinci.games.world.InteractableType;
import fr.supdevinci.games.world.KeyPickup;
import fr.supdevinci.games.world.LevelDefinition;
import fr.supdevinci.games.world.LevelId;
import fr.supdevinci.games.world.Obstacle;
import fr.supdevinci.games.world.TransitionZone;

/**
 * Renders the world using themed placeholders so the prototype stays readable without final textures.
 */
public final class WorldRenderer {
    private static final Color[] HUB_OBSTACLE_COLORS = {
        ColorPalette.BUILDING_HOUSE,
        ColorPalette.BUILDING_LIBRARY,
        ColorPalette.BUILDING_CEMETERY,
        ColorPalette.BUILDING_PORT
    };

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
        drawInteractables(level, gameWorld);
        drawSpecialDoors(level, gameWorld);
        drawKeyPickups(level, gameWorld);

        // Draw player
        shapeRenderer.setColor(ColorPalette.PLAYER);
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

        TransitionZone cellarDoor = findCellarTransition(level);
        if (cellarDoor == null) {
            return;
        }

        boolean unlocked = cellarDoor.getRequiredKeys().isEmpty()
            || gameWorld.getInventory().hasAllKeys(cellarDoor.getRequiredKeys());

        Color doorBody = unlocked ? ColorPalette.DOOR_OPEN : ColorPalette.DOOR_LOCKED;
        Color doorFrame = ColorPalette.DOOR_FRAME;

        shapeRenderer.setColor(doorFrame);
        shapeRenderer.rect(
            cellarDoor.getArea().x - 3f,
            cellarDoor.getArea().y - 3f,
            cellarDoor.getArea().width + 6f,
            cellarDoor.getArea().height + 6f
        );

        shapeRenderer.setColor(doorBody);
        shapeRenderer.rect(
            cellarDoor.getArea().x,
            cellarDoor.getArea().y,
            cellarDoor.getArea().width,
            cellarDoor.getArea().height
        );
    }

    private TransitionZone findCellarTransition(LevelDefinition level) {
        for (TransitionZone transitionZone : level.getTransitionZones()) {
            if (transitionZone.getTargetLevelId() == LevelId.CELLAR) {
                return transitionZone;
            }
        }
        return null;
    }

    private void drawThemedBackground(LevelDefinition level) {
        switch (level.getId()) {
            case HUB:
                drawHubBackground(level);
                return;
            case HOUSE:
                drawHouseBackground(level);
                return;
            case LIBRARY:
                drawLibraryBackground(level);
                return;
            case PORT:
                drawPortBackground(level);
                return;
            case CEMETERY:
                drawCemeteryBackground(level);
                return;
            default:
                shapeRenderer.setColor(level.getBackgroundColor());
                shapeRenderer.rect(0f, 0f, level.getWidth(), level.getHeight());
        }
    }

    private void drawHubBackground(LevelDefinition level) {
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
    }

    private void drawHouseBackground(LevelDefinition level) {
        shapeRenderer.setColor(ColorPalette.HOUSE_BACKGROUND);
        shapeRenderer.rect(0f, 0f, level.getWidth(), level.getHeight());
        shapeRenderer.setColor(ColorPalette.HOUSE_MAIN_AREA);
        shapeRenderer.rect(90f, 70f, 780f, 430f);
    }

    private void drawLibraryBackground(LevelDefinition level) {
        shapeRenderer.setColor(ColorPalette.LIBRARY_BACKGROUND);
        shapeRenderer.rect(0f, 0f, level.getWidth(), level.getHeight());
        shapeRenderer.setColor(ColorPalette.LIBRARY_MAIN_AREA);
        shapeRenderer.rect(120f, 80f, 760f, 360f);
    }

    private void drawPortBackground(LevelDefinition level) {
        shapeRenderer.setColor(ColorPalette.PORT_DOCK);
        shapeRenderer.rect(0f, 0f, level.getWidth(), 360f);
        shapeRenderer.setColor(ColorPalette.PORT_WATER);
        shapeRenderer.rect(0f, 360f, level.getWidth(), 180f);
        shapeRenderer.setColor(ColorPalette.PORT_PIER);
        shapeRenderer.rect(420f, 160f, 120f, 160f);

        shapeRenderer.setColor(ColorPalette.BROKEN_BRIDGE_PART);
        shapeRenderer.rect(430f, 378f, 80f, 22f);
        shapeRenderer.rect(530f, 430f, 70f, 22f);
        shapeRenderer.rect(390f, 480f, 70f, 22f);
    }

    private void drawCemeteryBackground(LevelDefinition level) {
        shapeRenderer.setColor(ColorPalette.CEMETERY_BACKGROUND);
        shapeRenderer.rect(0f, 0f, level.getWidth(), level.getHeight());
        shapeRenderer.setColor(ColorPalette.CEMETERY_MAIN_AREA);
        shapeRenderer.rect(120f, 90f, 760f, 300f);
    }

    private void drawInteractables(LevelDefinition level, GameWorld gameWorld) {
        for (InteractableObject interactableObject : level.getInteractableObjects()) {
            boolean explored = gameWorld.isInteractableExplored(interactableObject);
            if (interactableObject.getType() == InteractableType.BOOK) {
                shapeRenderer.setColor(explored ? ColorPalette.BOOK_EXPLORED : ColorPalette.BOOK);
            } else {
                shapeRenderer.setColor(explored ? ColorPalette.GRAVE_EXPLORED : ColorPalette.GRAVE);
            }

            shapeRenderer.rect(
                interactableObject.getArea().x,
                interactableObject.getArea().y,
                interactableObject.getArea().width,
                interactableObject.getArea().height
            );
        }
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
            Color color = index < HUB_OBSTACLE_COLORS.length
                ? HUB_OBSTACLE_COLORS[index]
                : ColorPalette.OBSTACLE_HUB;

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
        switch (levelId) {
            case HUB:
                return ColorPalette.OBSTACLE_HUB;
            case LIBRARY:
                return ColorPalette.OBSTACLE_LIBRARY;
            case PORT:
                return ColorPalette.OBSTACLE_PORT;
            case CEMETERY:
                return ColorPalette.OBSTACLE_CEMETERY;
            case HOUSE:
                return ColorPalette.OBSTACLE_HOUSE;
            default:
                return ColorPalette.OBSTACLE_DEFAULT;
        }
    }

    private void drawKeyPickups(LevelDefinition level, GameWorld gameWorld) {
        shapeRenderer.setColor(ColorPalette.KEY_PICKUP);
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

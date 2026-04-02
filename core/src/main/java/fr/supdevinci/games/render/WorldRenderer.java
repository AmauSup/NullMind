package fr.supdevinci.games.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
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

import java.util.EnumMap;
import java.util.function.Consumer;

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
    private final SpriteBatch spriteBatch;
    private final Texture houseFloorTexture;
    private final Texture cemeteryTopTexture;
    private final Texture graveTexture;
    private final Texture graveExploredTexture;
    private final Texture keyPickupTexture;
    private final EnumMap<LevelId, Consumer<LevelDefinition>> backgroundRenderers;
    private final EnumMap<LevelId, Color> obstacleColors;

    /**
     * Creates a renderer for world layers and optional texture overlays.
     *
     * @param assets shared game assets
     */
    public WorldRenderer(GameAssets assets) {
        this.shapeRenderer = assets.getShapeRenderer();
        this.spriteBatch = assets.getSpriteBatch();
        this.houseFloorTexture = assets.getHouseFloorTextureOrNull();
        this.cemeteryTopTexture = assets.getCemeteryTopTextureOrNull();
        this.graveTexture = assets.getGraveTextureOrNull();
        this.graveExploredTexture = assets.getGraveExploredTextureOrNull();
        this.keyPickupTexture = assets.getKeyPickupTextureOrNull();
        this.backgroundRenderers = createBackgroundRenderers();
        this.obstacleColors = createObstacleColors();
    }

    /**
     * Draws the current map, its blocking areas, transitions and the player.
     *
     * @param gameWorld current world state
     * @param camera world camera
     */
    public void render(GameWorld gameWorld, OrthographicCamera camera) {
        LevelDefinition level = gameWorld.getCurrentLevel();
        drawHouseTexturedFloorIfAvailable(level, camera);
        drawCemeteryTopTextureIfAvailable(level, camera);
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawThemedBackground(level);
        drawTransitions(level);
        drawObstacles(level);
        drawInteractables(level, gameWorld);
        drawSpecialDoors(level, gameWorld);
        drawKeyPickups(level, gameWorld);
        shapeRenderer.end();

        drawGraveTextures(level, gameWorld, camera);
        drawKeyPickupTexturesIfAvailable(level, gameWorld, camera);

        // Draw player above interactables
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
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

    /**
     * Draws cemetery grave textures on top of shape layers when available.
     *
     * @param level current level definition
     * @param gameWorld current world state
     * @param camera active world camera
     */
    private void drawGraveTextures(LevelDefinition level, GameWorld gameWorld, OrthographicCamera camera) {
        if (!canDrawGraveTextures(level)) {
            return;
        }

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        for (InteractableObject interactableObject : level.getInteractableObjects()) {
            drawGraveTextureIfApplicable(interactableObject, gameWorld);
        }
        spriteBatch.end();
    }

    /**
     * Checks whether textured graves can be rendered for this level.
     *
     * @param level current level definition
     * @return {@code true} when cemetery grave texture rendering is enabled
     */
    private boolean canDrawGraveTextures(LevelDefinition level) {
        return level.getId() == LevelId.CEMETERY && graveTexture != null;
    }

    /**
     * Draws one grave texture if the provided interactable is a grave.
     *
     * @param interactableObject interactable candidate
     * @param gameWorld current world state
     */
    private void drawGraveTextureIfApplicable(InteractableObject interactableObject, GameWorld gameWorld) {
        if (interactableObject.getType() != InteractableType.GRAVE) {
            return;
        }

        boolean explored = gameWorld.isInteractableExplored(interactableObject);
        Texture texture = getGraveTexture(explored);
        Rectangle area = interactableObject.getArea();
        spriteBatch.draw(texture, area.x, area.y, area.width, area.height);
    }

    /**
     * Selects the proper grave texture according to exploration state.
     *
     * @param explored whether the grave was already explored
     * @return texture to render for this grave
     */
    private Texture getGraveTexture(boolean explored) {
        if (explored && graveExploredTexture != null) {
            return graveExploredTexture;
        }
        return graveTexture;
    }

    /**
     * Draws the textured house floor when that texture exists.
     *
     * @param level current level definition
     * @param camera active world camera
     */
    private void drawHouseTexturedFloorIfAvailable(LevelDefinition level, OrthographicCamera camera) {
        if (level.getId() != LevelId.HOUSE || houseFloorTexture == null) {
            return;
        }

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        spriteBatch.setColor(Color.WHITE);
        spriteBatch.draw(houseFloorTexture, 0f, 0f, level.getWidth(), level.getHeight());
        spriteBatch.end();
    }

    /**
     * Draws the top cemetery building texture when available.
     *
     * @param level current level definition
     * @param camera active world camera
     */
    private void drawCemeteryTopTextureIfAvailable(LevelDefinition level, OrthographicCamera camera) {
        if (level.getId() != LevelId.CEMETERY || cemeteryTopTexture == null) {
            return;
        }

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        spriteBatch.setColor(Color.WHITE);
        // Matches the upper cemetery block area used by the fallback shape design
        spriteBatch.draw(cemeteryTopTexture, 330f, 390f, 300f, 140f);
        spriteBatch.end();
    }

    /**
     * Draws level-specific doors (currently used in house level).
     *
     * @param level current level definition
     * @param gameWorld current world state
     */
    private void drawSpecialDoors(LevelDefinition level, GameWorld gameWorld) {
        if (!shouldDrawSpecialDoors(level)) {
            return;
        }

        TransitionZone cellarDoor = findCellarTransition(level);
        if (cellarDoor == null) {
            return;
        }

        drawDoorFrame(cellarDoor);
        drawDoorBody(cellarDoor, isDoorUnlocked(cellarDoor, gameWorld));
    }

    /**
     * Indicates whether special door rendering is needed.
     *
     * @param level current level definition
     * @return {@code true} when special doors should be drawn
     */
    private boolean shouldDrawSpecialDoors(LevelDefinition level) {
        return level.getId() == LevelId.HOUSE;
    }

    /**
     * Checks if a door transition is unlocked by current inventory.
     *
     * @param cellarDoor transition representing the cellar door
     * @param gameWorld current world state
     * @return {@code true} when transition requirements are satisfied
     */
    private boolean isDoorUnlocked(TransitionZone cellarDoor, GameWorld gameWorld) {
        return cellarDoor.getRequiredKeys().isEmpty()
            || gameWorld.getInventory().hasAllKeys(cellarDoor.getRequiredKeys());
    }

    /**
     * Draws the decorative frame around a door transition.
     *
     * @param cellarDoor transition representing the cellar door
     */
    private void drawDoorFrame(TransitionZone cellarDoor) {
        shapeRenderer.setColor(ColorPalette.DOOR_FRAME);
        shapeRenderer.rect(
            cellarDoor.getArea().x - 3f,
            cellarDoor.getArea().y - 3f,
            cellarDoor.getArea().width + 6f,
            cellarDoor.getArea().height + 6f
        );
    }

    /**
     * Draws the door body in open/locked color.
     *
     * @param cellarDoor transition representing the cellar door
     * @param unlocked whether the door is unlocked
     */
    private void drawDoorBody(TransitionZone cellarDoor, boolean unlocked) {
        shapeRenderer.setColor(unlocked ? ColorPalette.DOOR_OPEN : ColorPalette.DOOR_LOCKED);
        shapeRenderer.rect(
            cellarDoor.getArea().x,
            cellarDoor.getArea().y,
            cellarDoor.getArea().width,
            cellarDoor.getArea().height
        );
    }

    /**
     * Finds the transition that leads to cellar in the provided level.
     *
     * @param level current level definition
     * @return cellar transition, or {@code null} if not present
     */
    private TransitionZone findCellarTransition(LevelDefinition level) {
        for (TransitionZone transitionZone : level.getTransitionZones()) {
            if (transitionZone.getTargetLevelId() == LevelId.CELLAR) {
                return transitionZone;
            }
        }
        return null;
    }

    /**
     * Draws background using the renderer registered for the level.
     *
     * @param level current level definition
     */
    private void drawThemedBackground(LevelDefinition level) {
        backgroundRenderers.getOrDefault(level.getId(), this::drawDefaultBackground).accept(level);
    }

    /**
     * Creates map-specific background renderers.
     *
     * @return background renderer map by level id
     */
    private EnumMap<LevelId, Consumer<LevelDefinition>> createBackgroundRenderers() {
        EnumMap<LevelId, Consumer<LevelDefinition>> renderers = new EnumMap<>(LevelId.class);
        renderers.put(LevelId.HUB, this::drawHubBackground);
        renderers.put(LevelId.HOUSE, this::drawHouseBackground);
        renderers.put(LevelId.LIBRARY, this::drawLibraryBackground);
        renderers.put(LevelId.PORT, this::drawPortBackground);
        renderers.put(LevelId.CEMETERY, this::drawCemeteryBackground);
        return renderers;
    }

    /**
     * Draws the default level background color.
     *
     * @param level current level definition
     */
    private void drawDefaultBackground(LevelDefinition level) {
        shapeRenderer.setColor(level.getBackgroundColor());
        shapeRenderer.rect(0f, 0f, level.getWidth(), level.getHeight());
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

    /**
     * Draws house fallback background (when no texture is available).
     *
     * @param level current level definition
     */
    private void drawHouseBackground(LevelDefinition level) {
        if (houseFloorTexture != null) {
            return;
        }
        shapeRenderer.setColor(ColorPalette.HOUSE_BACKGROUND);
        shapeRenderer.rect(0f, 0f, level.getWidth(), level.getHeight());
        shapeRenderer.setColor(ColorPalette.HOUSE_MAIN_AREA);
        shapeRenderer.rect(90f, 70f, 780f, 430f);
    }

    /**
     * Draws library background.
     *
     * @param level current level definition
     */
    private void drawLibraryBackground(LevelDefinition level) {
        shapeRenderer.setColor(ColorPalette.LIBRARY_BACKGROUND);
        shapeRenderer.rect(0f, 0f, level.getWidth(), level.getHeight());
        shapeRenderer.setColor(ColorPalette.LIBRARY_MAIN_AREA);
        shapeRenderer.rect(120f, 80f, 760f, 360f);
    }

    /**
     * Draws port background and bridge decorations.
     *
     * @param level current level definition
     */
    private void drawPortBackground(LevelDefinition level) {
        // Dock (lower area)
        shapeRenderer.setColor(ColorPalette.PORT_DOCK);
        shapeRenderer.rect(0f, 0f, level.getWidth(), level.getHeight());

        // Water zone (upper area)
        shapeRenderer.setColor(ColorPalette.PORT_WATER);
        shapeRenderer.rect(0f, 360f, level.getWidth(), 180f);

        // Bridge approach piers — connect dock to bridge on left and right sides
        shapeRenderer.setColor(ColorPalette.PORT_PIER);
        shapeRenderer.rect(0f,   360f, 120f, 100f);   // left approach  (x=0-120, y=360-460)
        shapeRenderer.rect(840f, 360f, 120f, 100f);   // right approach (x=840-960, y=360-460)

        // Bridge deck (full width, y=410-460)
        shapeRenderer.setColor(ColorPalette.BRIDGE_DECK);
        shapeRenderer.rect(0f, 410f, level.getWidth(), 50f);

        // Bridge guard rails (aesthetic lines, not collision obstacles)
        shapeRenderer.setColor(ColorPalette.PORT_PIER);
        shapeRenderer.rect(0f, 407f, level.getWidth(), 4f);   // south rail
        shapeRenderer.rect(0f, 457f, level.getWidth(), 4f);   // north rail
    }

    /**
     * Draws cemetery background and fallback top cross shape.
     *
     * @param level current level definition
     */
    private void drawCemeteryBackground(LevelDefinition level) {
        shapeRenderer.setColor(ColorPalette.CEMETERY_BACKGROUND);
        shapeRenderer.rect(0f, 0f, level.getWidth(), level.getHeight());
        shapeRenderer.setColor(ColorPalette.CEMETERY_MAIN_AREA);
        shapeRenderer.rect(120f, 90f, 760f, 300f);

        if (cemeteryTopTexture != null) {
            return;
        }

        // Church cross — drawn as two overlapping rectangles on the church obstacle (360,390,240,120)
        shapeRenderer.setColor(ColorPalette.OBSTACLE_CEMETERY);
        shapeRenderer.rect(462f, 430f, 36f, 60f);   // vertical beam
        shapeRenderer.rect(440f, 462f, 80f, 20f);   // horizontal beam
    }

    /**
     * Draws interactable objects (books and fallback graves).
     *
     * @param level current level definition
     * @param gameWorld current world state
     */
    private void drawInteractables(LevelDefinition level, GameWorld gameWorld) {
        boolean texturedGraves = shouldDrawTexturedGraves(level);
        for (InteractableObject interactableObject : level.getInteractableObjects()) {
            if (interactableObject.getType() == InteractableType.GRAVE) {
                drawGraveInteractable(interactableObject, gameWorld, texturedGraves);
                continue;
            }

            drawDefaultInteractable(interactableObject, gameWorld);
        }
    }

    /**
     * Draws a grave interactable using fallback shape rendering when needed.
     *
     * @param interactableObject grave interactable
     * @param gameWorld current world state
     * @param texturedGraves whether textured grave rendering is already active
     */
    private void drawGraveInteractable(InteractableObject interactableObject, GameWorld gameWorld, boolean texturedGraves) {
        if (texturedGraves) {
            return;
        }

        Rectangle area = interactableObject.getArea();
        boolean explored = gameWorld.isInteractableExplored(interactableObject);
        drawTombstone(area.x, area.y, area.width, area.height, explored);
    }

    /**
     * Draws a non-grave interactable with its contextual color.
     *
     * @param interactableObject interactable to draw
     * @param gameWorld current world state
     */
    private void drawDefaultInteractable(InteractableObject interactableObject, GameWorld gameWorld) {
        boolean explored = gameWorld.isInteractableExplored(interactableObject);
        Rectangle area = interactableObject.getArea();
        shapeRenderer.setColor(getInteractableColor(interactableObject, explored));
        shapeRenderer.rect(area.x, area.y, area.width, area.height);
    }

    /**
     * Indicates whether graves should be rendered with textures.
     *
     * @param level current level definition
     * @return {@code true} when textured grave rendering is active
     */
    private boolean shouldDrawTexturedGraves(LevelDefinition level) {
        return level.getId() == LevelId.CEMETERY && graveTexture != null;
    }

    /**
     * Draws a tombstone silhouette: a wide base with a narrower rounded head.
     * Uses shape primitives as a fallback when no grave texture is available.
     *
     * @param x        left edge of the tombstone bounds
     * @param y        bottom edge
     * @param w        total width
     * @param h        total height
     * @param explored whether the grave has already been searched
     */
    private void drawTombstone(float x, float y, float w, float h, boolean explored) {
        shapeRenderer.setColor(explored ? ColorPalette.GRAVE_EXPLORED : ColorPalette.GRAVE);
        // Stone body (lower 65 %)
        shapeRenderer.rect(x, y, w, h * 0.65f);
        // Stone head — narrower, creating a classic tombstone silhouette
        float headW = w * 0.65f;
        shapeRenderer.rect(x + (w - headW) / 2f, y + h * 0.65f, headW, h * 0.35f);
    }

    /**
     * Resolves interactable color based on type and exploration status.
     *
     * @param interactableObject interactable object
     * @param explored whether it has already been explored
     * @return color to draw
     */
    private Color getInteractableColor(InteractableObject interactableObject, boolean explored) {
        if (interactableObject.getType() == InteractableType.BOOK) {
            return explored ? ColorPalette.BOOK_EXPLORED : ColorPalette.BOOK;
        } else {
            return explored ? ColorPalette.GRAVE_EXPLORED : ColorPalette.GRAVE;
        }
    }

    /**
     * Draws transition zones with translucent accent color.
     *
     * @param level current level definition
     */
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

    /**
     * Draws collision obstacles for the current level.
     *
     * @param level current level definition
     */
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

    /**
     * Draws hub obstacles with distinct per-building colors.
     *
     * @param level hub level definition
     */
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

    /**
     * Returns obstacle color for a given level.
     *
     * @param levelId level identifier
     * @return configured obstacle color
     */
    private Color getObstacleColor(LevelId levelId) {
        return obstacleColors.getOrDefault(levelId, ColorPalette.OBSTACLE_DEFAULT);
    }

    /**
     * Creates obstacle colors mapped by level id.
     *
     * @return obstacle color map
     */
    private EnumMap<LevelId, Color> createObstacleColors() {
        EnumMap<LevelId, Color> colors = new EnumMap<>(LevelId.class);
        colors.put(LevelId.HUB, ColorPalette.OBSTACLE_HUB);
        colors.put(LevelId.LIBRARY, ColorPalette.OBSTACLE_LIBRARY);
        colors.put(LevelId.PORT, ColorPalette.OBSTACLE_PORT);
        colors.put(LevelId.CEMETERY, ColorPalette.OBSTACLE_CEMETERY);
        colors.put(LevelId.HOUSE, ColorPalette.OBSTACLE_HOUSE);
        return colors;
    }

    /**
     * Draws uncollected key pickups.
     *
     * @param level current level definition
     * @param gameWorld current world state
     */
    private void drawKeyPickups(LevelDefinition level, GameWorld gameWorld) {
        if (keyPickupTexture != null) {
            return;
        }

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

    /**
     * Draws textured key pickups above shape layers when a key texture is available.
     *
     * @param level current level definition
     * @param gameWorld current world state
     * @param camera active world camera
     */
    private void drawKeyPickupTexturesIfAvailable(LevelDefinition level, GameWorld gameWorld, OrthographicCamera camera) {
        if (keyPickupTexture == null) {
            return;
        }

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        for (KeyPickup keyPickup : level.getKeyPickups()) {
            if (!gameWorld.isPickupCollected(keyPickup)) {
                spriteBatch.draw(
                    keyPickupTexture,
                    keyPickup.getArea().x,
                    keyPickup.getArea().y,
                    keyPickup.getArea().width,
                    keyPickup.getArea().height
                );
            }
        }
        spriteBatch.end();
    }
}

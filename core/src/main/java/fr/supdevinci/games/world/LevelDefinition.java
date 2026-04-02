package fr.supdevinci.games.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable description of a playable level.
 *
 * This project currently uses flat levels only.
 */
public final class LevelDefinition {
    /**
     * Immutable level dimensions.
     */
    public static final class Size {
        private final float width;
        private final float height;

        private Size(float width, float height) {
            this.width = width;
            this.height = height;
        }

        /**
         * Creates a size object.
         *
         * @param width level width
         * @param height level height
         * @return size value object
         */
        public static Size of(float width, float height) {
            return new Size(width, height);
        }

        /**
         * Returns width.
         *
         * @return level width
         */
        public float width() {
            return width;
        }

        /**
         * Returns height.
         *
         * @return level height
         */
        public float height() {
            return height;
        }
    }

    /**
     * Immutable color pair for a level.
     */
    public static final class Colors {
        private final Color background;
        private final Color accent;

        private Colors(Color background, Color accent) {
            this.background = background;
            this.accent = accent;
        }

        /**
         * Creates a color configuration.
         *
         * @param background background color
         * @param accent accent color
         * @return colors value object
         */
        public static Colors of(Color background, Color accent) {
            return new Colors(background, accent);
        }

        /**
         * Returns background color.
         *
         * @return background color
         */
        public Color background() {
            return background;
        }

        /**
         * Returns accent color.
         *
         * @return accent color
         */
        public Color accent() {
            return accent;
        }
    }

    /**
     * Immutable content container for flat levels.
     */
    public static final class FlatContent {
        private final List<Obstacle> obstacles;
        private final List<TransitionZone> transitionZones;
        private final List<KeyPickup> keyPickups;
        private final List<InteractableObject> interactableObjects;

        private FlatContent(List<Obstacle> obstacles, List<TransitionZone> transitionZones,
                            List<KeyPickup> keyPickups, List<InteractableObject> interactableObjects) {
            this.obstacles = List.copyOf(obstacles);
            this.transitionZones = List.copyOf(transitionZones);
            this.keyPickups = List.copyOf(keyPickups);
            this.interactableObjects = List.copyOf(interactableObjects);
        }

        /**
         * Creates flat content without interactables.
         *
         * @param obstacles blocking obstacles
         * @param transitionZones transition zones
         * @param keyPickups key pickups
         * @return flat content object
         */
        public static FlatContent of(List<Obstacle> obstacles, List<TransitionZone> transitionZones, List<KeyPickup> keyPickups) {
            return new FlatContent(obstacles, transitionZones, keyPickups, List.of());
        }

        /**
         * Creates flat content with interactables.
         *
         * @param obstacles blocking obstacles
         * @param transitionZones transition zones
         * @param keyPickups key pickups
         * @param interactableObjects interactable objects
         * @return flat content object
         */
        public static FlatContent of(List<Obstacle> obstacles, List<TransitionZone> transitionZones,
                                     List<KeyPickup> keyPickups, List<InteractableObject> interactableObjects) {
            return new FlatContent(obstacles, transitionZones, keyPickups, interactableObjects);
        }
    }

    private final LevelId id;
    private final float width;
    private final float height;
    private final Color backgroundColor;
    private final Color accentColor;
    private final List<Obstacle> obstacles;
    private final List<TransitionZone> transitionZones;
    private final List<KeyPickup> keyPickups;
    private final List<InteractableObject> interactableObjects;
    private final Map<String, SpawnPoint> spawnPoints;
    private final String defaultSpawnId;

    /**
     * Builds an immutable flat level definition.
     *
     * @param id level id
     * @param size level size
     * @param colors level colors
     * @param flatContent flat level content
     * @param spawnPoints available spawn points
     * @param defaultSpawnId fallback spawn id
     * @return immutable level definition
     */
    public static LevelDefinition flat(
        LevelId id,
        Size size,
        Colors colors,
        FlatContent flatContent,
        Map<String, SpawnPoint> spawnPoints,
        String defaultSpawnId
    ) {
        return new LevelDefinition(id, size, colors, spawnPoints, defaultSpawnId, flatContent);
    }

    private LevelDefinition(
        LevelId id,
        Size size,
        Colors colors,
        Map<String, SpawnPoint> spawnPoints,
        String defaultSpawnId,
        FlatContent flatContent
    ) {
        this.id = id;
        this.width = size.width();
        this.height = size.height();
        this.backgroundColor = new Color(colors.background());
        this.accentColor = new Color(colors.accent());
        this.obstacles = List.copyOf(flatContent.obstacles);
        this.transitionZones = List.copyOf(flatContent.transitionZones);
        this.keyPickups = List.copyOf(flatContent.keyPickups);
        this.interactableObjects = List.copyOf(flatContent.interactableObjects);
        this.spawnPoints = Collections.unmodifiableMap(new LinkedHashMap<>(spawnPoints));
        this.defaultSpawnId = defaultSpawnId;
    }

    /**
     * Returns level id.
     *
     * @return level id
     */
    public LevelId getId() {
        return id;
    }

    /**
     * Returns localized level display name.
     *
     * @return level display name
     */
    public String getDisplayName() {
        return id.getDisplayName();
    }

    /**
     * Returns level width.
     *
     * @return width
     */
    public float getWidth() {
        return width;
    }

    /**
     * Returns level height.
     *
     * @return height
     */
    public float getHeight() {
        return height;
    }

    /**
     * Returns background color.
     *
     * @return background color
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Returns accent color.
     *
     * @return accent color
     */
    public Color getAccentColor() {
        return accentColor;
    }

    /**
     * Returns level obstacles.
     *
     * @return immutable obstacle list
     */
    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    /**
     * Returns transition zones.
     *
     * @return immutable transition list
     */
    public List<TransitionZone> getTransitionZones() {
        return transitionZones;
    }

    /**
     * Returns key pickups.
     *
     * @return immutable key pickup list
     */
    public List<KeyPickup> getKeyPickups() {
        return keyPickups;
    }

    /**
     * Returns interactable objects.
     *
     * @return immutable interactable list
     */
    public List<InteractableObject> getInteractableObjects() {
        return interactableObjects;
    }

    /**
     * Resolves a spawn point by id and falls back to the default one when missing.
     *
     * @param spawnId requested spawn id
     * @return a valid spawn point for this level
     */
    public SpawnPoint resolveSpawn(String spawnId) {
        SpawnPoint requestedSpawn = spawnPoints.get(spawnId);
        if (requestedSpawn != null) {
            return requestedSpawn;
        }
        return spawnPoints.get(defaultSpawnId);
    }

    /**
     * @return the playable world bounds of the level
     */
    public Rectangle getWorldBounds() {
        return new Rectangle(0f, 0f, width, height);
    }

}

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
    public static final class Size {
        private final float width;
        private final float height;

        private Size(float width, float height) {
            this.width = width;
            this.height = height;
        }

        public static Size of(float width, float height) {
            return new Size(width, height);
        }

        public float width() {
            return width;
        }

        public float height() {
            return height;
        }
    }

    public static final class Colors {
        private final Color background;
        private final Color accent;

        private Colors(Color background, Color accent) {
            this.background = background;
            this.accent = accent;
        }

        public static Colors of(Color background, Color accent) {
            return new Colors(background, accent);
        }

        public Color background() {
            return background;
        }

        public Color accent() {
            return accent;
        }
    }

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

        public static FlatContent of(List<Obstacle> obstacles, List<TransitionZone> transitionZones, List<KeyPickup> keyPickups) {
            return new FlatContent(obstacles, transitionZones, keyPickups, List.of());
        }

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

    public LevelId getId() {
        return id;
    }

    public String getDisplayName() {
        return id.getDisplayName();
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Color getAccentColor() {
        return accentColor;
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public List<TransitionZone> getTransitionZones() {
        return transitionZones;
    }

    public List<KeyPickup> getKeyPickups() {
        return keyPickups;
    }

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

package fr.supdevinci.games.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import fr.supdevinci.games.room.Room;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Immutable description of a playable level.
 *
 * <p>Supports two modes:
 * <ul>
 *   <li><strong>Flat levels</strong> (Hub, exterior zones): obstacles and transitions define level structure.</li>
 *   <li><strong>Interior levels</strong> (House): rooms and doors define multi-room exploration.</li>
 * </ul>
 * </p>
 */
public final class LevelDefinition {
    private final LevelId id;
    private final float width;
    private final float height;
    private final Color backgroundColor;
    private final Color accentColor;
    private final List<Obstacle> obstacles;
    private final List<TransitionZone> transitionZones;
    private final List<KeyPickup> keyPickups;
    private final Map<String, SpawnPoint> spawnPoints;
    private final String defaultSpawnId;
    private final Optional<Map<String, Room>> rooms;
    private final Optional<String> currentRoomId;

    /**
     * Creates a flat level definition (hub, exterior zones).
     *
     * @param id stable level identifier
     * @param width world width
     * @param height world height
     * @param backgroundColor clear color used for the level
     * @param accentColor secondary color used for rendering points of interest
     * @param obstacles blocking rectangles
     * @param transitionZones exits to other levels
    * @param keyPickups collectible keys available in the level
     * @param spawnPoints reusable spawn points indexed by id
     * @param defaultSpawnId fallback spawn point id
     */
    public LevelDefinition(
        LevelId id,
        float width,
        float height,
        Color backgroundColor,
        Color accentColor,
        List<Obstacle> obstacles,
        List<TransitionZone> transitionZones,
        List<KeyPickup> keyPickups,
        Map<String, SpawnPoint> spawnPoints,
        String defaultSpawnId
    ) {
        this(id, width, height, backgroundColor, accentColor, obstacles, transitionZones, keyPickups, spawnPoints, defaultSpawnId, null, null);
    }

    /**
     * Creates an interior level definition (house with multiple rooms).
     *
     * @param id stable level identifier
     * @param width world width (room viewport size)
     * @param height world height (room viewport size)
     * @param backgroundColor fallback clear color
     * @param accentColor secondary color
     * @param rooms map of room id to room definition
     * @param spawnPoints reusable spawn points for room entry points
     * @param defaultSpawnId fallback spawn point id
     * @param currentRoomId the initial room to load
     */
    public LevelDefinition(
        LevelId id,
        float width,
        float height,
        Color backgroundColor,
        Color accentColor,
        Map<String, Room> rooms,
        Map<String, SpawnPoint> spawnPoints,
        String defaultSpawnId,
        String currentRoomId
    ) {
        this(id, width, height, backgroundColor, accentColor, List.of(), List.of(), List.of(), spawnPoints, defaultSpawnId, rooms, currentRoomId);
    }

    /**
     * Private constructor for both flat and interior levels.
     */
    private LevelDefinition(
        LevelId id,
        float width,
        float height,
        Color backgroundColor,
        Color accentColor,
        List<Obstacle> obstacles,
        List<TransitionZone> transitionZones,
        List<KeyPickup> keyPickups,
        Map<String, SpawnPoint> spawnPoints,
        String defaultSpawnId,
        Map<String, Room> roomsMap,
        String currentRoomIdValue
    ) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.backgroundColor = new Color(backgroundColor);
        this.accentColor = new Color(accentColor);
        this.obstacles = List.copyOf(obstacles);
        this.transitionZones = List.copyOf(transitionZones);
        this.keyPickups = List.copyOf(keyPickups);
        this.spawnPoints = Collections.unmodifiableMap(new LinkedHashMap<>(spawnPoints));
        this.defaultSpawnId = defaultSpawnId;
        this.rooms = roomsMap != null ? Optional.of(Map.copyOf(roomsMap)) : Optional.empty();
        this.currentRoomId = currentRoomIdValue != null ? Optional.of(currentRoomIdValue) : Optional.empty();
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

    /**
     * @return true if this level has multiple rooms (interior level)
     */
    public boolean isInteriorLevel() {
        return rooms.isPresent();
    }

    /**
     * @return optional map of rooms for interior levels
     */
    public Optional<Map<String, Room>> getRooms() {
        return rooms;
    }

    /**
     * @return optional current room id for interior levels
     */
    public Optional<String> getCurrentRoomId() {
        return currentRoomId;
    }
}

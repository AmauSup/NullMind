package fr.supdevinci.games.room;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import fr.supdevinci.games.world.Obstacle;
import fr.supdevinci.games.world.KeyPickup;

import java.util.List;
import java.util.Objects;

/**
 * Represents a room (pièce) in the house.
 * Contains: layout, obstacles, doors, collectible keys.
 * 
 * Designed to be:
 * - Independently testable
 * - Serializable later (for loading/saving)
 * - Easy to extend with properties (lighting, events, NPCs)
 */
public final class Room {
    private final String id;
    private final String displayName;
    private final String description;
    private final Color baseColor;
    private final Rectangle bounds;
    private final List<Obstacle> obstacles;
    private final List<Door> doors;
    private final List<KeyPickup> keyPickups;
    private final Rectangle spawnArea;

    public Room(String id, String displayName, String description, Color baseColor,
                float x, float y, float width, float height,
                List<Obstacle> obstacles, List<Door> doors,
                List<KeyPickup> keyPickups, Rectangle spawnArea) {
        this.id = Objects.requireNonNull(id);
        this.displayName = Objects.requireNonNull(displayName);
        this.description = Objects.requireNonNull(description);
        this.baseColor = Objects.requireNonNull(baseColor);
        this.bounds = new Rectangle(x, y, width, height);
        this.obstacles = List.copyOf(obstacles);
        this.doors = List.copyOf(doors);
        this.keyPickups = List.copyOf(keyPickups);
        this.spawnArea = Objects.requireNonNull(spawnArea);
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public Color getBaseColor() {
        return baseColor;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public List<Door> getDoors() {
        return doors;
    }

    public List<KeyPickup> getKeyPickups() {
        return keyPickups;
    }

    public Rectangle getSpawnArea() {
        return spawnArea;
    }

    /**
     * Finds a door by target room id.
     * @param targetRoomId the target room identifier
     * @return door if found, null otherwise
     */
    public Door getDoorTo(String targetRoomId) {
        return doors.stream()
            .filter(d -> d.getTargetRoomId().equals(targetRoomId))
            .findFirst()
            .orElse(null);
    }
}

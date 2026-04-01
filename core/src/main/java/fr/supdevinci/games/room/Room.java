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

    public Room(Builder builder) {
        this.id = Objects.requireNonNull(builder.id);
        this.displayName = Objects.requireNonNull(builder.displayName);
        this.description = Objects.requireNonNull(builder.description);
        this.baseColor = Objects.requireNonNull(builder.baseColor);
        this.bounds = new Rectangle(builder.bounds.x, builder.bounds.y, builder.bounds.width, builder.bounds.height);
        this.obstacles = List.copyOf(builder.obstacles);
        this.doors = List.copyOf(builder.doors);
        this.keyPickups = List.copyOf(builder.keyPickups);
        this.spawnArea = new Rectangle(builder.spawnArea);
    }

    public static Builder builder(String id, String displayName, String description, Color baseColor, Rectangle bounds) {
        return new Builder(id, displayName, description, baseColor, bounds);
    }

    public static final class Builder {
        private final String id;
        private final String displayName;
        private final String description;
        private final Color baseColor;
        private final Rectangle bounds;
        private List<Obstacle> obstacles = List.of();
        private List<Door> doors = List.of();
        private List<KeyPickup> keyPickups = List.of();
        private Rectangle spawnArea = new Rectangle();

        private Builder(String id, String displayName, String description, Color baseColor, Rectangle bounds) {
            this.id = id;
            this.displayName = displayName;
            this.description = description;
            this.baseColor = baseColor;
            this.bounds = new Rectangle(bounds);
        }

        public Builder obstacles(List<Obstacle> value) {
            this.obstacles = List.copyOf(value);
            return this;
        }

        public Builder doors(List<Door> value) {
            this.doors = List.copyOf(value);
            return this;
        }

        public Builder keyPickups(List<KeyPickup> value) {
            this.keyPickups = List.copyOf(value);
            return this;
        }

        public Builder spawnArea(Rectangle value) {
            this.spawnArea = new Rectangle(value);
            return this;
        }

        public Room build() {
            return new Room(this);
        }
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

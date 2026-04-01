package fr.supdevinci.games.room;

import com.badlogic.gdx.math.Rectangle;
import fr.supdevinci.games.progress.Inventory;
import fr.supdevinci.games.progress.KeyId;

import java.util.List;
import java.util.Objects;

/**
 * Represents a door in the house: position, state, access requirements.
 * Pure logic, no rendering.
 * Testable independently.
 */
public final class Door {
    private final String id;
    private final Rectangle area;
    private final String label;
    private final String targetRoomId;
    private final List<KeyId> requiredKeys;
    private DoorState state;

    /**
     * Creates an unlocked door (CLOSED state).
     */
    public Door(String id, float x, float y, float width, float height,
                String label, String targetRoomId) {
        this(builder(id, Geometry.of(x, y, width, height), label, targetRoomId));
    }

    public Door(Builder builder) {
        this.id = Objects.requireNonNull(builder.id);
        this.area = new Rectangle(builder.geometry.x, builder.geometry.y, builder.geometry.width, builder.geometry.height);
        this.label = Objects.requireNonNull(builder.label);
        this.targetRoomId = Objects.requireNonNull(builder.targetRoomId);
        this.requiredKeys = List.copyOf(builder.requiredKeys);
        this.state = Objects.requireNonNull(builder.initialState);
    }

    public static Builder builder(String id, Geometry geometry, String label, String targetRoomId) {
        return new Builder(id, geometry, label, targetRoomId);
    }

    public static final class Builder {
        private final String id;
        private final Geometry geometry;
        private final String label;
        private final String targetRoomId;
        private List<KeyId> requiredKeys = List.of();
        private DoorState initialState = DoorState.CLOSED;

        private Builder(String id, Geometry geometry, String label, String targetRoomId) {
            this.id = id;
            this.geometry = geometry;
            this.label = label;
            this.targetRoomId = targetRoomId;
        }

        public Builder requiredKeys(List<KeyId> keys) {
            this.requiredKeys = List.copyOf(keys);
            return this;
        }

        public Builder initialState(DoorState state) {
            this.initialState = state;
            return this;
        }

        public Door build() {
            return new Door(this);
        }
    }

    public static final class Geometry {
        private final float x;
        private final float y;
        private final float width;
        private final float height;

        private Geometry(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public static Geometry of(float x, float y, float width, float height) {
            return new Geometry(x, y, width, height);
        }
    }

    // Getters
    public String getId() {
        return id;
    }

    public Rectangle getArea() {
        return area;
    }

    public String getLabel() {
        return label;
    }

    public String getTargetRoomId() {
        return targetRoomId;
    }

    public List<KeyId> getRequiredKeys() {
        return requiredKeys;
    }

    public DoorState getState() {
        return state;
    }

    // State management
    public void open() {
        this.state = DoorState.OPEN;
    }

    public void close() {
        this.state = DoorState.CLOSED;
    }

    public void lock() {
        this.state = DoorState.LOCKED;
    }

    // Access logic
    /**
     * Checks if player can pass through this door.
     * Takes into account current state and required keys.
     * 
     * @param inventory player's inventory
     * @return true if door can be traversed
     */
    public boolean canPass(Inventory inventory) {
        // If door is open, always passable
        if (state == DoorState.OPEN) {
            return true;
        }
        
        // If no keys required, passable
        if (requiredKeys.isEmpty()) {
            return state != DoorState.LOCKED;
        }
        
        // Otherwise, check if all required keys are present
        return inventory.hasAllKeys(requiredKeys);
    }

    /**
     * Attempts to open the door with the given inventory.
     * Returns the new state and a message.
     * 
     * @param inventory player's inventory
     * @return message describing what happened
     */
    public String attemptOpen(Inventory inventory) {
        if (state == DoorState.OPEN) {
            return label + " est déjà ouverte.";
        }
        
        if (canPass(inventory)) {
            open();
            return label + " s'ouvre.";
        }
        
        long missingCount = requiredKeys.stream()
            .filter(k -> !inventory.hasKey(k))
            .count();
        return label + " est verrouillée — " + missingCount + " clé(s) manquante(s) sur " + requiredKeys.size();
    }
}

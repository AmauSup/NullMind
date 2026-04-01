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
        this(id, x, y, width, height, label, targetRoomId, List.of(), DoorState.CLOSED);
    }

    /**
     * Creates a door with specific requirements.
     * 
     * @param id unique identifier
     * @param x, y, width, height collision box
     * @param label display name
     * @param targetRoomId the room this door leads to
     * @param requiredKeys list of keys needed to open
     * @param initialState initial state (OPEN, CLOSED, or LOCKED)
     */
    public Door(String id, float x, float y, float width, float height,
                String label, String targetRoomId, List<KeyId> requiredKeys,
                DoorState initialState) {
        this.id = Objects.requireNonNull(id);
        this.area = new Rectangle(x, y, width, height);
        this.label = Objects.requireNonNull(label);
        this.targetRoomId = Objects.requireNonNull(targetRoomId);
        this.requiredKeys = List.copyOf(requiredKeys);
        this.state = Objects.requireNonNull(initialState);
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

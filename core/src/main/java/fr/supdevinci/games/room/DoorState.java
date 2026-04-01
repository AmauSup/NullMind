package fr.supdevinci.games.room;

/**
 * Represents the state of a door in the house.
 * Simple enum-based state machine for clarity.
 */
public enum DoorState {
    /**
     * Door is open, player can pass through.
     */
    OPEN("Ouvert"),
    
    /**
     * Door is closed but not locked, player can pass.
     */
    CLOSED("Fermé"),
    
    /**
     * Door is locked, player cannot pass without correct keys.
     */
    LOCKED("Verrouillé");

    private final String displayName;

    DoorState(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isPassable() {
        return this != LOCKED;
    }
}

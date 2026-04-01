package fr.supdevinci.games.progress;

/**
 * Identifies collectible keys used to unlock map transitions.
 */
public enum KeyId {
    HOUSE_KEY("Clé de la Maison"),
    LIBRARY_KEY("Clé de la Bibliothèque"),
    PORT_KEY("Pièce du Port"),
    CEMETERY_KEY("Clé du Cimetière");

    private final String displayName;

    KeyId(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

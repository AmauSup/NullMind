package fr.supdevinci.games.world;

/**
 * Identifies the playable maps of the prototype.
 */
public enum LevelId {
    HUB("Hub / Ruelle"),
    HOUSE("Maison"),
    CELLAR("Cave"),
    LIBRARY("Bibliothèque"),
    PORT("Port"),
    CEMETERY("Cimetière");

    private final String displayName;

    LevelId(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

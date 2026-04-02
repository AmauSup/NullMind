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

    /**
     * Returns the localized display name for this level.
     *
     * @return level display name
     */
    public String getDisplayName() {
        return displayName;
    }
}

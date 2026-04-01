package fr.supdevinci.games.config;

/**
 * Central configuration constants for the game.
 * Updated to support house interior dimensions.
 */
public final class GameConfig {
    // Window & camera
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;

    // World dimensions
    public static final float WORLD_WIDTH = 960f;
    public static final float WORLD_HEIGHT = 540f;

    // Player
    public static final float PLAYER_WIDTH = 28f;
    public static final float PLAYER_HEIGHT = 28f;
    public static final float PLAYER_SPEED = 220f;

    // House interior dimensions
    public static final float HOUSE_ROOM_WIDTH = 300f;
    public static final float HOUSE_ROOM_HEIGHT = 250f;
    
    // Door dimensions (standard)
    public static final float DOOR_WIDTH = 80f;
    public static final float DOOR_HEIGHT = 20f;

    // Key pickup dimensions
    public static final float KEY_PICKUP_WIDTH = 20f;
    public static final float KEY_PICKUP_HEIGHT = 20f;

    private GameConfig() {
    }
}

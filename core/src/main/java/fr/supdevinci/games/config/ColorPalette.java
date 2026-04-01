package fr.supdevinci.games.config;

import com.badlogic.gdx.graphics.Color;

/**
 * Centralizes color definitions for all zones and elements.
 * Supports fallback to placeholder colors if textures are missing.
 * Easy to update for thematic consistency.
 */
public final class ColorPalette {
    
    // House Interior
    public static final Color BEDROOM_PLAYER = Color.valueOf("D4A574");      // Warm beige
    public static final Color BEDROOM_SISTER = Color.valueOf("E8B4D4");      // Soft pink
    public static final Color BEDROOM_PARENTS = Color.valueOf("8B7355");     // Dark brown
    public static final Color KITCHEN = Color.valueOf("F4E4C1");             // Cream
    public static final Color LIVING_ROOM = Color.valueOf("C5A572");         // Sand
    public static final Color CELLAR = Color.valueOf("4A4A4A");              // Dark gray
    public static final Color HALLWAY = Color.valueOf("E5DDD0");             // Light gray

    // Exterior / Hub
    public static final Color HUB_GROUND = Color.valueOf("5A8C3A");          // Grass
    public static final Color HUB_ROAD = Color.valueOf("3A3A3A");            // Asphalt
    public static final Color HUB_SIDEWALK = Color.valueOf("C0C0C0");        // Gray
    public static final Color BUILDING_HOUSE = Color.valueOf("8B4513");      // Saddle brown
    public static final Color BUILDING_LIBRARY = Color.valueOf("2F3E46");    // Dark blue-gray
    public static final Color BUILDING_CEMETERY = Color.valueOf("1F2421");   // Very dark
    public static final Color BUILDING_PORT = Color.valueOf("0F4C75");       // Ocean blue

    // Door states (visual)
    public static final Color DOOR_OPEN = Color.valueOf("00AA00");           // Green
    public static final Color DOOR_CLOSED = Color.valueOf("999999");         // Gray
    public static final Color DOOR_LOCKED = Color.valueOf("AA0000");         // Red

    // Doors details
    public static final Color DOOR_FRAME = Color.valueOf("654321");          // Brown
    public static final Color DOOR_HANDLE = Color.valueOf("FFD700");         // Gold

    // Interactables
    public static final Color BOOK = Color.valueOf("8B5A2B");
    public static final Color BOOK_EXPLORED = Color.valueOf("5E4630");
    public static final Color GRAVE = Color.valueOf("8A8F95");
    public static final Color GRAVE_EXPLORED = Color.valueOf("555B61");

    // Port jump parts
    public static final Color BROKEN_BRIDGE_PART = Color.valueOf("9B7653");

    // UI / Info
    public static final Color TRANSITION_ZONE = Color.valueOf("76ABAE");     // Teal (existing)
    public static final Color OBSTACLE = Color.valueOf("000000");            // Black

    private ColorPalette() {
        // Utility class
    }

    /**
     * Returns a color for a given zone and element type.
     * Can be extended to support asset loading later.
     * 
     * @param zone the zone (e.g., "BEDROOM_PLAYER", "KITCHEN")
     * @param fallback default color if zone not found
     * @return the color or fallback
     */
    public static Color getColor(String zone, Color fallback) {
        try {
            return (Color) ColorPalette.class.getDeclaredField(zone)
                .get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            return fallback;
        }
    }
}

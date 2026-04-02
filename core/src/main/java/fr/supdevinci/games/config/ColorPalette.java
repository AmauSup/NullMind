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
    public static final Color HUB_ROAD = Color.valueOf("3A3A3A");            // Asphalt
    public static final Color HUB_SIDEWALK = Color.valueOf("C0C0C0");        // Gray
    public static final Color BUILDING_HOUSE = Color.valueOf("8B4513");      // Saddle brown
    public static final Color BUILDING_LIBRARY = Color.valueOf("2F3E46");    // Dark blue-gray
    public static final Color BUILDING_CEMETERY = Color.valueOf("1F2421");   // Very dark
    public static final Color BUILDING_PORT = Color.valueOf("0F4C75");       // Ocean blue

    // Door states (visual)
    public static final Color DOOR_OPEN = Color.valueOf("00AA00");           // Green
    public static final Color DOOR_LOCKED = Color.valueOf("AA0000");         // Red

    // Door frame
    public static final Color DOOR_FRAME = Color.valueOf("654321");          // Brown

    // Interactables
    public static final Color BOOK = Color.valueOf("8B5A2B");
    public static final Color BOOK_EXPLORED = Color.valueOf("5E4630");
    public static final Color GRAVE = Color.valueOf("8A8F95");
    public static final Color GRAVE_EXPLORED = Color.valueOf("555B61");

    // Port bridge parts
    public static final Color BRIDGE_DECK = Color.valueOf("8B6940");  // Wooden bridge planks

    // Player / pickups
    public static final Color PLAYER = Color.valueOf("FFD166");
    public static final Color KEY_PICKUP = Color.valueOf("F4D35E");

    // Level background layers
    public static final Color HOUSE_BACKGROUND = Color.valueOf("6E4B37");
    public static final Color HOUSE_MAIN_AREA = Color.valueOf("8A6248");
    public static final Color LIBRARY_BACKGROUND = Color.valueOf("2E241C");
    public static final Color LIBRARY_MAIN_AREA = Color.valueOf("3C2F24");
    public static final Color PORT_DOCK = Color.valueOf("5F4A3B");
    public static final Color PORT_WATER = Color.valueOf("233744");
    public static final Color PORT_PIER = Color.valueOf("8B6A45");
    public static final Color CEMETERY_BACKGROUND = Color.valueOf("2D3430");
    public static final Color CEMETERY_MAIN_AREA = Color.valueOf("3A433E");

    // Obstacles by level
    public static final Color OBSTACLE_HUB = new Color(0f, 0f, 0f, 0.55f);
    public static final Color OBSTACLE_LIBRARY = Color.valueOf("6A4A2A");
    public static final Color OBSTACLE_PORT = Color.valueOf("4D3A2D");
    public static final Color OBSTACLE_CEMETERY = Color.valueOf("757575");
    public static final Color OBSTACLE_HOUSE = Color.valueOf("2B1E17");
    public static final Color OBSTACLE_DEFAULT = new Color(0f, 0f, 0f, 0.4f);

    /**
     * Prevents instantiation of utility class.
     */
    private ColorPalette() {
        // Utility class
    }

}

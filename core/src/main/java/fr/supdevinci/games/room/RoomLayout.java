package fr.supdevinci.games.room;

import com.badlogic.gdx.math.Rectangle;
import fr.supdevinci.games.config.ColorPalette;
import fr.supdevinci.games.progress.KeyId;
import fr.supdevinci.games.world.Obstacle;
import fr.supdevinci.games.world.KeyPickup;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Factory for creating house interior room layouts.
 * 
 * Spatial arrangement:
 * 
 *     [Sister's Room] [Parents' Room]
 *            |               |
 *     [Hallway Center] --- [Kitchen]
 *            |               |
 *     [Player's Room] --- [Living Room]
 *                             |
 *                         [Cellar]
 * 
 * Testable: can verify room positions, doors, obstacles without rendering.
 */
public final class RoomLayout {
    
    // Room dimensions (interior)
    private static final float ROOM_WIDTH = 300f;
    private static final float ROOM_HEIGHT = 250f;
    private static final float HALLWAY_WIDTH = 100f;

    // Grid coordinates (top-left of each room)
    private static final float COL_LEFT = 50f;
    private static final float COL_CENTER = 200f;
    private static final float COL_RIGHT = 350f;
    
    private static final float ROW_TOP = 350f;
    private static final float ROW_MIDDLE = 100f;
    private static final float ROW_BOTTOM = -150f;

    private RoomLayout() {
        // Factory class
    }

    /**
     * Creates all rooms for the house interior.
     * @return map of room ID → Room
     */
    public static Map<String, Room> createHouseRooms() {
        Map<String, Room> rooms = new LinkedHashMap<>();

        // Hallway center (main hub of house)
        rooms.put("hallway", createHallway());

        // Row top: Sister & Parents
        rooms.put("sister_bedroom", createSisterBedroom());
        rooms.put("parents_bedroom", createParentsBedroom());

        // Row middle: Player & Living Room
        rooms.put("player_bedroom", createPlayerBedroom());
        rooms.put("living_room", createLivingRoom());

        // Row bottom: Kitchen
        rooms.put("kitchen", createKitchen());

        // Cellar (bottom-most, special access)
        rooms.put("cellar", createCellar());

        return rooms;
    }

    // Room definitions

    private static Room createHallway() {
        String id = "hallway";
        float x = COL_CENTER;
        float y = ROW_MIDDLE;
        
        // Hallway is narrow connector, few obstacles
        java.util.List<Obstacle> obstacles = java.util.List.of(
            new Obstacle(x + 30f, y + 80f, 40f, 30f)   // small pillar
        );

        // Doors to other rooms
        java.util.List<Door> doors = java.util.List.of(
            new Door("to_player_bd", x + 10f, y + 4f, 50f, 16f, "Vers chambre", "player_bedroom", 
                     java.util.List.of(), DoorState.CLOSED),
            new Door("to_sister_bd", x + 10f, y + ROOM_HEIGHT - 20f, 50f, 16f, "Vers chambre sœur", "sister_bedroom",
                     java.util.List.of(), DoorState.LOCKED),
            new Door("to_parents_bd", x + 90f, y + ROOM_HEIGHT - 20f, 50f, 16f, "Vers chambre parents", "parents_bedroom",
                     java.util.List.of(), DoorState.LOCKED),
            new Door("to_kitchen", x + 90f, y + 4f, 50f, 16f, "Vers cuisine", "kitchen",
                     java.util.List.of(), DoorState.CLOSED),
            new Door("to_living_room", x + (HALLWAY_WIDTH * 1.5f) - 16f, y + 110f, 16f, 50f, "Vers salon", "living_room",
                     java.util.List.of(), DoorState.CLOSED),
            new Door("to_cellar", x + (HALLWAY_WIDTH * 1.5f) - 16f, y + 40f, 16f, 50f, "Vers cave", "cellar",
                     java.util.List.of(KeyId.HOUSE_KEY, KeyId.LIBRARY_KEY, KeyId.PORT_KEY, KeyId.CEMETERY_KEY),
                     DoorState.LOCKED)
        );

        java.util.List<KeyPickup> keys = java.util.List.of();
        Rectangle spawn = new Rectangle(x + 30f, y + 30f, 40f, 40f);

        return new Room(id, "Couloir", "Le centre de la maison. Portes partout.",
                        ColorPalette.HALLWAY, x, y, HALLWAY_WIDTH * 1.5f, ROOM_HEIGHT, 
                        obstacles, doors, keys, spawn);
    }

    private static Room createPlayerBedroom() {
        String id = "player_bedroom";
        float x = COL_LEFT;
        float y = ROW_MIDDLE;

        java.util.List<Obstacle> obstacles = java.util.List.of(
            new Obstacle(x + 50f, y + 80f, 100f, 40f),   // bed
            new Obstacle(x + 200f, y + 100f, 60f, 80f)   // wardrobe
        );

        java.util.List<Door> doors = java.util.List.of(
            new Door("to_hallway", x + ROOM_WIDTH - 16f, y + 110f, 16f, 50f, "Vers couloir", "hallway",
                     java.util.List.of(), DoorState.CLOSED)
        );

        java.util.List<KeyPickup> keys = java.util.List.of(
            new KeyPickup("player_bd_key", x + 250f, y + 50f, 20f, 20f, KeyId.HOUSE_KEY)
        );

        Rectangle spawn = new Rectangle(x + 120f, y + 120f, 40f, 40f);

        return new Room(id, "Ma chambre", "Refuge tranquille. Y a une clé.",
                        ColorPalette.BEDROOM_PLAYER, x, y, ROOM_WIDTH, ROOM_HEIGHT,
                        obstacles, doors, keys, spawn);
    }

    private static Room createSisterBedroom() {
        String id = "sister_bedroom";
        float x = COL_LEFT;
        float y = ROW_TOP;

        java.util.List<Obstacle> obstacles = java.util.List.of(
            new Obstacle(x + 40f, y + 60f, 120f, 60f),   // bed
            new Obstacle(x + 180f, y + 80f, 80f, 100f)   // desk & stuff
        );

        java.util.List<Door> doors = java.util.List.of(
            new Door("to_hallway", x + 120f, y + 4f, 80f, 16f, "Vers couloir", "hallway",
                     java.util.List.of(), DoorState.LOCKED)
        );

        java.util.List<KeyPickup> keys = java.util.List.of();
        Rectangle spawn = new Rectangle(x + 100f, y + 100f, 40f, 40f);

        return new Room(id, "Chambre de ma sœur", "Fermée. Je peux pas entrer.",
                        ColorPalette.BEDROOM_SISTER, x, y, ROOM_WIDTH, ROOM_HEIGHT,
                        obstacles, doors, keys, spawn);
    }

    private static Room createParentsBedroom() {
        String id = "parents_bedroom";
        float x = COL_RIGHT;
        float y = ROW_TOP;

        java.util.List<Obstacle> obstacles = java.util.List.of(
            new Obstacle(x + 40f, y + 60f, 140f, 80f),   // large bed
            new Obstacle(x + 200f, y + 100f, 70f, 100f)  // furniture
        );

        java.util.List<Door> doors = java.util.List.of(
            new Door("to_hallway", x + 120f, y + 4f, 80f, 16f, "Vers couloir", "hallway",
                     java.util.List.of(), DoorState.LOCKED)
        );

        java.util.List<KeyPickup> keys = java.util.List.of();
        Rectangle spawn = new Rectangle(x + 100f, y + 100f, 40f, 40f);

        return new Room(id, "Chambre des parents", "Privée, pas d'accès.",
                        ColorPalette.BEDROOM_PARENTS, x, y, ROOM_WIDTH, ROOM_HEIGHT,
                        obstacles, doors, keys, spawn);
    }

    private static Room createKitchen() {
        String id = "kitchen";
        float x = COL_CENTER;
        float y = ROW_BOTTOM + 100f;

        java.util.List<Obstacle> obstacles = java.util.List.of(
            new Obstacle(x + 40f, y + 100f, 100f, 60f),   // kitchen counter
            new Obstacle(x + 160f, y + 80f, 80f, 40f),    // table
            new Obstacle(x + 200f, y + 30f, 70f, 50f)     // fridge
        );

        java.util.List<Door> doors = java.util.List.of(
            new Door("to_hallway", x + 110f, y + ROOM_HEIGHT - 20f, 80f, 16f, "Vers couloir", "hallway",
                     java.util.List.of(), DoorState.CLOSED),
            new Door("to_living", x + ROOM_WIDTH - 16f, y + 110f, 16f, 50f, "Vers salon", "living_room",
                     java.util.List.of(), DoorState.CLOSED)
        );

        java.util.List<KeyPickup> keys = java.util.List.of(
            new KeyPickup("kitchen_key", x + 240f, y + 140f, 20f, 20f, KeyId.LIBRARY_KEY)
        );

        Rectangle spawn = new Rectangle(x + 80f, y + 60f, 40f, 40f);

        return new Room(id, "Cuisine", "Pièce centrale. Clé de biblio ici.",
                        ColorPalette.KITCHEN, x, y, ROOM_WIDTH, ROOM_HEIGHT,
                        obstacles, doors, keys, spawn);
    }

    private static Room createLivingRoom() {
        String id = "living_room";
        float x = COL_RIGHT;
        float y = ROW_MIDDLE;

        java.util.List<Obstacle> obstacles = java.util.List.of(
            new Obstacle(x + 50f, y + 80f, 120f, 100f),   // sofa
            new Obstacle(x + 180f, y + 60f, 80f, 40f),    // TV / media
            new Obstacle(x + 40f, y + 30f, 60f, 40f)      // small table
        );

        java.util.List<Door> doors = java.util.List.of(
            new Door("to_hallway", x + 4f, y + 120f, 16f, 50f, "Vers couloir", "hallway",
                     java.util.List.of(), DoorState.CLOSED),
            new Door("to_kitchen", x + 4f, y + 40f, 16f, 50f, "Vers cuisine", "kitchen",
                     java.util.List.of(), DoorState.CLOSED),
            new Door("to_cellar", x + 120f, y + 4f, 80f, 16f, "Vers cave", "cellar",
                     java.util.List.of(KeyId.HOUSE_KEY, KeyId.LIBRARY_KEY, KeyId.PORT_KEY, KeyId.CEMETERY_KEY),
                     DoorState.LOCKED)
        );

        java.util.List<KeyPickup> keys = java.util.List.of(
            new KeyPickup("living_room_key", x + 240f, y + 140f, 20f, 20f, KeyId.PORT_KEY)
        );

        Rectangle spawn = new Rectangle(x + 100f, y + 120f, 40f, 40f);

        return new Room(id, "Salon", "Confortable. Pièce du port.",
                        ColorPalette.LIVING_ROOM, x, y, ROOM_WIDTH, ROOM_HEIGHT,
                        obstacles, doors, keys, spawn);
    }

    private static Room createCellar() {
        String id = "cellar";
        float x = COL_CENTER;
        float y = ROW_BOTTOM;

        java.util.List<Obstacle> obstacles = java.util.List.of(
            new Obstacle(x + 20f, y + 80f, 60f, 100f),    // pillar
            new Obstacle(x + 100f, y + 100f, 80f, 40f),   // storage shelf
            new Obstacle(x + 200f, y + 60f, 70f, 80f)     // boxes
        );

        java.util.List<Door> doors = java.util.List.of(
            new Door("to_living", x + 120f, y + ROOM_HEIGHT - 20f, 80f, 16f, "Vers salon", "living_room",
                     java.util.List.of(KeyId.HOUSE_KEY, KeyId.LIBRARY_KEY, KeyId.PORT_KEY, KeyId.CEMETERY_KEY),
                     DoorState.LOCKED)
        );

        java.util.List<KeyPickup> keys = java.util.List.of(
            new KeyPickup("cellar_key", x + 240f, y + 80f, 20f, 20f, KeyId.CEMETERY_KEY)
        );

        Rectangle spawn = new Rectangle(x + 50f, y + 50f, 40f, 40f);

        return new Room(id, "Cave", "Lieu secret. Clé cimetière. FIN du POC.",
                        ColorPalette.CELLAR, x, y, ROOM_WIDTH + 50f, ROOM_HEIGHT,
                        obstacles, doors, keys, spawn);
    }
}

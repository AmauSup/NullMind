package fr.supdevinci.games.room;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import fr.supdevinci.games.config.ColorPalette;
import fr.supdevinci.games.progress.KeyId;
import fr.supdevinci.games.world.KeyPickup;
import fr.supdevinci.games.world.Obstacle;

import java.util.LinkedHashMap;
import java.util.List;
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

    private static final String HALLWAY = "hallway";
    private static final String SISTER_BEDROOM = "sister_bedroom";
    private static final String PARENTS_BEDROOM = "parents_bedroom";
    private static final String PLAYER_BEDROOM = "player_bedroom";
    private static final String LIVING_ROOM = "living_room";
    private static final String KITCHEN = "kitchen";
    private static final String CELLAR = "cellar";

    private static final String TO_HALLWAY = "to_hallway";
    private static final String TO_CELLAR = "to_cellar";
    private static final String VERS_SALON = "Vers salon";
    private static final String VERS_COULOIR = "Vers couloir";
    
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
        rooms.put(HALLWAY, createHallway());

        // Row top: Sister & Parents
        rooms.put(SISTER_BEDROOM, createSisterBedroom());
        rooms.put(PARENTS_BEDROOM, createParentsBedroom());

        // Row middle: Player & Living Room
        rooms.put(PLAYER_BEDROOM, createPlayerBedroom());
        rooms.put(LIVING_ROOM, createLivingRoom());

        // Row bottom: Kitchen
        rooms.put(KITCHEN, createKitchen());

        // Cellar (bottom-most, special access)
        rooms.put(CELLAR, createCellar());

        return rooms;
    }

    // Room definitions

    private static Room createHallway() {
        float x = COL_CENTER;
        float y = ROW_MIDDLE;
        
        // Hallway is narrow connector, few obstacles
        List<Obstacle> obstacles = List.of(
            new Obstacle(x + 30f, y + 80f, 40f, 30f)   // small pillar
        );

        // Doors to other rooms
        List<Door> doors = List.of(
            door("to_player_bd", Door.Geometry.of(x + 10f, y + 4f, 50f, 16f), "Vers chambre", PLAYER_BEDROOM, List.of(), DoorState.CLOSED),
            door("to_sister_bd", Door.Geometry.of(x + 10f, y + ROOM_HEIGHT - 20f, 50f, 16f), "Vers chambre sœur", SISTER_BEDROOM, List.of(), DoorState.LOCKED),
            door("to_parents_bd", Door.Geometry.of(x + 90f, y + ROOM_HEIGHT - 20f, 50f, 16f), "Vers chambre parents", PARENTS_BEDROOM, List.of(), DoorState.LOCKED),
            door("to_kitchen", Door.Geometry.of(x + 90f, y + 4f, 50f, 16f), "Vers cuisine", KITCHEN, List.of(), DoorState.CLOSED),
            door("to_living_room", Door.Geometry.of(x + (HALLWAY_WIDTH * 1.5f) - 16f, y + 110f, 16f, 50f), VERS_SALON, LIVING_ROOM, List.of(), DoorState.CLOSED),
            door(TO_CELLAR, Door.Geometry.of(x + (HALLWAY_WIDTH * 1.5f) - 16f, y + 40f, 16f, 50f), "Vers cave", CELLAR,
                List.of(KeyId.HOUSE_KEY, KeyId.LIBRARY_KEY, KeyId.PORT_KEY, KeyId.CEMETERY_KEY), DoorState.LOCKED)
        );

        List<KeyPickup> keys = List.of();
        Rectangle spawn = new Rectangle(x + 30f, y + 30f, 40f, 40f);

        return roomBuilder(HALLWAY, "Couloir", "Le centre de la maison. Portes partout.", ColorPalette.HALLWAY,
            new Rectangle(x, y, HALLWAY_WIDTH * 1.5f, ROOM_HEIGHT))
            .obstacles(obstacles)
            .doors(doors)
            .keyPickups(keys)
            .spawnArea(spawn)
            .build();
    }

    private static Room createPlayerBedroom() {
        float x = COL_LEFT;
        float y = ROW_MIDDLE;

        List<Obstacle> obstacles = List.of(
            new Obstacle(x + 50f, y + 80f, 100f, 40f),   // bed
            new Obstacle(x + 200f, y + 100f, 60f, 80f)   // wardrobe
        );

        List<Door> doors = List.of(
            door(TO_HALLWAY, Door.Geometry.of(x + ROOM_WIDTH - 16f, y + 110f, 16f, 50f), VERS_COULOIR, HALLWAY, List.of(), DoorState.CLOSED)
        );

        List<KeyPickup> keys = List.of(
            new KeyPickup("player_bd_key", x + 250f, y + 50f, 20f, 20f, KeyId.HOUSE_KEY)
        );

        Rectangle spawn = new Rectangle(x + 120f, y + 120f, 40f, 40f);

        return roomBuilder(PLAYER_BEDROOM, "Ma chambre", "Refuge tranquille. Y a une clé.", ColorPalette.BEDROOM_PLAYER,
            new Rectangle(x, y, ROOM_WIDTH, ROOM_HEIGHT))
            .obstacles(obstacles)
            .doors(doors)
            .keyPickups(keys)
            .spawnArea(spawn)
            .build();
    }

    private static Room createSisterBedroom() {
        float x = COL_LEFT;
        float y = ROW_TOP;

        List<Obstacle> obstacles = List.of(
            new Obstacle(x + 40f, y + 60f, 120f, 60f),   // bed
            new Obstacle(x + 180f, y + 80f, 80f, 100f)   // desk & stuff
        );

        List<Door> doors = List.of(
            door(TO_HALLWAY, Door.Geometry.of(x + 120f, y + 4f, 80f, 16f), VERS_COULOIR, HALLWAY, List.of(), DoorState.LOCKED)
        );

        List<KeyPickup> keys = List.of();
        Rectangle spawn = new Rectangle(x + 100f, y + 100f, 40f, 40f);

        return roomBuilder(SISTER_BEDROOM, "Chambre de ma sœur", "Fermée. Je peux pas entrer.", ColorPalette.BEDROOM_SISTER,
            new Rectangle(x, y, ROOM_WIDTH, ROOM_HEIGHT))
            .obstacles(obstacles)
            .doors(doors)
            .keyPickups(keys)
            .spawnArea(spawn)
            .build();
    }

    private static Room createParentsBedroom() {
        float x = COL_RIGHT;
        float y = ROW_TOP;

        List<Obstacle> obstacles = List.of(
            new Obstacle(x + 40f, y + 60f, 140f, 80f),   // large bed
            new Obstacle(x + 200f, y + 100f, 70f, 100f)  // furniture
        );

        List<Door> doors = List.of(
            door(TO_HALLWAY, Door.Geometry.of(x + 120f, y + 4f, 80f, 16f), VERS_COULOIR, HALLWAY, List.of(), DoorState.LOCKED)
        );

        List<KeyPickup> keys = List.of();
        Rectangle spawn = new Rectangle(x + 100f, y + 100f, 40f, 40f);

        return roomBuilder(PARENTS_BEDROOM, "Chambre des parents", "Privée, pas d'accès.", ColorPalette.BEDROOM_PARENTS,
            new Rectangle(x, y, ROOM_WIDTH, ROOM_HEIGHT))
            .obstacles(obstacles)
            .doors(doors)
            .keyPickups(keys)
            .spawnArea(spawn)
            .build();
    }

    private static Room createKitchen() {
        float x = COL_CENTER;
        float y = ROW_BOTTOM + 100f;

        List<Obstacle> obstacles = List.of(
            new Obstacle(x + 40f, y + 100f, 100f, 60f),   // kitchen counter
            new Obstacle(x + 160f, y + 80f, 80f, 40f),    // table
            new Obstacle(x + 200f, y + 30f, 70f, 50f)     // fridge
        );

        List<Door> doors = List.of(
            door(TO_HALLWAY, Door.Geometry.of(x + 110f, y + ROOM_HEIGHT - 20f, 80f, 16f), VERS_COULOIR, HALLWAY, List.of(), DoorState.CLOSED),
            door("to_living", Door.Geometry.of(x + ROOM_WIDTH - 16f, y + 110f, 16f, 50f), VERS_SALON, LIVING_ROOM, List.of(), DoorState.CLOSED)
        );

        List<KeyPickup> keys = List.of(
            new KeyPickup("kitchen_key", x + 240f, y + 140f, 20f, 20f, KeyId.LIBRARY_KEY)
        );

        Rectangle spawn = new Rectangle(x + 80f, y + 60f, 40f, 40f);

        return roomBuilder(KITCHEN, "Cuisine", "Pièce centrale. Clé de biblio ici.", ColorPalette.KITCHEN,
            new Rectangle(x, y, ROOM_WIDTH, ROOM_HEIGHT))
            .obstacles(obstacles)
            .doors(doors)
            .keyPickups(keys)
            .spawnArea(spawn)
            .build();
    }

    private static Room createLivingRoom() {
        float x = COL_RIGHT;
        float y = ROW_MIDDLE;

        List<Obstacle> obstacles = List.of(
            new Obstacle(x + 50f, y + 80f, 120f, 100f),   // sofa
            new Obstacle(x + 180f, y + 60f, 80f, 40f),    // TV / media
            new Obstacle(x + 40f, y + 30f, 60f, 40f)      // small table
        );

        List<Door> doors = List.of(
            door(TO_HALLWAY, Door.Geometry.of(x + 4f, y + 120f, 16f, 50f), VERS_COULOIR, HALLWAY, List.of(), DoorState.CLOSED),
            door("to_kitchen", Door.Geometry.of(x + 4f, y + 40f, 16f, 50f), "Vers cuisine", KITCHEN, List.of(), DoorState.CLOSED),
            door(TO_CELLAR, Door.Geometry.of(x + 120f, y + 4f, 80f, 16f), "Vers cave", CELLAR,
                List.of(KeyId.HOUSE_KEY, KeyId.LIBRARY_KEY, KeyId.PORT_KEY, KeyId.CEMETERY_KEY), DoorState.LOCKED)
        );

        List<KeyPickup> keys = List.of(
            new KeyPickup("living_room_key", x + 240f, y + 140f, 20f, 20f, KeyId.PORT_KEY)
        );

        Rectangle spawn = new Rectangle(x + 100f, y + 120f, 40f, 40f);

        return roomBuilder(LIVING_ROOM, "Salon", "Confortable. Pièce du port.", ColorPalette.LIVING_ROOM,
            new Rectangle(x, y, ROOM_WIDTH, ROOM_HEIGHT))
            .obstacles(obstacles)
            .doors(doors)
            .keyPickups(keys)
            .spawnArea(spawn)
            .build();
    }

    private static Room createCellar() {
        float x = COL_CENTER;
        float y = ROW_BOTTOM;

        List<Obstacle> obstacles = List.of(
            new Obstacle(x + 20f, y + 80f, 60f, 100f),    // pillar
            new Obstacle(x + 100f, y + 100f, 80f, 40f),   // storage shelf
            new Obstacle(x + 200f, y + 60f, 70f, 80f)     // boxes
        );

        List<Door> doors = List.of(
            door("to_living", Door.Geometry.of(x + 120f, y + ROOM_HEIGHT - 20f, 80f, 16f), VERS_SALON, LIVING_ROOM,
                List.of(KeyId.HOUSE_KEY, KeyId.LIBRARY_KEY, KeyId.PORT_KEY, KeyId.CEMETERY_KEY), DoorState.LOCKED)
        );

        List<KeyPickup> keys = List.of(
            new KeyPickup("cellar_key", x + 240f, y + 80f, 20f, 20f, KeyId.CEMETERY_KEY)
        );

        Rectangle spawn = new Rectangle(x + 50f, y + 50f, 40f, 40f);

        return roomBuilder(CELLAR, "Cave", "Lieu secret. Clé cimetière. FIN du POC.", ColorPalette.CELLAR,
            new Rectangle(x, y, ROOM_WIDTH + 50f, ROOM_HEIGHT))
            .obstacles(obstacles)
            .doors(doors)
            .keyPickups(keys)
            .spawnArea(spawn)
            .build();
    }

    private static Door door(String id, Door.Geometry geometry, String label,
                             String targetRoomId, List<KeyId> requiredKeys, DoorState state) {
        return Door.builder(id, geometry, label, targetRoomId)
            .requiredKeys(requiredKeys)
            .initialState(state)
            .build();
    }

    private static Room.Builder roomBuilder(String id, String name, String description, Color color,
                                            Rectangle bounds) {
        return Room.builder(id, name, description, color, bounds);
    }
}

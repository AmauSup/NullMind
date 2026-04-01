package fr.supdevinci.games.room;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RoomLayout factory.
 * Verifies spatial consistency and door connectivity.
 */
class RoomLayoutTest {
    private static final String HALLWAY = "hallway";
    private static final String PLAYER_BEDROOM = "player_bedroom";
    private static final String LIVING_ROOM = "living_room";
    private static final String CELLAR = "cellar";
    private static final String KITCHEN = "kitchen";
    private static final String SISTER_BEDROOM = "sister_bedroom";
    
    @Test
    void shouldCreateAllSevenRooms() {
        Map<String, Room> rooms = RoomLayout.createHouseRooms();
        
        assertEquals(7, rooms.size());
        assertTrue(rooms.containsKey(HALLWAY));
        assertTrue(rooms.containsKey(PLAYER_BEDROOM));
        assertTrue(rooms.containsKey(SISTER_BEDROOM));
        assertTrue(rooms.containsKey("parents_bedroom"));
        assertTrue(rooms.containsKey(KITCHEN));
        assertTrue(rooms.containsKey(LIVING_ROOM));
        assertTrue(rooms.containsKey(CELLAR));
    }
    
    @Test
    void shouldHavePlayerBedroomWithOneKey() {
        Map<String, Room> rooms = RoomLayout.createHouseRooms();
        Room playerBd = rooms.get(PLAYER_BEDROOM);
        
        assertEquals(1, playerBd.getKeyPickups().size());
    }
    
    @Test
    void shouldHaveCellarWithOneKey() {
        Map<String, Room> rooms = RoomLayout.createHouseRooms();
        Room cellar = rooms.get(CELLAR);
        
        assertEquals(1, cellar.getKeyPickups().size());
    }
    
    @Test
    void shouldHaveCellarLockedBehindFourKeys() {
        Map<String, Room> rooms = RoomLayout.createHouseRooms();
        Room living = rooms.get(LIVING_ROOM);
        
        // Living room should have door to cellar locked by 4 keys
        Door doorToCellar = living.getDoors().stream()
            .filter(d -> d.getTargetRoomId().equals(CELLAR))
            .findFirst()
            .orElse(null);
        
        assertNotNull(doorToCellar);
        assertEquals(4, doorToCellar.getRequiredKeys().size());
    }
    
    @Test
    void shouldHaveHallwayConnectingMultipleRooms() {
        Map<String, Room> rooms = RoomLayout.createHouseRooms();
        Room hallway = rooms.get(HALLWAY);
        
        // Hallway should have at least 5 doors (player_bd, sister, parents, kitchen, living, cellar)
        assertTrue(hallway.getDoors().size() >= 5);
    }
    
    @Test
    void shouldHaveSisterBedroomLocked() {
        Map<String, Room> rooms = RoomLayout.createHouseRooms();
        Room hallway = rooms.get(HALLWAY);
        
        Door doorToSister = hallway.getDoors().stream()
            .filter(d -> d.getTargetRoomId().equals(SISTER_BEDROOM))
            .findFirst()
            .orElse(null);
        
        assertNotNull(doorToSister);
        assertEquals(DoorState.LOCKED, doorToSister.getState());
    }
    
    @Test
    void shouldHaveKitchenAndLivingRoomConnected() {
        Map<String, Room> rooms = RoomLayout.createHouseRooms();
        Room kitchen = rooms.get(KITCHEN);
        
        boolean hasLivingRoomDoor = kitchen.getDoors().stream()
            .anyMatch(d -> d.getTargetRoomId().equals(LIVING_ROOM));
        
        assertTrue(hasLivingRoomDoor);
    }

            @Test
            void playerBedroomDoorShouldBeInsideReachableBounds() {
                Map<String, Room> rooms = RoomLayout.createHouseRooms();
                Room playerBd = rooms.get(PLAYER_BEDROOM);
                Door doorToHallway = playerBd.getDoorTo(HALLWAY);

                assertNotNull(doorToHallway);
                assertTrue(playerBd.getBounds().overlaps(doorToHallway.getArea()));
            }
}

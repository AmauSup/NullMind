package fr.supdevinci.games.room;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RoomLayout factory.
 * Verifies spatial consistency and door connectivity.
 */
class RoomLayoutTest {
    
    @Test
    void shouldCreateAllSevenRooms() {
        Map<String, Room> rooms = RoomLayout.createHouseRooms();
        
        assertEquals(7, rooms.size());
        assertTrue(rooms.containsKey("hallway"));
        assertTrue(rooms.containsKey("player_bedroom"));
        assertTrue(rooms.containsKey("sister_bedroom"));
        assertTrue(rooms.containsKey("parents_bedroom"));
        assertTrue(rooms.containsKey("kitchen"));
        assertTrue(rooms.containsKey("living_room"));
        assertTrue(rooms.containsKey("cellar"));
    }
    
    @Test
    void shouldHavePlayerBedroomWithOneKey() {
        Map<String, Room> rooms = RoomLayout.createHouseRooms();
        Room playerBd = rooms.get("player_bedroom");
        
        assertEquals(1, playerBd.getKeyPickups().size());
    }
    
    @Test
    void shouldHaveCellarWithOneKey() {
        Map<String, Room> rooms = RoomLayout.createHouseRooms();
        Room cellar = rooms.get("cellar");
        
        assertEquals(1, cellar.getKeyPickups().size());
    }
    
    @Test
    void shouldHaveCellarLockedBehindFourKeys() {
        Map<String, Room> rooms = RoomLayout.createHouseRooms();
        Room cellar = rooms.get("cellar");
        Room living = rooms.get("living_room");
        
        // Living room should have door to cellar locked by 4 keys
        Door doorToCellar = living.getDoors().stream()
            .filter(d -> d.getTargetRoomId().equals("cellar"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(doorToCellar);
        assertEquals(4, doorToCellar.getRequiredKeys().size());
    }
    
    @Test
    void shouldHaveHallwayConnectingMultipleRooms() {
        Map<String, Room> rooms = RoomLayout.createHouseRooms();
        Room hallway = rooms.get("hallway");
        
        // Hallway should have at least 5 doors (player_bd, sister, parents, kitchen, living, cellar)
        assertTrue(hallway.getDoors().size() >= 5);
    }
    
    @Test
    void shouldHaveSisterBedroomLocked() {
        Map<String, Room> rooms = RoomLayout.createHouseRooms();
        Room hallway = rooms.get("hallway");
        
        Door doorToSister = hallway.getDoors().stream()
            .filter(d -> d.getTargetRoomId().equals("sister_bedroom"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(doorToSister);
        assertEquals(DoorState.LOCKED, doorToSister.getState());
    }
    
    @Test
    void shouldHaveKitchenAndLivingRoomConnected() {
        Map<String, Room> rooms = RoomLayout.createHouseRooms();
        Room kitchen = rooms.get("kitchen");
        
        boolean hasLivingRoomDoor = kitchen.getDoors().stream()
            .anyMatch(d -> d.getTargetRoomId().equals("living_room"));
        
        assertTrue(hasLivingRoomDoor);
    }

            @Test
            void playerBedroomDoorShouldBeInsideReachableBounds() {
                Map<String, Room> rooms = RoomLayout.createHouseRooms();
                Room playerBd = rooms.get("player_bedroom");
                Door doorToHallway = playerBd.getDoorTo("hallway");

                assertNotNull(doorToHallway);
                assertTrue(playerBd.getBounds().overlaps(doorToHallway.getArea()));
            }
}

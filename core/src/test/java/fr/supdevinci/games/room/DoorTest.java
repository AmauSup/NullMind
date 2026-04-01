package fr.supdevinci.games.room;

import fr.supdevinci.games.progress.Inventory;
import fr.supdevinci.games.progress.KeyId;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Door state and access logic.
 */
class DoorTest {
    private static final String LABEL = "Test Door";
    private static final String TARGET_ROOM = "room2";

    private static Door createDoor(String id, List<KeyId> requiredKeys, DoorState state) {
        return Door.builder(id, Door.Geometry.of(100f, 100f, 80f, 20f), LABEL, TARGET_ROOM)
            .requiredKeys(requiredKeys)
            .initialState(state)
            .build();
    }
    
    @Test
    void shouldAllowPassageWhenDoorIsOpen() {
        Door door = createDoor("door1", List.of(), DoorState.OPEN);
        Inventory empty = new Inventory();
        
        assertTrue(door.canPass(empty));
    }
    
    @Test
    void shouldAllowPassageWhenDoorIsClosedWithoutKeyRequirement() {
        Door door = createDoor("door2", List.of(), DoorState.CLOSED);
        Inventory empty = new Inventory();
        
        assertTrue(door.canPass(empty));
    }
    
    @Test
    void shouldBlockPassageWhenDoorIsLockedAndKeysAreMissing() {
        Door door = createDoor("door3", List.of(KeyId.HOUSE_KEY, KeyId.LIBRARY_KEY), DoorState.LOCKED);
        Inventory empty = new Inventory();
        
        assertFalse(door.canPass(empty));
    }
    
    @Test
    void shouldAllowPassageWhenAllKeysArePresent() {
        Door door = createDoor("door4", List.of(KeyId.HOUSE_KEY, KeyId.LIBRARY_KEY), DoorState.LOCKED);
        Inventory inventory = new Inventory();
        inventory.addKey(KeyId.HOUSE_KEY);
        inventory.addKey(KeyId.LIBRARY_KEY);
        
        assertTrue(door.canPass(inventory));
    }
    
    @Test
    void shouldBlockPassageWhenSomeKeysAreMissing() {
        Door door = createDoor("door5", List.of(KeyId.HOUSE_KEY, KeyId.LIBRARY_KEY, KeyId.PORT_KEY), DoorState.LOCKED);
        Inventory inventory = new Inventory();
        inventory.addKey(KeyId.HOUSE_KEY);
        
        assertFalse(door.canPass(inventory));
    }
    
    @Test
    void shouldTransitionStateWhenOpened() {
        Door door = createDoor("door6", List.of(), DoorState.CLOSED);
        
        assertEquals(DoorState.CLOSED, door.getState());
        door.open();
        assertEquals(DoorState.OPEN, door.getState());
    }
    
    @Test
    void shouldProvideCorrectMessageWhenBlockedByMissingKeys() {
        Door door = Door.builder("door7", Door.Geometry.of(100f, 100f, 80f, 20f), "My Door", TARGET_ROOM)
            .requiredKeys(List.of(KeyId.HOUSE_KEY, KeyId.LIBRARY_KEY, KeyId.PORT_KEY))
            .initialState(DoorState.LOCKED)
            .build();
        Inventory inventory = new Inventory();
        inventory.addKey(KeyId.HOUSE_KEY);
        
        String message = door.attemptOpen(inventory);
        
        assertTrue(message.contains("verrouillée"));
        assertTrue(message.contains("2"));  // 2 keys missing
        assertTrue(message.contains("3"));  // out of 3
    }
}

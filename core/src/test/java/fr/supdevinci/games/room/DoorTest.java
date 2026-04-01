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
    
    @Test
    void shouldAllowPassageWhenDoorIsOpen() {
        Door door = new Door("door1", 100, 100, 80, 20, "Test Door", "room2", 
                            List.of(), DoorState.OPEN);
        Inventory empty = new Inventory();
        
        assertTrue(door.canPass(empty));
    }
    
    @Test
    void shouldAllowPassageWhenDoorIsClosedWithoutKeyRequirement() {
        Door door = new Door("door2", 100, 100, 80, 20, "Test Door", "room2",
                            List.of(), DoorState.CLOSED);
        Inventory empty = new Inventory();
        
        assertTrue(door.canPass(empty));
    }
    
    @Test
    void shouldBlockPassageWhenDoorIsLockedAndKeysAreMissing() {
        Door door = new Door("door3", 100, 100, 80, 20, "Test Door", "room2",
                            List.of(KeyId.HOUSE_KEY, KeyId.LIBRARY_KEY), DoorState.LOCKED);
        Inventory empty = new Inventory();
        
        assertFalse(door.canPass(empty));
    }
    
    @Test
    void shouldAllowPassageWhenAllKeysArePresent() {
        Door door = new Door("door4", 100, 100, 80, 20, "Test Door", "room2",
                            List.of(KeyId.HOUSE_KEY, KeyId.LIBRARY_KEY), DoorState.LOCKED);
        Inventory inventory = new Inventory();
        inventory.addKey(KeyId.HOUSE_KEY);
        inventory.addKey(KeyId.LIBRARY_KEY);
        
        assertTrue(door.canPass(inventory));
    }
    
    @Test
    void shouldBlockPassageWhenSomeKeysAreMissing() {
        Door door = new Door("door5", 100, 100, 80, 20, "Test Door", "room2",
                            List.of(KeyId.HOUSE_KEY, KeyId.LIBRARY_KEY, KeyId.PORT_KEY), DoorState.LOCKED);
        Inventory inventory = new Inventory();
        inventory.addKey(KeyId.HOUSE_KEY);
        
        assertFalse(door.canPass(inventory));
    }
    
    @Test
    void shouldTransitionStateWhenOpened() {
        Door door = new Door("door6", 100, 100, 80, 20, "Test Door", "room2",
                            List.of(), DoorState.CLOSED);
        
        assertEquals(DoorState.CLOSED, door.getState());
        door.open();
        assertEquals(DoorState.OPEN, door.getState());
    }
    
    @Test
    void shouldProvideCorrectMessageWhenBlockedByMissingKeys() {
        Door door = new Door("door7", 100, 100, 80, 20, "My Door", "room2",
                            List.of(KeyId.HOUSE_KEY, KeyId.LIBRARY_KEY, KeyId.PORT_KEY), DoorState.LOCKED);
        Inventory inventory = new Inventory();
        inventory.addKey(KeyId.HOUSE_KEY);
        
        String message = door.attemptOpen(inventory);
        
        assertTrue(message.contains("verrouillée"));
        assertTrue(message.contains("2"));  // 2 keys missing
        assertTrue(message.contains("3"));  // out of 3
    }
}

package fr.supdevinci.games.progress;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InventoryTest {
    @Test
    void shouldAddKeyOnce() {
        Inventory inventory = new Inventory();

        assertTrue(inventory.addKey(KeyId.HOUSE_KEY));
        assertFalse(inventory.addKey(KeyId.HOUSE_KEY));
        assertTrue(inventory.hasKey(KeyId.HOUSE_KEY));
    }
}

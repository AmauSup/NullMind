package fr.supdevinci.games.progress;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void shouldValidateRequiredKeysSet() {
        Inventory inventory = new Inventory();
        inventory.addKey(KeyId.HOUSE_KEY);
        inventory.addKey(KeyId.PORT_KEY);

        assertTrue(inventory.hasAllKeys(List.of(KeyId.HOUSE_KEY, KeyId.PORT_KEY)));
        assertFalse(inventory.hasAllKeys(List.of(KeyId.HOUSE_KEY, KeyId.CEMETERY_KEY)));
    }

    @Test
    void shouldReturnFormattedKeysAndCount() {
        Inventory inventory = new Inventory();
        assertEquals(0, inventory.getKeyCount());
        assertEquals("", inventory.getFormattedKeys());

        inventory.addKey(KeyId.HOUSE_KEY);
        inventory.addKey(KeyId.PORT_KEY);

        assertEquals(2, inventory.getKeyCount());
        assertEquals("Clé de la Maison, Pièce du Port", inventory.getFormattedKeys());
    }
}

package fr.supdevinci.games.world.screamer;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultScreamerManagerFactoryTest {
    @Test
    void shouldCreateIndependentManagers() {
        DefaultScreamerManagerFactory factory = new DefaultScreamerManagerFactory(1f, 1f, new Random(0));

        ScreamerManager first = factory.create();
        ScreamerManager second = factory.create();

        assertNotNull(first);
        assertNotNull(second);
        assertFalse(first == second);
    }

    @Test
    void shouldPropagateInvalidDurationAsException() {
        DefaultScreamerManagerFactory factory = new DefaultScreamerManagerFactory(0.5f, 0f, new Random(0));

        assertThrows(IllegalArgumentException.class, factory::create);
    }
}

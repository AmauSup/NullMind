package fr.supdevinci.games.world.screamer;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the screamer subsystem.
 * All tests use deterministic triggers (lambdas) to avoid random behaviour.
 */
class ScreamerManagerTest {

    // ──────────────────────────────────────────────
    // ScreamerManager — lifecycle
    // ──────────────────────────────────────────────

    @Test
    void shouldBeInactiveByDefault() {
        ScreamerManager manager = new ScreamerManager(() -> true, 3f);

        assertFalse(manager.isActive());
    }

    @Test
    void shouldActivateWhenTriggerFires() {
        ScreamerManager manager = new ScreamerManager(() -> true, 3f);

        manager.tryActivate();

        assertTrue(manager.isActive());
    }

    @Test
    void shouldNotActivateWhenTriggerDoesNotFire() {
        ScreamerManager manager = new ScreamerManager(() -> false, 3f);

        manager.tryActivate();

        assertFalse(manager.isActive());
    }

    @Test
    void shouldDeactivateAfterDisplayDurationElapses() {
        ScreamerManager manager = new ScreamerManager(() -> true, 2f);
        manager.tryActivate();

        manager.update(2.1f); // exceed duration

        assertFalse(manager.isActive());
    }

    @Test
    void shouldStillBeActiveBeforeDurationElapses() {
        ScreamerManager manager = new ScreamerManager(() -> true, 3f);
        manager.tryActivate();

        manager.update(1.5f); // half duration

        assertTrue(manager.isActive());
    }

    @Test
    void shouldNotActivateAgainWhileAlreadyActive() {
        ScreamerManager manager = new ScreamerManager(() -> true, 3f);
        manager.tryActivate();
        manager.update(1f); // 2 seconds remaining
        manager.tryActivate(); // second call — should be ignored

        assertTrue(manager.isActive());
        assertEquals(2f, manager.getRemainingTime(), 0.001f);
    }

    @Test
    void shouldRejectNonPositiveDuration() {
        assertThrows(IllegalArgumentException.class, () -> new ScreamerManager(() -> true, 0f));
        assertThrows(IllegalArgumentException.class, () -> new ScreamerManager(() -> true, -1f));
    }

    // ──────────────────────────────────────────────
    // RandomScreamerTrigger
    // ──────────────────────────────────────────────

    @Test
    void alwaysFiresTriggerShouldAlwaysFire() {
        // Seed 0 with probability 1.0 always returns true
        RandomScreamerTrigger trigger = new RandomScreamerTrigger(1.0f, new Random(0));

        assertTrue(trigger.shouldFire());
    }

    @Test
    void neverFiresTriggerShouldNeverFire() {
        RandomScreamerTrigger trigger = new RandomScreamerTrigger(0.0f, new Random(0));

        assertFalse(trigger.shouldFire());
    }

    @Test
    void shouldRejectOutOfRangeProbability() {
        assertThrows(IllegalArgumentException.class, () -> new RandomScreamerTrigger(-0.1f, new Random()));
        assertThrows(IllegalArgumentException.class, () -> new RandomScreamerTrigger(1.1f, new Random()));
    }
}

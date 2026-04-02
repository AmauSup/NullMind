package fr.supdevinci.games.world;

import fr.supdevinci.games.config.GameConstants;
import fr.supdevinci.games.logic.MovementIntent;
import fr.supdevinci.games.logic.PlayerMovementService;
import fr.supdevinci.games.progress.KeyId;
import fr.supdevinci.games.world.screamer.ScreamerManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the port level mechanics with the new bridge layout.
 *
 * <p>The port key is located on the bridge (y ≈ 422–448) which is accessible
 * from the dock via lateral approach corridors (x=0-120 and x=840-960).</p>
 */
class PortMechanicsTest {
    private GameWorld gameWorld;

    @BeforeEach
    void setUp() {
        // Never-firing screamer trigger: deterministic tests
        ScreamerManager neverScream = new ScreamerManager(() -> false, 3f);
        gameWorld = new GameWorld(LevelCatalog.createDefault(), new PlayerMovementService(), neverScream);
        gameWorld.loadLevel(LevelId.PORT, GameConstants.SPAWN_FROM_HUB);
    }

    @Test
    void shouldCollectPortKeyWhenStandingOnPickup() {
        // The port key pickup is at (465, 422, 26, 26) — teleport player onto it
        gameWorld.getPlayer().setPosition(465f, 422f);

        gameWorld.update(new MovementIntent(0f, 0f), 0.016f, false);

        assertTrue(gameWorld.getInventory().hasKey(KeyId.PORT_KEY),
            "Port key should be auto-collected by stepping on the pickup");
    }

    @Test
    void shouldRemainInPortWhenStandingInDockArea() {
        gameWorld.getPlayer().setPosition(470f, 90f);

        gameWorld.update(new MovementIntent(0f, 0f), 0.016f, false);

        assertEquals(LevelId.PORT, gameWorld.getCurrentLevel().getId());
    }

    @Test
    void shouldTransitionToHubWhenWalkingOnExitZone() {
        // Hub exit transition is at (440, 30, 80, 24)
        gameWorld.getPlayer().setPosition(450f, 30f);

        gameWorld.update(new MovementIntent(0f, 0f), 0.016f, false);

        assertEquals(LevelId.HUB, gameWorld.getCurrentLevel().getId());
    }
}

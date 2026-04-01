package fr.supdevinci.games.world;

import fr.supdevinci.games.logic.MovementIntent;
import fr.supdevinci.games.logic.PlayerMovementService;
import fr.supdevinci.games.progress.KeyId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameWorldKeyFlowTest {
    private static final String FROM_HUB = "fromHub";
    private static final String START = "start";

    private static GameWorld freshWorld() {
        return new GameWorld(LevelCatalog.createDefault(), new PlayerMovementService());
    }

    @Test
    void shouldPhysicallyBlockCaveDoorWhenLocked() {
        GameWorld gameWorld = freshWorld();

        // Place player just above locked cave door and try to move downward through it.
        gameWorld.getPlayer().setPosition(470f, 90f);
        gameWorld.update(new MovementIntent(0f, -1f), 0.20f);

        // Door collision blocks movement while keys are missing.
        assertTrue(gameWorld.getPlayer().getY() >= 90f);
        assertEquals(LevelId.HOUSE, gameWorld.getCurrentLevel().getId());
    }

    @Test
    void shouldBlockCaveWhenMissingAllKeys() {
        GameWorld gameWorld = freshWorld();
        // Porte cave en bas de la maison.
        gameWorld.getPlayer().setPosition(470f, 62f);
        gameWorld.update(new MovementIntent(0f, 0f), 0.016f);

        assertEquals(LevelId.HOUSE, gameWorld.getCurrentLevel().getId());
        assertTrue(gameWorld.getLastStatusMessage().contains("verrouillée"));
    }

    @Test
    void shouldEnterCaveWhenThreeRequiredKeysCollected() {
        GameWorld gameWorld = freshWorld();

        // Bibliothèque
        gameWorld.loadLevel(LevelId.LIBRARY, FROM_HUB);
        gameWorld.getPlayer().setPosition(760f, 290f);
        gameWorld.update(new MovementIntent(0f, 0f), 0.016f);
        assertTrue(gameWorld.getInventory().hasKey(KeyId.LIBRARY_KEY));

        // Cimetière
        gameWorld.loadLevel(LevelId.CEMETERY, FROM_HUB);
        gameWorld.getPlayer().setPosition(790f, 320f);
        gameWorld.update(new MovementIntent(0f, 0f), 0.016f);
        assertTrue(gameWorld.getInventory().hasKey(KeyId.CEMETERY_KEY));

        // Port
        gameWorld.loadLevel(LevelId.PORT, FROM_HUB);
        gameWorld.getPlayer().setPosition(470f, 320f);
        gameWorld.update(new MovementIntent(0f, 0f), 0.016f);
        assertTrue(gameWorld.getInventory().hasKey(KeyId.PORT_KEY));

        // Retour maison, porte cave en bas
        gameWorld.loadLevel(LevelId.HOUSE, START);
        gameWorld.getPlayer().setPosition(470f, 62f);
        gameWorld.update(new MovementIntent(0f, 0f), 0.016f);
        assertEquals(LevelId.CELLAR, gameWorld.getCurrentLevel().getId());
    }
}

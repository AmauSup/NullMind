package fr.supdevinci.games.world;

import fr.supdevinci.games.logic.MovementIntent;
import fr.supdevinci.games.logic.PlayerMovementService;
import fr.supdevinci.games.progress.KeyId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameWorldKeyFlowTest {

    private static GameWorld freshWorld() {
        return new GameWorld(LevelCatalog.createDefault(), new PlayerMovementService());
    }

    @Test
    void shouldPhysicallyBlockCaveDoorWhenLocked() {
        GameWorld gameWorld = freshWorld();

        // Place player just below locked cave door and try to move upward through it.
        gameWorld.getPlayer().setPosition(470f, 470f);
        gameWorld.update(new MovementIntent(0f, 1f), 0.20f);

        // Door collision blocks movement while keys are missing.
        assertTrue(gameWorld.getPlayer().getY() <= 470f);
        assertEquals(LevelId.HOUSE, gameWorld.getCurrentLevel().getId());
    }

    @Test
    void shouldBlockCaveWhenMissingAllKeys() {
        GameWorld gameWorld = freshWorld();
        // Porte cave en haut de la maison.
        gameWorld.getPlayer().setPosition(470f, 502f);
        gameWorld.update(new MovementIntent(0f, 0f), 0.016f);

        assertEquals(LevelId.HOUSE, gameWorld.getCurrentLevel().getId());
        assertTrue(gameWorld.getLastStatusMessage().contains("verrouillée"));
    }

    @Test
    void shouldEnterCaveWhenThreeRequiredKeysCollected() {
        GameWorld gameWorld = freshWorld();

        // Bibliothèque
        gameWorld.loadLevel(LevelId.LIBRARY, "fromHub");
        gameWorld.getPlayer().setPosition(760f, 290f);
        gameWorld.update(new MovementIntent(0f, 0f), 0.016f);
        assertTrue(gameWorld.getInventory().hasKey(KeyId.LIBRARY_KEY));

        // Cimetière
        gameWorld.loadLevel(LevelId.CEMETERY, "fromHub");
        gameWorld.getPlayer().setPosition(790f, 320f);
        gameWorld.update(new MovementIntent(0f, 0f), 0.016f);
        assertTrue(gameWorld.getInventory().hasKey(KeyId.CEMETERY_KEY));

        // Port
        gameWorld.loadLevel(LevelId.PORT, "fromHub");
        gameWorld.getPlayer().setPosition(470f, 320f);
        gameWorld.update(new MovementIntent(0f, 0f), 0.016f);
        assertTrue(gameWorld.getInventory().hasKey(KeyId.PORT_KEY));

        // Retour maison, porte cave en haut
        gameWorld.loadLevel(LevelId.HOUSE, "start");
        gameWorld.getPlayer().setPosition(470f, 502f);
        gameWorld.update(new MovementIntent(0f, 0f), 0.016f);
        assertEquals(LevelId.CELLAR, gameWorld.getCurrentLevel().getId());
    }
}

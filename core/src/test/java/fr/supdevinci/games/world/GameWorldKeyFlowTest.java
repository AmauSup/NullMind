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
        gameWorld.update(new MovementIntent(0f, -1f), 0.20f, false);

        // Door collision blocks movement while keys are missing.
        assertTrue(gameWorld.getPlayer().getY() >= 90f);
        assertEquals(LevelId.HOUSE, gameWorld.getCurrentLevel().getId());
    }

    @Test
    void shouldBlockCaveWhenMissingAllKeys() {
        GameWorld gameWorld = freshWorld();
        // Porte cave en bas de la maison.
        gameWorld.getPlayer().setPosition(470f, 62f);
        gameWorld.update(new MovementIntent(0f, 0f), 0.016f, false);

        assertEquals(LevelId.HOUSE, gameWorld.getCurrentLevel().getId());
        assertTrue(gameWorld.getLastStatusMessage().contains("verrouillée"));
    }

    @Test
    void shouldEnterCaveWhenThreeRequiredKeysCollected() {
        GameWorld gameWorld = freshWorld();

        // Bibliothèque : la clé est dans book_3 (interactable, E requis)
        // book_3 est à (370f, 230f, 34f, 40f), donc on se place juste à côté
        gameWorld.loadLevel(LevelId.LIBRARY, FROM_HUB);
        gameWorld.getPlayer().setPosition(370f, 220f);
        gameWorld.update(new MovementIntent(0f, 0f), 0.016f, true);
        assertTrue(gameWorld.getInventory().hasKey(KeyId.LIBRARY_KEY),
            "Library key should be collected after pressing E near book_3");

        // Cimetière : la clé est dans grave_4 (interactable, E requis)
        // grave_4 est à (700f, 280f, 40f, 28f), donc on se place juste à côté
        gameWorld.loadLevel(LevelId.CEMETERY, FROM_HUB);
        gameWorld.getPlayer().setPosition(700f, 270f);
        gameWorld.update(new MovementIntent(0f, 0f), 0.016f, true);
        assertTrue(gameWorld.getInventory().hasKey(KeyId.CEMETERY_KEY),
            "Cemetery key should be collected after pressing E near grave_4");

        // Port : la clé est un pickup automatique (pas d'interactable)
        gameWorld.loadLevel(LevelId.PORT, FROM_HUB);
            gameWorld.getPlayer().setPosition(465f, 430f);
            gameWorld.update(new MovementIntent(0f, 0f), 0.016f, false);
        assertTrue(gameWorld.getInventory().hasKey(KeyId.PORT_KEY),
            "Port key should be auto-collected by walking on the pickup zone");

        // Retour maison, porte cave en bas
        gameWorld.loadLevel(LevelId.HOUSE, START);
        gameWorld.getPlayer().setPosition(470f, 62f);
            gameWorld.update(new MovementIntent(0f, 0f), 0.016f, false);
        assertEquals(LevelId.CELLAR, gameWorld.getCurrentLevel().getId());
    }
}

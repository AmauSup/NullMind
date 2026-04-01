package fr.supdevinci.games.world;

import fr.supdevinci.games.logic.MovementIntent;
import fr.supdevinci.games.logic.PlayerMovementService;
import fr.supdevinci.games.progress.KeyId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameWorldTest {
    @Test
    void shouldStartInHubAndTransitionToHouse() {
        GameWorld gameWorld = new GameWorld(LevelCatalog.createDefault(), new PlayerMovementService());

        gameWorld.loadLevel(LevelId.HUB, "start");
        gameWorld.getPlayer().setPosition(460f, 122f);
        gameWorld.update(new MovementIntent(0f, 0f), 0.016f, false, false);

        assertEquals(LevelId.HOUSE, gameWorld.getCurrentLevel().getId());
        assertEquals(470f, gameWorld.getPlayer().getX());
        // Door positions were inverted: house entrance from hub is now at top spawn.
        assertEquals(430f, gameWorld.getPlayer().getY());
    }

    @Test
    void shouldCollectKeyInLibrary() {
        GameWorld gameWorld = new GameWorld(LevelCatalog.createDefault(), new PlayerMovementService());
        gameWorld.loadLevel(LevelId.LIBRARY, "fromHub");
        // Library key is hidden in book_3 and requires interaction (E).
        gameWorld.getPlayer().setPosition(370f, 220f);
        gameWorld.update(new MovementIntent(0f, 0f), 0.016f, true, false);

        assertTrue(gameWorld.getInventory().hasKey(KeyId.LIBRARY_KEY));
    }
}

package fr.supdevinci.games.world;

import fr.supdevinci.games.config.GameConstants;
import fr.supdevinci.games.logic.MovementIntent;
import fr.supdevinci.games.logic.PlayerMovementService;
import fr.supdevinci.games.progress.KeyId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InteractionMechanicsTest {
    private static final String FROM_HUB = "fromHub";

    private static GameWorld freshWorld() {
        return new GameWorld(LevelCatalog.createDefault(), new PlayerMovementService());
    }

    @Test
    void shouldReportNothingToInspectWhenNoInteractableInRange() {
        GameWorld gameWorld = freshWorld();
        gameWorld.loadLevel(LevelId.LIBRARY, FROM_HUB);

        gameWorld.getPlayer().setPosition(840f, 260f);
        gameWorld.update(new MovementIntent(0f, 0f), 0.016f, true, false);

        assertEquals(GameConstants.MSG_NOTHING_TO_INSPECT, gameWorld.getLastStatusMessage());
    }

    @Test
    void shouldCollectHiddenKeyWhenInteractingNearBook() {
        GameWorld gameWorld = freshWorld();
        gameWorld.loadLevel(LevelId.LIBRARY, FROM_HUB);

        gameWorld.getPlayer().setPosition(370f, 220f);
        gameWorld.update(new MovementIntent(0f, 0f), 0.016f, true, false);

        assertTrue(gameWorld.getInventory().hasKey(KeyId.LIBRARY_KEY));
        assertEquals(GameConstants.MSG_KEY_FOUND + KeyId.LIBRARY_KEY.getDisplayName(), gameWorld.getLastStatusMessage());
    }

    @Test
    void shouldMarkInteractableAsAlreadyExploredOnSecondInteraction() {
        GameWorld gameWorld = freshWorld();
        gameWorld.loadLevel(LevelId.LIBRARY, FROM_HUB);

        gameWorld.getPlayer().setPosition(210f, 110f);
        gameWorld.update(new MovementIntent(0f, 0f), 0.016f, true, false);
        gameWorld.update(new MovementIntent(0f, 0f), 0.016f, true, false);

        assertEquals(GameConstants.MSG_ALREADY_EXPLORED, gameWorld.getLastStatusMessage());
    }
}

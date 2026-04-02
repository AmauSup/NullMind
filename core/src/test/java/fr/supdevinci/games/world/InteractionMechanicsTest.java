package fr.supdevinci.games.world;

import fr.supdevinci.games.config.GameConstants;
import fr.supdevinci.games.logic.MovementIntent;
import fr.supdevinci.games.logic.PlayerMovementService;
import fr.supdevinci.games.progress.KeyId;
import fr.supdevinci.games.world.screamer.ScreamerManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InteractionMechanicsTest {
    private static final String FROM_HUB = "fromHub";

    private static GameWorld freshWorld() {
        return new GameWorld(LevelCatalog.createDefault(), new PlayerMovementService());
    }

    private static GameWorld freshWorldWithScreamer(ScreamerManager screamerManager) {
        return new GameWorld(LevelCatalog.createDefault(), new PlayerMovementService(), screamerManager);
    }

    @Test
    void shouldReportNothingToInspectWhenNoInteractableInRange() {
        GameWorld gameWorld = freshWorld();
        gameWorld.loadLevel(LevelId.LIBRARY, FROM_HUB);

        gameWorld.getPlayer().setPosition(840f, 260f);
        gameWorld.update(new MovementIntent(0f, 0f), 0.016f, true);

        assertEquals(GameConstants.MSG_NOTHING_TO_INSPECT, gameWorld.getLastStatusMessage());
    }

    @Test
    void shouldCollectHiddenKeyWhenInteractingNearBook() {
        GameWorld gameWorld = freshWorld();
        gameWorld.loadLevel(LevelId.LIBRARY, FROM_HUB);

        gameWorld.getPlayer().setPosition(370f, 220f);
        gameWorld.update(new MovementIntent(0f, 0f), 0.016f, true);

        assertTrue(gameWorld.getInventory().hasKey(KeyId.LIBRARY_KEY));
        assertEquals(GameConstants.MSG_KEY_FOUND + KeyId.LIBRARY_KEY.getDisplayName(), gameWorld.getLastStatusMessage());
    }

    @Test
    void shouldMarkInteractableAsAlreadyExploredOnSecondInteraction() {
        GameWorld gameWorld = freshWorld();
        gameWorld.loadLevel(LevelId.LIBRARY, FROM_HUB);

        gameWorld.getPlayer().setPosition(210f, 110f);
        gameWorld.update(new MovementIntent(0f, 0f), 0.016f, true);
        gameWorld.update(new MovementIntent(0f, 0f), 0.016f, true);

        assertEquals(GameConstants.MSG_ALREADY_EXPLORED, gameWorld.getLastStatusMessage());
    }

    @Test
    void shouldTriggerScreamerAtMostOnceInSameLevel() {
        ScreamerManager alwaysTrigger = new ScreamerManager(() -> true, 0.5f);
        GameWorld gameWorld = freshWorldWithScreamer(alwaysTrigger);
        gameWorld.loadLevel(LevelId.LIBRARY, FROM_HUB);

        // First searchable book -> screamer triggers
        gameWorld.getPlayer().setPosition(210f, 110f);
        gameWorld.update(new MovementIntent(0f, 0f), 0.016f, true);
        assertTrue(gameWorld.getScreamerManager().isActive());

        // Let it expire
        gameWorld.update(new MovementIntent(0f, 0f), 1f, false);
        assertTrue(!gameWorld.getScreamerManager().isActive());

        // Second different book in the same room should NOT trigger again
        gameWorld.getPlayer().setPosition(290f, 170f);
        gameWorld.update(new MovementIntent(0f, 0f), 0.016f, true);

        assertTrue(!gameWorld.getScreamerManager().isActive());
    }
}

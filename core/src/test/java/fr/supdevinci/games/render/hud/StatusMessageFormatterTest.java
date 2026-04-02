package fr.supdevinci.games.render.hud;

import fr.supdevinci.games.logic.MovementIntent;
import fr.supdevinci.games.logic.PlayerMovementService;
import fr.supdevinci.games.world.GameWorld;
import fr.supdevinci.games.world.LevelCatalog;
import fr.supdevinci.games.world.LevelId;
import fr.supdevinci.games.world.screamer.ScreamerManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatusMessageFormatterTest {
    private static GameWorld freshWorld() {
        ScreamerManager neverScream = new ScreamerManager(() -> false, 3f);
        return new GameWorld(LevelCatalog.createDefault(), new PlayerMovementService(), neverScream);
    }

    private static StatusMessageFormatter formatterChain() {
        return new LevelPrefixedStatusDecorator(
            new EmptyStatusFallbackDecorator(new BaseStatusMessageFormatter())
        );
    }

    @Test
    void shouldProvideFallbackWhenStatusIsEmpty() {
        GameWorld gameWorld = freshWorld();

        String formatted = formatterChain().format(gameWorld);

        assertEquals("[Maison] Aucun événement.", formatted);
    }

    @Test
    void shouldPrefixLevelNameWhenStatusExists() {
        GameWorld gameWorld = freshWorld();
        gameWorld.loadLevel(LevelId.LIBRARY, "fromHub");
        gameWorld.getPlayer().setPosition(840f, 260f);
        gameWorld.update(new MovementIntent(0f, 0f), 0.016f, true);

        String formatted = formatterChain().format(gameWorld);

        assertTrue(formatted.startsWith("[Bibliothèque] "));
    }
}

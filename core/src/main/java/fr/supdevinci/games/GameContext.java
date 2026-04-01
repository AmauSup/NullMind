package fr.supdevinci.games;

import fr.supdevinci.games.assets.GameAssets;
import fr.supdevinci.games.logic.MovementService;
import fr.supdevinci.games.world.LevelCatalog;

/**
 * Shares the small set of application-wide services needed by screens.
 */
public final class GameContext {
    private final Main game;
    private final GameAssets assets;
    private final LevelCatalog levelCatalog;
    private final MovementService movementService;

    /**
     * Creates a new immutable context.
     *
     * @param game main game instance used to switch screens
     * @param assets shared libGDX resources
     * @param levelCatalog immutable level definitions for the prototype
     * @param movementService movement resolution strategy
     */
    public GameContext(Main game, GameAssets assets, LevelCatalog levelCatalog, MovementService movementService) {
        this.game = game;
        this.assets = assets;
        this.levelCatalog = levelCatalog;
        this.movementService = movementService;
    }

    public Main getGame() {
        return game;
    }

    public GameAssets getAssets() {
        return assets;
    }

    public LevelCatalog getLevelCatalog() {
        return levelCatalog;
    }

    public MovementService getMovementService() {
        return movementService;
    }
}

package fr.supdevinci.games;

import fr.supdevinci.games.assets.GameAssets;
import fr.supdevinci.games.logic.MovementService;
import fr.supdevinci.games.world.LevelCatalog;
import fr.supdevinci.games.world.screamer.ScreamerManagerFactory;

/**
 * Shares the small set of application-wide services needed by screens.
 */
public final class GameContext {
    private final Main game;
    private final GameAssets assets;
    private final LevelCatalog levelCatalog;
    private final MovementService movementService;
    private final ScreamerManagerFactory screamerManagerFactory;

    /**
     * Creates a new immutable context.
     *
     * @param game           main game instance used to switch screens
     * @param assets         shared libGDX resources
     * @param levelCatalog   immutable level definitions for the prototype
     * @param movementService movement resolution strategy
      * @param screamerManagerFactory factory used to create screamer managers
     */
    public GameContext(Main game, GameAssets assets, LevelCatalog levelCatalog,
                              MovementService movementService,
                              ScreamerManagerFactory screamerManagerFactory) {
        this.game = game;
        this.assets = assets;
        this.levelCatalog = levelCatalog;
        this.movementService = movementService;
          this.screamerManagerFactory = screamerManagerFactory;
    }

    /**
     * Returns the main game instance used for screen navigation.
     *
     * @return active {@link Main} instance
     */
    public Main getGame() {
        return game;
    }

    /**
     * Returns shared game assets.
     *
     * @return shared {@link GameAssets}
     */
    public GameAssets getAssets() {
        return assets;
    }

    /**
     * Returns the immutable level catalog.
     *
     * @return current {@link LevelCatalog}
     */
    public LevelCatalog getLevelCatalog() {
        return levelCatalog;
    }

    /**
     * Returns the movement resolution service.
     *
     * @return configured {@link MovementService}
     */
    public MovementService getMovementService() {
        return movementService;
    }

    /**
     * Returns the screamer manager factory.
     *
     * @return configured {@link ScreamerManagerFactory}
     */
    public ScreamerManagerFactory getScreamerManagerFactory() {
        return screamerManagerFactory;
    }
}

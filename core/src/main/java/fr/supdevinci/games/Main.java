package fr.supdevinci.games;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import fr.supdevinci.games.assets.GameAssets;
import fr.supdevinci.games.logic.MovementService;
import fr.supdevinci.games.logic.PlayerMovementService;
import fr.supdevinci.games.screen.GameScreen;
import fr.supdevinci.games.screen.TitleScreen;
import fr.supdevinci.games.world.LevelCatalog;
import fr.supdevinci.games.world.screamer.DefaultScreamerManagerFactory;
import fr.supdevinci.games.world.screamer.ScreamerManagerFactory;

/**
 * Entry point shared by all platforms.
 *
 * <p>The game keeps the lifecycle simple: shared resources are created once here, screens are swapped
 * through dedicated methods, and all libGDX resources are released in {@link #dispose()}.</p>
 */
public class Main extends Game {
    private GameAssets assets;
    private GameContext context;

    /**
     * Initializes shared resources and opens the title screen.
     */
    @Override
    public void create() {
        assets = new GameAssets();
        MovementService movementService = new PlayerMovementService();
        ScreamerManagerFactory screamerManagerFactory = new DefaultScreamerManagerFactory();
        context = new GameContext(
            this,
            assets,
            LevelCatalog.createDefault(),
            movementService,
            screamerManagerFactory
        );
        showTitleScreen();
    }

    /** Displays the title screen. */
    public void showTitleScreen() {
        replaceScreen(new TitleScreen(context));
    }

    /** Starts a fresh playable session on the hub map. */
    public void showGameScreen() {
        replaceScreen(new GameScreen(context));
    }

    /**
     * Disposes the current screen and shared game assets.
     */
    @Override
    public void dispose() {
        super.dispose();
    
        if (assets != null) {
            assets.dispose();
        }
    }

    /**
     * Replaces the active screen and disposes the previous one if present.
     *
     * @param nextScreen screen to activate
     */
    private void replaceScreen(Screen nextScreen) {
        Screen previousScreen = getScreen();
        setScreen(nextScreen);
        if (previousScreen != null) {
            previousScreen.dispose();
        }
    }
}
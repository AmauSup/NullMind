package fr.supdevinci.games;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import fr.supdevinci.games.assets.GameAssets;
import fr.supdevinci.games.logic.MovementService;
import fr.supdevinci.games.logic.PlayerMovementService;
import fr.supdevinci.games.screen.GameScreen;
import fr.supdevinci.games.screen.TitleScreen;
import fr.supdevinci.games.world.LevelCatalog;

/**
 * Entry point shared by all platforms.
 *
 * <p>The game keeps the lifecycle simple: shared resources are created once here, screens are swapped
 * through dedicated methods, and all libGDX resources are released in {@link #dispose()}.</p>
 */
public class Main extends Game {
    private GameAssets assets;
    private GameContext context;

    @Override
    public void create() {
        assets = new GameAssets();
        MovementService movementService = new PlayerMovementService();
        context = new GameContext(this, assets, LevelCatalog.createDefault(), movementService);
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

    @Override
    public void dispose() {
        super.dispose();
        if (assets != null) {
            assets.dispose();
        }
    }

    private void replaceScreen(Screen nextScreen) {
        Screen previousScreen = getScreen();
        setScreen(nextScreen);
        if (previousScreen != null) {
            previousScreen.dispose();
        }
    }
}
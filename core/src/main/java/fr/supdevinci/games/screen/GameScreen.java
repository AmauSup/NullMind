package fr.supdevinci.games.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import fr.supdevinci.games.GameContext;
import fr.supdevinci.games.config.GameConfig;
import fr.supdevinci.games.input.GameInputController;
import fr.supdevinci.games.render.HudRenderer;
import fr.supdevinci.games.render.WorldRenderer;
import fr.supdevinci.games.world.GameWorld;
import fr.supdevinci.games.logic.PlayerMovementService;

/**
 * Main playable screen of the prototype.
 */
public final class GameScreen extends ScreenAdapter {
    private final GameContext context;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final GameInputController inputController;
    private final WorldRenderer worldRenderer;
    private final HudRenderer hudRenderer;
    private final GameWorld gameWorld;

    public GameScreen(GameContext context) {
        this.context = context;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        this.inputController = new GameInputController();
        this.worldRenderer = new WorldRenderer(context.getAssets());
        this.hudRenderer = new HudRenderer(context.getAssets());
        this.gameWorld = new GameWorld(context.getLevelCatalog(), new PlayerMovementService());
        this.camera.position.set(new Vector3(GameConfig.WORLD_WIDTH / 2f, GameConfig.WORLD_HEIGHT / 2f, 0f));
        this.camera.update();
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            context.getGame().showTitleScreen();
            return;
        }

        gameWorld.update(inputController.readMovement(), delta);

        Color backgroundColor = gameWorld.getCurrentLevel().getBackgroundColor();
        ScreenUtils.clear(backgroundColor.r, backgroundColor.g, backgroundColor.b, 1f);
        viewport.apply(true);
        camera.update();
        worldRenderer.render(gameWorld, camera);
        hudRenderer.render(gameWorld, viewport);
    }

    @Override
    public void resize(int width, int height) {
        if (width > 0 && height > 0) {
            viewport.update(width, height, true);
        }
    }
}

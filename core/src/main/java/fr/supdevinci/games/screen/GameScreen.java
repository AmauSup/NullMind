package fr.supdevinci.games.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import fr.supdevinci.games.GameContext;
import fr.supdevinci.games.config.GameConfig;
import fr.supdevinci.games.config.GameConstants;
import fr.supdevinci.games.input.GameInputController;
import fr.supdevinci.games.render.HudRenderer;
import fr.supdevinci.games.render.ScreamerRenderer;
import fr.supdevinci.games.render.WorldRenderer;
import fr.supdevinci.games.world.GameWorld;
import fr.supdevinci.games.world.LevelId;

/**
 * Main playable screen of the prototype.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Poll input and forward to {@link GameWorld#update}</li>
 *   <li>Update camera position to follow the player</li>
 *   <li>Delegate rendering to {@link WorldRenderer}, {@link HudRenderer} and {@link ScreamerRenderer}</li>
 * </ul></p>
 */
public final class GameScreen extends ScreenAdapter {
    private final GameContext context;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final GameInputController inputController;
    private final WorldRenderer worldRenderer;
    private final HudRenderer hudRenderer;
    private final ScreamerRenderer screamerRenderer;
    private final GameWorld gameWorld;
    private float elapsedSeconds;
    private GamePlayState gamePlayState;
    private String finalTimeText;
    private String victoryText;

    /**
     * Creates the gameplay screen and initializes world/render/input components.
     *
     * @param context shared game context
     */
    public GameScreen(GameContext context) {
        this.context = context;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        this.inputController = new GameInputController();
        this.worldRenderer = new WorldRenderer(context.getAssets());
        this.hudRenderer = new HudRenderer(context.getAssets());
        this.screamerRenderer = new ScreamerRenderer(context.getAssets());
        this.gameWorld = new GameWorld(
            context.getLevelCatalog(),
            context.getMovementService(),
            context.getScreamerManagerFactory().create()
        );
        this.elapsedSeconds = 0f;
        this.gamePlayState = GamePlayState.RUNNING;
        this.finalTimeText = "00:00";
        this.victoryText = "";
        this.camera.position.set(new Vector3(GameConfig.WORLD_WIDTH / 2f, GameConfig.WORLD_HEIGHT / 2f, 0f));
        this.camera.update();
    }

    /**
     * Runs one gameplay frame: input, world update, camera update and rendering.
     *
     * @param delta time elapsed since last frame, in seconds
     */
    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            context.getGame().showTitleScreen();
            return;
        }

        updateChronometer(delta);

        gameWorld.update(
            inputController.readMovement(),
            delta,
            inputController.isInteractPressed()
        );

        stopChronometerIfReachedCellar();

        Color backgroundColor = gameWorld.getCurrentLevel().getBackgroundColor();
        ScreenUtils.clear(backgroundColor.r, backgroundColor.g, backgroundColor.b, 1f);
        viewport.apply(true);
        updateCamera(delta);
        camera.update();
        worldRenderer.render(gameWorld, camera);
        hudRenderer.render(gameWorld, viewport, getCurrentTimerText(), victoryText);
        screamerRenderer.render(gameWorld.getScreamerManager(), viewport);
    }

    /**
     * Updates the running timer while the game is not finished.
     *
     * @param delta frame delta time in seconds
     */
    private void updateChronometer(float delta) {
        if (gamePlayState == GamePlayState.RUNNING) {
            elapsedSeconds += delta;
        }
    }

    /**
     * Stops the timer when the player reaches the cellar level and prepares victory text.
     */
    private void stopChronometerIfReachedCellar() {
        if (gamePlayState != GamePlayState.RUNNING || gameWorld.getCurrentLevel().getId() != LevelId.CELLAR) {
            return;
        }
        gamePlayState = GamePlayState.VICTORY;
        finalTimeText = formatElapsedTime(elapsedSeconds);
        victoryText = GameConstants.VICTORY_MESSAGE_PREFIX + finalTimeText;
    }

    /**
     * Returns the timer text to display in the HUD.
     *
     * @return formatted current or final elapsed time
     */
    private String getCurrentTimerText() {
        return gamePlayState == GamePlayState.VICTORY ? finalTimeText : formatElapsedTime(elapsedSeconds);
    }

    /**
     * Converts elapsed seconds to MM:SS format.
     *
     * @param seconds elapsed time value
     * @return formatted string in minutes/seconds format
     */
    private String formatElapsedTime(float seconds) {
        int totalSeconds = Math.max(0, (int) seconds);
        int minutes = totalSeconds / 60;
        int remainingSeconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    /**
     * Smoothly updates camera position to follow the player while clamping to map bounds.
     *
     * @param delta frame delta time in seconds
     */
    private void updateCamera(float delta) {
        var player = gameWorld.getPlayer();
        var level = gameWorld.getCurrentLevel();

        float targetX = player.getX() + (player.getWidth() / 2f);
        float targetY = player.getY() + (player.getHeight() / 2f);

        float minX = viewport.getWorldWidth() / 2f;
        float maxX = level.getWidth() - minX;
        float minY = viewport.getWorldHeight() / 2f;
        float maxY = level.getHeight() - minY;

        targetX = maxX < minX
            ? level.getWidth() / 2f
            : MathUtils.clamp(targetX, minX, maxX);

        targetY = maxY < minY
            ? level.getHeight() / 2f
            : MathUtils.clamp(targetY, minY, maxY);

        float alpha = Math.min(1f, delta * GameConstants.CAMERA_LERP_SPEED);
        camera.position.x = MathUtils.lerp(camera.position.x, targetX, alpha);
        camera.position.y = MathUtils.lerp(camera.position.y, targetY, alpha);
    }

    /**
     * Updates viewport dimensions after window resize.
     *
     * @param width new window width
     * @param height new window height
     */
    @Override
    public void resize(int width, int height) {
        if (width > 0 && height > 0) {
            viewport.update(width, height, true);
        }
    }
}

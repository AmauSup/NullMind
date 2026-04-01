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
import fr.supdevinci.games.render.WorldRenderer;
import fr.supdevinci.games.world.GameWorld;

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
        this.gameWorld = new GameWorld(context.getLevelCatalog(), context.getMovementService());
        this.camera.position.set(new Vector3(GameConfig.WORLD_WIDTH / 2f, GameConfig.WORLD_HEIGHT / 2f, 0f));
        this.camera.update();
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            context.getGame().showTitleScreen();
            return;
        }

        gameWorld.update(
            inputController.readMovement(),
            delta,
            inputController.isInteractPressed(),
            inputController.isJumpPressed()
        );

        Color backgroundColor = gameWorld.getCurrentLevel().getBackgroundColor();
        ScreenUtils.clear(backgroundColor.r, backgroundColor.g, backgroundColor.b, 1f);
        viewport.apply(true);
        updateCamera(delta);
        camera.update();
        worldRenderer.render(gameWorld, camera);
        hudRenderer.render(gameWorld, viewport);
    }

    private void updateCamera(float delta) {
        float targetX = gameWorld.getPlayer().getX() + (gameWorld.getPlayer().getWidth() / 2f);
        float targetY = gameWorld.getPlayer().getY() + (gameWorld.getPlayer().getHeight() / 2f);

        float minX = viewport.getWorldWidth() / 2f;
        float maxX = gameWorld.getCurrentLevel().getWidth() - minX;
        float minY = viewport.getWorldHeight() / 2f;
        float maxY = gameWorld.getCurrentLevel().getHeight() - minY;

        if (maxX < minX) {
            targetX = gameWorld.getCurrentLevel().getWidth() / 2f;
        } else {
            targetX = MathUtils.clamp(targetX, minX, maxX);
        }

        if (maxY < minY) {
            targetY = gameWorld.getCurrentLevel().getHeight() / 2f;
        } else {
            targetY = MathUtils.clamp(targetY, minY, maxY);
        }

        float alpha = Math.min(1f, delta * GameConstants.CAMERA_LERP_SPEED);
        camera.position.x = MathUtils.lerp(camera.position.x, targetX, alpha);
        camera.position.y = MathUtils.lerp(camera.position.y, targetY, alpha);
    }

    @Override
    public void resize(int width, int height) {
        if (width > 0 && height > 0) {
            viewport.update(width, height, true);
        }
    }
}

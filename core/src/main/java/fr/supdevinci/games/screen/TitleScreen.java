package fr.supdevinci.games.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import fr.supdevinci.games.GameContext;
import fr.supdevinci.games.config.GameConfig;
import fr.supdevinci.games.config.GameConstants;
import fr.supdevinci.games.render.HudRenderer;

/**
 * Minimal title screen used to demonstrate screen management before entering the playable prototype.
 */
public final class TitleScreen extends ScreenAdapter {
    private final GameContext context;
    private final Viewport viewport;
    private final OrthographicCamera camera;
    private final HudRenderer hudRenderer;

    /**
     * Creates the title screen and initializes its camera, viewport and HUD renderer.
     *
     * @param context shared game context
     */
    public TitleScreen(GameContext context) {
        this.context = context;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        this.hudRenderer = new HudRenderer(context.getAssets());
    }

    /**
     * Renders the title view and handles start input.
     *
     * @param delta time elapsed since last frame, in seconds
     */
    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            context.getGame().showGameScreen();
            return;
        }

        ScreenUtils.clear(Color.valueOf("0B132B"));
        viewport.apply(true);
        camera.update();
        hudRenderer.renderCenteredText(GameConstants.TITLE_SCREEN_TEXT, viewport);
    }

    /**
     * Updates the viewport dimensions after window resize.
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

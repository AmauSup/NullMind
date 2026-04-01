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
import fr.supdevinci.games.render.HudRenderer;

/**
 * Minimal title screen used to demonstrate screen management before entering the playable prototype.
 */
public final class TitleScreen extends ScreenAdapter {
    private static final String TITLE_TEXT = "POC libGDX\n\nPeurs et traumatismes\n\nEntrée / Espace : démarrer\nÉchap : revenir ici pendant le jeu";

    private final GameContext context;
    private final Viewport viewport;
    private final OrthographicCamera camera;
    private final HudRenderer hudRenderer;

    public TitleScreen(GameContext context) {
        this.context = context;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        this.hudRenderer = new HudRenderer(context.getAssets());
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            context.getGame().showGameScreen();
            return;
        }

        ScreenUtils.clear(Color.valueOf("0B132B"));
        viewport.apply(true);
        camera.update();
        hudRenderer.renderCenteredText(TITLE_TEXT, viewport);
    }

    @Override
    public void resize(int width, int height) {
        if (width > 0 && height > 0) {
            viewport.update(width, height, true);
        }
    }
}

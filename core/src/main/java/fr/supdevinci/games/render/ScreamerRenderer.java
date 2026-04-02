package fr.supdevinci.games.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import fr.supdevinci.games.assets.GameAssets;
import fr.supdevinci.games.world.screamer.ScreamerManager;

import java.util.function.Consumer;

/**
 * Draws a full-screen screamer overlay when {@link ScreamerManager} is active.
 *
 * <p>If {@code ui/screamer.png} is found in the assets folder the image is displayed.
 * Otherwise a solid red flash covers the screen as a safe fallback.</p>
 */
public final class ScreamerRenderer {

    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch spriteBatch;
    private final Texture screamerTexture;
    private final Consumer<Viewport> activeOverlayRenderer;

    /**
     * @param assets shared libGDX rendering resources; the screamer texture is
     *               loaded from assets if present and may be {@code null}
     */
    public ScreamerRenderer(GameAssets assets) {
        this.shapeRenderer = assets.getShapeRenderer();
        this.spriteBatch = assets.getSpriteBatch();
        this.screamerTexture = assets.getScreamerTextureOrNull();
        this.activeOverlayRenderer = hasTexture() ? this::drawTextureOverlay : this::drawFallbackOverlay;
    }

    /**
     * Draws the screamer overlay if the manager reports an active event.
     * Must be called after all other renderers so the overlay appears on top.
     *
     * @param manager  current screamer state
     * @param viewport viewport used for this screen
     */
    public void render(ScreamerManager manager, Viewport viewport) {
        drawWhenActive(viewport, manager.isActive());
    }

    /**
     * Draws an overlay only when the screamer state is active.
     *
     * @param viewport active viewport
     * @param active whether screamer overlay should be displayed
     */
    private void drawWhenActive(Viewport viewport, boolean active) {
        if (active) {
            activeOverlayRenderer.accept(viewport);
        }
    }

    /**
     * Indicates whether a screamer texture is available.
     *
     * @return {@code true} when a texture was loaded
     */
    private boolean hasTexture() {
        return screamerTexture != null;
    }

    /**
     * Draws the texture-based screamer overlay.
     *
     * @param viewport active viewport
     */
    private void drawTextureOverlay(Viewport viewport) {
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();
        spriteBatch.setColor(Color.WHITE);
        spriteBatch.draw(screamerTexture, 0f, 0f, viewport.getWorldWidth(), viewport.getWorldHeight());
        spriteBatch.end();
    }

    /**
     * Draws a fallback full-screen red overlay when no screamer texture exists.
     *
     * @param viewport active viewport
     */
    private void drawFallbackOverlay(Viewport viewport) {
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(0f, 0f, viewport.getWorldWidth(), viewport.getWorldHeight());
        shapeRenderer.end();
    }
}

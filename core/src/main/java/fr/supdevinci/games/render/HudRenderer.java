package fr.supdevinci.games.render;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import fr.supdevinci.games.assets.GameAssets;
import fr.supdevinci.games.config.GameConstants;
import fr.supdevinci.games.render.hud.BaseStatusMessageFormatter;
import fr.supdevinci.games.render.hud.EmptyStatusFallbackDecorator;
import fr.supdevinci.games.render.hud.LevelPrefixedStatusDecorator;
import fr.supdevinci.games.render.hud.StatusMessageFormatter;
import fr.supdevinci.games.world.GameWorld;
import fr.supdevinci.games.world.TransitionZone;

/**
 * Renders the screen-space information of the prototype.
 */
public final class HudRenderer {
    private final SpriteBatch spriteBatch;
    private final BitmapFont font;
    private final GlyphLayout glyphLayout;
    private final StatusMessageFormatter statusMessageFormatter;

    public HudRenderer(GameAssets assets) {
        this.spriteBatch = assets.getSpriteBatch();
        this.font = assets.getFont();
        this.glyphLayout = new GlyphLayout();
        this.statusMessageFormatter = new LevelPrefixedStatusDecorator(
            new EmptyStatusFallbackDecorator(new BaseStatusMessageFormatter())
        );
    }

    /**
     * Draws the current map name, controls and available exits.
     *
     * @param gameWorld current world state
     * @param viewport viewport used for the current screen
     */
    public void render(GameWorld gameWorld, Viewport viewport) {
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        // Draw level info
        String levelDisplay = gameWorld.getCurrentLevel().getDisplayName();
        font.draw(spriteBatch, GameConstants.HUD_LEVEL + levelDisplay, 24f, viewport.getWorldHeight() - 20f);

        // Draw controls and position info
        font.draw(spriteBatch, GameConstants.HUD_CONTROLS, 24f, viewport.getWorldHeight() - 48f);
        font.draw(spriteBatch, GameConstants.HUD_POSITION + (int)gameWorld.getPlayer().getX() + ", " + (int)gameWorld.getPlayer().getY() + ")", 24f, viewport.getWorldHeight() - 76f);
        font.draw(spriteBatch, GameConstants.HUD_VISITED_LEVELS + gameWorld.getVisitedLevelCount() + "/6", 24f, viewport.getWorldHeight() - 104f);
        font.draw(spriteBatch, GameConstants.HUD_KEYS + formatKeys(gameWorld), 24f, viewport.getWorldHeight() - 132f);
        font.draw(spriteBatch, GameConstants.HUD_STATUS + statusMessageFormatter.format(gameWorld), 24f, viewport.getWorldHeight() - 160f);

        float baseY = 110f;
        font.draw(spriteBatch, GameConstants.HUD_TRANSITIONS, 24f, baseY + 56f);
        int index = 0;
        for (TransitionZone transitionZone : gameWorld.getCurrentLevel().getTransitionZones()) {
            String lockPart = transitionZone.getRequiredKeys().isEmpty() ? ""
                : GameConstants.HUD_REQUIRED_KEYS_PREFIX + transitionZone.getRequiredKeys().size() + GameConstants.HUD_REQUIRED_KEYS_SUFFIX;
            font.draw(spriteBatch, "- " + transitionZone.getLabel() + lockPart, 24f, baseY + 32f - (index * 20f));
            index++;
        }

        spriteBatch.end();
    }

    private String formatKeys(GameWorld gameWorld) {
        if (gameWorld.getInventory().getKeyCount() == 0) {
            return GameConstants.HUD_NO_KEYS;
        }
        return gameWorld.getInventory().getFormattedKeys();
    }

    /**
     * Draws a centered text block, useful for menu-like screens.
     *
     * @param text text to display
     * @param viewport viewport used for the screen
     */
    public void renderCenteredText(String text, Viewport viewport) {
        glyphLayout.setText(font, text);
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();
        font.draw(
            spriteBatch,
            glyphLayout,
            (viewport.getWorldWidth() - glyphLayout.width) / 2f,
            (viewport.getWorldHeight() + glyphLayout.height) / 2f
        );
        spriteBatch.end();
    }
}

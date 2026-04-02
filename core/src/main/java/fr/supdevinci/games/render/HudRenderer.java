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

    /**
     * Creates a HUD renderer backed by shared assets.
     *
     * @param assets shared game assets
     */
    public HudRenderer(GameAssets assets) {
        this.spriteBatch = assets.getSpriteBatch();
        this.font = assets.getFont();
        this.glyphLayout = new GlyphLayout();
        this.statusMessageFormatter = new LevelPrefixedStatusDecorator(
            new EmptyStatusFallbackDecorator(new BaseStatusMessageFormatter())
        );
    }

    /**
     * Draws HUD information with timer and optional victory text.
     *
     * @param gameWorld current world state
     * @param viewport viewport used for the current screen
     * @param timerText formatted timer text (e.g. 02:14)
     * @param victoryText optional victory message, empty when not applicable
     */
    public void render(GameWorld gameWorld, Viewport viewport, String timerText, String victoryText) {
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        drawMainInfo(gameWorld, viewport, timerText);
        drawTransitions(gameWorld);
        drawVictoryText(victoryText, viewport);

        spriteBatch.end();
    }

    /**
     * Draws the primary HUD lines (level, controls, position, keys and status).
     *
     * @param gameWorld current world state
     * @param viewport active viewport
     * @param timerText formatted timer text
     */
    private void drawMainInfo(GameWorld gameWorld, Viewport viewport, String timerText) {
        String levelDisplay = gameWorld.getCurrentLevel().getDisplayName();
        font.draw(spriteBatch, GameConstants.HUD_LEVEL + levelDisplay, 24f, viewport.getWorldHeight() - 20f);
        font.draw(spriteBatch, GameConstants.HUD_CONTROLS, 24f, viewport.getWorldHeight() - 48f);
        font.draw(spriteBatch, GameConstants.HUD_POSITION + (int)gameWorld.getPlayer().getX() + ", " + (int)gameWorld.getPlayer().getY() + ")", 24f, viewport.getWorldHeight() - 76f);
        font.draw(spriteBatch, GameConstants.HUD_VISITED_LEVELS + gameWorld.getVisitedLevelCount() + "/6", 24f, viewport.getWorldHeight() - 104f);
        font.draw(spriteBatch, GameConstants.HUD_KEYS + formatKeys(gameWorld), 24f, viewport.getWorldHeight() - 132f);
        font.draw(spriteBatch, GameConstants.HUD_STATUS + statusMessageFormatter.format(gameWorld), 24f, viewport.getWorldHeight() - 160f);
        if (!timerText.isEmpty()) {
            font.draw(spriteBatch, GameConstants.HUD_TIMER + timerText, 24f, viewport.getWorldHeight() - 188f);
        }
    }

    /**
     * Draws the victory text when it is available.
     *
     * @param victoryText text to display
     * @param viewport active viewport
     */
    private void drawVictoryText(String victoryText, Viewport viewport) {
        if (victoryText == null || victoryText.isEmpty()) {
            return;
        }
        font.draw(spriteBatch, victoryText, 24f, viewport.getWorldHeight() - 216f);
    }

    /**
     * Draws the list of transitions available from the current level.
     *
     * @param gameWorld current world state
     */
    private void drawTransitions(GameWorld gameWorld) {
        float baseY = 110f;
        font.draw(spriteBatch, GameConstants.HUD_TRANSITIONS, 24f, baseY + 56f);
        int index = 0;
        for (TransitionZone transitionZone : gameWorld.getCurrentLevel().getTransitionZones()) {
            String lockPart = transitionZone.getRequiredKeys().isEmpty() ? ""
                : GameConstants.HUD_REQUIRED_KEYS_PREFIX + transitionZone.getRequiredKeys().size() + GameConstants.HUD_REQUIRED_KEYS_SUFFIX;
            font.draw(spriteBatch, "- " + transitionZone.getLabel() + lockPart, 24f, baseY + 32f - (index * 20f));
            index++;
        }
    }

    /**
     * Formats the inventory key summary for HUD display.
     *
     * @param gameWorld current world state
     * @return formatted key list or fallback text when empty
     */
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

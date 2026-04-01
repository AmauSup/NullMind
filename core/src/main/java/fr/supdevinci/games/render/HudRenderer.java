package fr.supdevinci.games.render;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import fr.supdevinci.games.assets.GameAssets;
import fr.supdevinci.games.progress.KeyId;
import fr.supdevinci.games.world.GameWorld;
import fr.supdevinci.games.world.TransitionZone;

import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Renders the screen-space information of the prototype.
 */
public final class HudRenderer {
    private final SpriteBatch spriteBatch;
    private final BitmapFont font;
    private final GlyphLayout glyphLayout;

    public HudRenderer(GameAssets assets) {
        this.spriteBatch = assets.getSpriteBatch();
        this.font = assets.getFont();
        this.glyphLayout = new GlyphLayout();
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
        font.draw(spriteBatch, "Map : " + levelDisplay, 24f, viewport.getWorldHeight() - 20f);

        // Draw controls and position info
        font.draw(spriteBatch, "Déplacement : WASD / flèches | Interagir : E | Saut : Espace", 24f, viewport.getWorldHeight() - 48f);
        font.draw(spriteBatch, "Pos : (" + (int)gameWorld.getPlayer().getX() + ", " + (int)gameWorld.getPlayer().getY() + ")", 24f, viewport.getWorldHeight() - 76f);
        font.draw(spriteBatch, "Zones visitées : " + gameWorld.getVisitedLevelCount() + "/6", 24f, viewport.getWorldHeight() - 104f);
        font.draw(spriteBatch, "Clés : " + formatKeys(gameWorld), 24f, viewport.getWorldHeight() - 132f);
        font.draw(spriteBatch, "Info : " + gameWorld.getLastStatusMessage(), 24f, viewport.getWorldHeight() - 160f);

        float baseY = 110f;
        font.draw(spriteBatch, "Sorties :", 24f, baseY + 56f);
        int index = 0;
        for (TransitionZone transitionZone : gameWorld.getCurrentLevel().getTransitionZones()) {
            String lockPart = transitionZone.getRequiredKeys().isEmpty() ? ""
                : " [" + transitionZone.getRequiredKeys().size() + " pièce(s) requise(s)]";
            font.draw(spriteBatch, "- " + transitionZone.getLabel() + lockPart, 24f, baseY + 32f - (index * 20f));
            index++;
        }

        spriteBatch.end();
    }

    private String formatKeys(GameWorld gameWorld) {
        if (gameWorld.getInventory().getKeyCount() == 0) {
            return "aucune";
        }
        return gameWorld.getInventory().getKeys().stream()
            .sorted(Comparator.comparing(KeyId::name))
            .map(KeyId::getDisplayName)
            .collect(Collectors.joining(", "));
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

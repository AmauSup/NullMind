package fr.supdevinci.games.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;

/**
 * Provides and manages shared rendering resources for the game.
 *
 * <p>This class creates and stores the common libGDX objects used by multiple
 * screens, such as {@link SpriteBatch}, {@link ShapeRenderer}, {@link BitmapFont}
 * and optional textures. Optional textures are loaded defensively and may be
 * {@code null} when the corresponding file is not available.</p>
 */
public final class GameAssets implements Disposable {
    private final SpriteBatch spriteBatch;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;

    /** Nullable — closed grave texture for cemetery interactables. */
    private final Texture graveTexture;
    /** Nullable — explored/open grave texture for cemetery interactables. */
    private final Texture graveExploredTexture;
    /** Nullable — displayed on screamer event; red flash used as fallback. */
    private final Texture screamerTexture;
    /** Nullable — house floor texture (room.pong / room.png) used when available. */
    private final Texture houseFloorTexture;
    /** Nullable — cemetery top area texture (chapel/tomb wall), drawn in cemetery upper zone. */
    private final Texture cemeteryTopTexture;

    /**
     * Initializes all rendering resources and attempts to load optional textures.
     *
     * @throws RuntimeException if a mandatory libGDX resource cannot be initialized
     */
    public GameAssets() {
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().markupEnabled = false;

        graveTexture = tryLoadFirstTexture(
            "texture/Tombe1.png",
            "texture/tombe1.png",
            "ui/grave.png"
        );
        graveExploredTexture = tryLoadFirstTexture(
            "texture/TombeOuvert1.png",
            "texture/tombeouvert1.png",
            "texture/TombeOpen1.png",
            "texture/tombeopen1.png"
        );
        screamerTexture = tryLoadTexture("ui/screamer.png");
        houseFloorTexture = tryLoadFirstTexture(
            "texture/room.pong",
            "texture/room.png",
            "room.pong",
            "room.png"
        );
        cemeteryTopTexture = tryLoadFirstTexture(
            "texture/grandtombe.png",
            "texture/GrandTombe.png",
            "texture/grandTombe.png",
            "texture/grandtombe.jpg",
            "texture/grandtombe.jpeg",
            "texture/cemetery_top.png",
            "texture/cemeteryTop.png",
            "texture/cimetiere_haut.png",
            "texture/cimetiereTop.png",
            "texture/church_top.png",
            "texture/chapelle.png"
        );
    }

    /**
     * Attempts to load a texture from the internal assets directory.
     *
     * @param path relative path inside the assets directory
     * @return the loaded {@link Texture}, or {@code null} when the file is missing or invalid
     */
    private static Texture tryLoadTexture(String path) {
        try {
            FileHandle file = Gdx.files.internal(path);
            return file.exists() ? new Texture(file) : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * Tries multiple paths and returns the first loadable texture.
     *
     * @param paths ordered candidate paths to evaluate
     * @return the first successfully loaded {@link Texture}, or {@code null} when none are valid
     */
    private static Texture tryLoadFirstTexture(String... paths) {
        for (String path : paths) {
            Texture texture = tryLoadTexture(path);
            if (texture != null) {
                return texture;
            }
        }
        return null;
    }

    /**
     * Returns the shared sprite batch instance.
     *
     * @return shared {@link SpriteBatch}
     */
    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    /**
     * Returns the shared shape renderer instance.
     *
     * @return shared {@link ShapeRenderer}
     */
    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    /**
     * Returns the shared bitmap font instance.
     *
     * @return shared {@link BitmapFont}
     */
    public BitmapFont getFont() {
        return font;
    }

    /**
     * Returns the grave texture if loaded.
     *
     * @return grave {@link Texture}, or {@code null} when not available
     */
    public Texture getGraveTextureOrNull() {
        return graveTexture;
    }

    /**
     * Returns the explored/open grave texture if loaded.
     *
     * @return explored grave {@link Texture}, or {@code null} when not available
     */
    public Texture getGraveExploredTextureOrNull() {
        return graveExploredTexture;
    }

    /**
     * Returns the screamer overlay texture if loaded.
     *
     * @return screamer {@link Texture}, or {@code null} when not available
     */
    public Texture getScreamerTextureOrNull() {
        return screamerTexture;
    }

    /**
     * Returns the house floor texture if loaded.
     *
     * @return house floor {@link Texture}, or {@code null} when not available
     */
    public Texture getHouseFloorTextureOrNull() {
        return houseFloorTexture;
    }

    /**
     * Returns the cemetery top-area texture if loaded.
     *
     * @return cemetery top-area {@link Texture}, or {@code null} when not available
     */
    public Texture getCemeteryTopTextureOrNull() {
        return cemeteryTopTexture;
    }

    /**
     * Disposes all allocated graphics resources owned by this asset container.
     */
    @Override
    public void dispose() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        disposeTexture(graveTexture);
        disposeTexture(graveExploredTexture);
        disposeTexture(screamerTexture);
        disposeTexture(houseFloorTexture);
        disposeTexture(cemeteryTopTexture);
    }

    /**
     * Disposes a texture only when it is not {@code null}.
     *
     * @param texture texture instance to dispose
     */
    private void disposeTexture(Texture texture) {
        if (texture != null) {
            texture.dispose();
        }
    }
}

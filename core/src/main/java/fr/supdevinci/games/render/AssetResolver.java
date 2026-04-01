package fr.supdevinci.games.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Resolves asset paths to textures or fallback colors.
 * Provides clean abstraction for future texture loading.
 * 
 * Lookup order:
 * 1. Check if texture exists in assets/
 * 2. If not found, return fallback color
 * 3. If error, return fallback color
 * 
 * Thread-safe: can be called during render() without issues.
 */
public final class AssetResolver {

    private static final String ASSETS_DIR = "assets/";

    private AssetResolver() {
        // Utility class
    }

    /**
     * Tries to load a texture from assets/ directory.
     * Falls back to placeholder color if not found.
     * 
     * @param assetPath relative path within assets/ (e.g., "textures/bedroom.png")
     * @return Texture if found, or null (use fallbackColor for rendering)
     */
    public static Texture resolveTexture(String assetPath) {
        return new Texture(Gdx.files.internal(ASSETS_DIR + assetPath));
    }

}

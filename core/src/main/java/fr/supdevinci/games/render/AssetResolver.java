package fr.supdevinci.games.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
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
     * @param fallbackColor color to use if asset is missing
     * @return Texture if found, or null (use fallbackColor for rendering)
     */
    public static Texture resolveTexture(String assetPath, Color fallbackColor) {
        try {
            FileHandle fh = Gdx.files.internal(ASSETS_DIR + assetPath);
            if (fh.exists()) {
                return new Texture(fh);
            }
        } catch (Exception e) {
            System.err.println("Failed to load asset: " + assetPath + " — using fallback color");
        }
        return null;
    }

    /**
     * Checks if an asset exists without loading it.
     * Useful for conditional logic.
     * 
     * @param assetPath relative path within assets/
     * @return true if asset file exists
     */
    public static boolean assetExists(String assetPath) {
        try {
            return Gdx.files.internal(ASSETS_DIR + assetPath).exists();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Loads color from palette or returns fallback.
     * Wrapper around ColorPalette for convenience.
     * 
     * @param colorKey palette key (e.g., "BEDROOM_PLAYER")
     * @param fallback default if not found
     * @return the color
     */
    public static Color resolveColor(String colorKey, Color fallback) {
        return fr.supdevinci.games.config.ColorPalette.getColor(colorKey, fallback);
    }
}

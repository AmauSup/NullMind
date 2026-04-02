package fr.supdevinci.games.world;

import com.badlogic.gdx.math.Rectangle;
import fr.supdevinci.games.progress.KeyId;

/**
 * Declares an in-world collectible key.
 */
public final class KeyPickup {
    private final String id;
    private final Rectangle area;
    private final KeyId keyId;

    /**
     * Creates a key pickup placed in world coordinates.
     *
     * @param id pickup identifier
     * @param x left position
     * @param y bottom position
     * @param width pickup width
     * @param height pickup height
     * @param keyId key granted by this pickup
     */
    public KeyPickup(String id, float x, float y, float width, float height, KeyId keyId) {
        this.id = id;
        this.area = new Rectangle(x, y, width, height);
        this.keyId = keyId;
    }

    /**
     * Returns pickup identifier.
     *
     * @return pickup id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns pickup bounds.
     *
     * @return pickup rectangle
     */
    public Rectangle getArea() {
        return area;
    }

    /**
     * Returns the key associated with this pickup.
     *
     * @return key id
     */
    public KeyId getKeyId() {
        return keyId;
    }
}

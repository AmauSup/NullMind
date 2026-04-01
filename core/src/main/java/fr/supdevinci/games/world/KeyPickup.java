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

    public KeyPickup(String id, float x, float y, float width, float height, KeyId keyId) {
        this.id = id;
        this.area = new Rectangle(x, y, width, height);
        this.keyId = keyId;
    }

    public String getId() {
        return id;
    }

    public Rectangle getArea() {
        return area;
    }

    public KeyId getKeyId() {
        return keyId;
    }
}

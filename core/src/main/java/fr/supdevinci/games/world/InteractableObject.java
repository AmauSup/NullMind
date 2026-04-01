package fr.supdevinci.games.world;

import com.badlogic.gdx.math.Rectangle;
import fr.supdevinci.games.progress.KeyId;

import java.util.Optional;

/**
 * Object that can be inspected with interaction input and may contain a key.
 */
public final class InteractableObject {
    private final String id;
    private final Rectangle area;
    private final InteractableType type;
    private final Optional<KeyId> hiddenKey;

    public InteractableObject(String id, float x, float y, float width, float height, InteractableType type, KeyId hiddenKey) {
        this.id = id;
        this.area = new Rectangle(x, y, width, height);
        this.type = type;
        this.hiddenKey = Optional.ofNullable(hiddenKey);
    }

    public String getId() {
        return id;
    }

    public Rectangle getArea() {
        return area;
    }

    public InteractableType getType() {
        return type;
    }

    public Optional<KeyId> getHiddenKey() {
        return hiddenKey;
    }
}

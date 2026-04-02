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

    /**
     * Creates an interactable object in world coordinates.
     *
     * @param id unique interactable identifier
     * @param x left position
     * @param y bottom position
     * @param width interactable width
     * @param height interactable height
     * @param type interactable type
     * @param hiddenKey optional hidden key, may be {@code null}
     */
    public InteractableObject(String id, float x, float y, float width, float height, InteractableType type, KeyId hiddenKey) {
        this.id = id;
        this.area = new Rectangle(x, y, width, height);
        this.type = type;
        this.hiddenKey = Optional.ofNullable(hiddenKey);
    }

    /**
     * Returns interactable identifier.
     *
     * @return interactable id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns interactable bounds.
     *
     * @return interactable rectangle
     */
    public Rectangle getArea() {
        return area;
    }

    /**
     * Returns interactable type.
     *
     * @return interactable type
     */
    public InteractableType getType() {
        return type;
    }

    /**
     * Returns optional hidden key.
     *
     * @return optional key id
     */
    public Optional<KeyId> getHiddenKey() {
        return hiddenKey;
    }
}

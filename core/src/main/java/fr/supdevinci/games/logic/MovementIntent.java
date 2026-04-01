package fr.supdevinci.games.logic;

import com.badlogic.gdx.math.Vector2;

/**
 * Immutable movement intent captured from the input layer.
 */
public final class MovementIntent {
    private final float horizontal;
    private final float vertical;

    public MovementIntent(float horizontal, float vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public float getHorizontal() {
        return horizontal;
    }

    public float getVertical() {
        return vertical;
    }

    /**
     * @return true when the player requested movement on at least one axis
     */
    public boolean hasMovement() {
        return horizontal != 0f || vertical != 0f;
    }

    /**
     * @return the requested direction as a mutable vector
     */
    public Vector2 toVector() {
        return new Vector2(horizontal, vertical);
    }
}

package fr.supdevinci.games.world;

import com.badlogic.gdx.math.Rectangle;

/**
 * Simple blocking area used by the prototype maps.
 */
public final class Obstacle {
    private final Rectangle bounds;

    /**
     * Creates a rectangular blocking obstacle.
     *
     * @param x left position
     * @param y bottom position
     * @param width obstacle width
     * @param height obstacle height
     */
    public Obstacle(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);
    }

    /**
     * Returns obstacle bounds.
     *
     * @return obstacle rectangle
     */
    public Rectangle getBounds() {
        return bounds;
    }
}

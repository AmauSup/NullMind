package fr.supdevinci.games.world;

import com.badlogic.gdx.math.Rectangle;

/**
 * Simple blocking area used by the prototype maps.
 */
public final class Obstacle {
    private final Rectangle bounds;

    public Obstacle(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);
    }

    public Rectangle getBounds() {
        return bounds;
    }
}

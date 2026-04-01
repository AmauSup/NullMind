package fr.supdevinci.games.entity;

import com.badlogic.gdx.math.Rectangle;

/**
 * Represents the playable character in the world.
 */
public final class Player {
    private final float width;
    private final float height;
    private float x;
    private float y;

    /**
     * Creates a player at the provided position.
     *
     * @param x initial world x coordinate
     * @param y initial world y coordinate
     * @param width collision box width
     * @param height collision box height
     */
    public Player(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    /**
     * Updates the player's world position.
     *
     * @param x new world x coordinate
     * @param y new world y coordinate
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return a snapshot of the current collision box
     */
    public Rectangle toBounds() {
        return new Rectangle(x, y, width, height);
    }
}

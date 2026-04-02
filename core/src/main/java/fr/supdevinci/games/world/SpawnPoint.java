package fr.supdevinci.games.world;

/**
 * Declares a reusable spawn location inside a level.
 */
public final class SpawnPoint {
    private final String id;
    private final float x;
    private final float y;

    /**
     * Creates a named spawn point.
     *
     * @param id spawn identifier
     * @param x spawn x coordinate
     * @param y spawn y coordinate
     */
    public SpawnPoint(String id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    /**
     * Returns spawn identifier.
     *
     * @return spawn id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns spawn x coordinate.
     *
     * @return x coordinate
     */
    public float getX() {
        return x;
    }

    /**
     * Returns spawn y coordinate.
     *
     * @return y coordinate
     */
    public float getY() {
        return y;
    }
}

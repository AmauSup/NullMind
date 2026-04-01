package fr.supdevinci.games.world;

/**
 * Declares a reusable spawn location inside a level.
 */
public final class SpawnPoint {
    private final String id;
    private final float x;
    private final float y;

    public SpawnPoint(String id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public String getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}

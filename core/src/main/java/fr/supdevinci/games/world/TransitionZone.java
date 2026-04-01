package fr.supdevinci.games.world;

import com.badlogic.gdx.math.Rectangle;
import fr.supdevinci.games.progress.KeyId;

import java.util.List;

/**
 * Defines a rectangular area that moves the player to another level.
 */
public final class TransitionZone {
    public static final class Area {
        private final float x;
        private final float y;
        private final float width;
        private final float height;

        private Area(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public static Area of(float x, float y, float width, float height) {
            return new Area(x, y, width, height);
        }

        public float x() {
            return x;
        }

        public float y() {
            return y;
        }

        public float width() {
            return width;
        }

        public float height() {
            return height;
        }
    }

    private final Rectangle area;
    private final LevelId targetLevelId;
    private final String targetSpawnId;
    private final String label;
    private final List<KeyId> requiredKeys;

    /** Transition libre (aucune clé requise). */
    public TransitionZone(Area area, LevelId targetLevelId, String targetSpawnId, String label) {
        this(area, targetLevelId, targetSpawnId, label, List.of());
    }

    /** Transition verrouillée : toutes les clés de la liste sont nécessaires. */
    public TransitionZone(Area area, LevelId targetLevelId, String targetSpawnId, String label, List<KeyId> requiredKeys) {
        this.area = new Rectangle(area.x(), area.y(), area.width(), area.height());
        this.targetLevelId = targetLevelId;
        this.targetSpawnId = targetSpawnId;
        this.label = label;
        this.requiredKeys = List.copyOf(requiredKeys);
    }

    public Rectangle getArea() {
        return area;
    }

    public LevelId getTargetLevelId() {
        return targetLevelId;
    }

    public String getTargetSpawnId() {
        return targetSpawnId;
    }

    public String getLabel() {
        return label;
    }

    /** Retourne toutes les clés requises. Liste vide = transition libre. */
    public List<KeyId> getRequiredKeys() {
        return requiredKeys;
    }
}

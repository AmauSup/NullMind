package fr.supdevinci.games.world;

import com.badlogic.gdx.math.Rectangle;
import fr.supdevinci.games.progress.KeyId;

import java.util.List;

/**
 * Defines a rectangular area that moves the player to another level.
 */
public final class TransitionZone {
    /**
     * Immutable rectangle description for transition construction.
     */
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

        /**
         * Creates an {@link Area} instance.
         *
         * @param x left position
         * @param y bottom position
         * @param width area width
         * @param height area height
         * @return area instance
         */
        public static Area of(float x, float y, float width, float height) {
            return new Area(x, y, width, height);
        }

        /**
         * Returns x coordinate.
         *
         * @return x coordinate
         */
        public float x() {
            return x;
        }

        /**
         * Returns y coordinate.
         *
         * @return y coordinate
         */
        public float y() {
            return y;
        }

        /**
         * Returns width.
         *
         * @return area width
         */
        public float width() {
            return width;
        }

        /**
         * Returns height.
         *
         * @return area height
         */
        public float height() {
            return height;
        }
    }

    private final Rectangle area;
    private final LevelId targetLevelId;
    private final String targetSpawnId;
    private final String label;
    private final List<KeyId> requiredKeys;

    /**
     * Creates an unlocked transition (no key required).
     *
     * @param area transition bounds
     * @param targetLevelId destination level
     * @param targetSpawnId destination spawn id
     * @param label user-facing transition label
     */
    public TransitionZone(Area area, LevelId targetLevelId, String targetSpawnId, String label) {
        this(area, targetLevelId, targetSpawnId, label, List.of());
    }

    /**
     * Creates a transition with key requirements.
     *
     * @param area transition bounds
     * @param targetLevelId destination level
     * @param targetSpawnId destination spawn id
     * @param label user-facing transition label
     * @param requiredKeys required keys to unlock transition
     */
    public TransitionZone(Area area, LevelId targetLevelId, String targetSpawnId, String label, List<KeyId> requiredKeys) {
        this.area = new Rectangle(area.x(), area.y(), area.width(), area.height());
        this.targetLevelId = targetLevelId;
        this.targetSpawnId = targetSpawnId;
        this.label = label;
        this.requiredKeys = List.copyOf(requiredKeys);
    }

    /**
     * Returns transition area.
     *
     * @return transition rectangle
     */
    public Rectangle getArea() {
        return area;
    }

    /**
     * Returns destination level id.
     *
     * @return target level id
     */
    public LevelId getTargetLevelId() {
        return targetLevelId;
    }

    /**
     * Returns destination spawn id.
     *
     * @return target spawn id
     */
    public String getTargetSpawnId() {
        return targetSpawnId;
    }

    /**
     * Returns transition label.
     *
     * @return transition label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns all required keys for this transition.
     *
     * @return required keys list, empty when transition is unlocked
     */
    public List<KeyId> getRequiredKeys() {
        return requiredKeys;
    }
}

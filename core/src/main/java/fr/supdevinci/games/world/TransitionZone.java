package fr.supdevinci.games.world;

import com.badlogic.gdx.math.Rectangle;
import fr.supdevinci.games.progress.KeyId;

import java.util.List;

/**
 * Defines a rectangular area that moves the player to another level.
 */
public final class TransitionZone {
    private final Rectangle area;
    private final LevelId targetLevelId;
    private final String targetSpawnId;
    private final String label;
    private final List<KeyId> requiredKeys;

    /** Transition libre (aucune clé requise). */
    public TransitionZone(float x, float y, float width, float height, LevelId targetLevelId, String targetSpawnId, String label) {
        this(x, y, width, height, targetLevelId, targetSpawnId, label, List.of());
    }

    /** Transition verrouillée : toutes les clés de la liste sont nécessaires. */
    public TransitionZone(
        float x,
        float y,
        float width,
        float height,
        LevelId targetLevelId,
        String targetSpawnId,
        String label,
        List<KeyId> requiredKeys
    ) {
        this.area = new Rectangle(x, y, width, height);
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

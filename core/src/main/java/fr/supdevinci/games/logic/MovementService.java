package fr.supdevinci.games.logic;

import com.badlogic.gdx.math.Rectangle;
import fr.supdevinci.games.world.Obstacle;

import java.util.List;

/**
 * Contract for movement resolution.
 *
 * <p>Allows the world simulation to depend on an abstraction rather than a concrete
 * movement implementation.</p>
 */
public interface MovementService {
    /**
     * Computes the next collision bounds for the player.
     *
     * @param currentBounds current player bounds
     * @param movementIntent requested movement intent
     * @param speed movement speed in world units per second
     * @param delta frame delta time in seconds
     * @param worldBounds playable world bounds
     * @param obstacles blocking obstacles
     * @return resolved next bounds
     */
    Rectangle computeNextBounds(
        Rectangle currentBounds,
        MovementIntent movementIntent,
        float speed,
        float delta,
        Rectangle worldBounds,
        List<Obstacle> obstacles
    );
}

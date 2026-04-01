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
    Rectangle computeNextBounds(
        Rectangle currentBounds,
        MovementIntent movementIntent,
        float speed,
        float delta,
        Rectangle worldBounds,
        List<Obstacle> obstacles
    );
}

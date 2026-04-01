package fr.supdevinci.games.logic;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import fr.supdevinci.games.world.Obstacle;

import java.util.List;

/**
 * Applies player movement while keeping the character inside the map and outside blocking obstacles.
 */
public final class PlayerMovementService implements MovementService {
    /**
     * Computes the next collision box from the current state.
     *
     * @param currentBounds current player collision box
     * @param movementIntent requested direction from the input layer
     * @param speed movement speed in world units per second
     * @param delta elapsed time in seconds
     * @param worldBounds playable area bounds
     * @param obstacles blocking areas inside the current level
     * @return the resolved collision box after clamping and collision checks
     */
    public Rectangle computeNextBounds(
        Rectangle currentBounds,
        MovementIntent movementIntent,
        float speed,
        float delta,
        Rectangle worldBounds,
        List<Obstacle> obstacles
    ) {
        Rectangle nextBounds = new Rectangle(currentBounds);
        if (!movementIntent.hasMovement() || delta <= 0f) {
            return nextBounds;
        }

        Vector2 direction = movementIntent.toVector().nor().scl(speed * delta);

        Rectangle xCandidate = new Rectangle(nextBounds);
        xCandidate.x += direction.x;
        clampToWorld(xCandidate, worldBounds);
        if (!isBlocked(xCandidate, obstacles)) {
            nextBounds.x = xCandidate.x;
        }

        Rectangle yCandidate = new Rectangle(nextBounds);
        yCandidate.y += direction.y;
        clampToWorld(yCandidate, worldBounds);
        if (!isBlocked(yCandidate, obstacles)) {
            nextBounds.y = yCandidate.y;
        }

        return nextBounds;
    }

    private void clampToWorld(Rectangle bounds, Rectangle worldBounds) {
        bounds.x = MathUtils.clamp(bounds.x, worldBounds.x, worldBounds.x + worldBounds.width - bounds.width);
        bounds.y = MathUtils.clamp(bounds.y, worldBounds.y, worldBounds.y + worldBounds.height - bounds.height);
    }

    private boolean isBlocked(Rectangle candidate, List<Obstacle> obstacles) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.getBounds().overlaps(candidate)) {
                return true;
            }
        }
        return false;
    }
}

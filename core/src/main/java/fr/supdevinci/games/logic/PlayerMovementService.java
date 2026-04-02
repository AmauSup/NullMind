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

        resolveHorizontalMovement(nextBounds, direction.x, worldBounds, obstacles);
        resolveVerticalMovement(nextBounds, direction.y, worldBounds, obstacles);

        return nextBounds;
    }

    /**
     * Resolves movement on horizontal axis with world clamp and obstacle checks.
     *
     * @param nextBounds mutable destination bounds
     * @param deltaX horizontal movement delta
     * @param worldBounds playable world bounds
     * @param obstacles collision obstacles
     */
    private void resolveHorizontalMovement(Rectangle nextBounds, float deltaX, Rectangle worldBounds,
                                           List<Obstacle> obstacles) {
        Rectangle candidate = new Rectangle(nextBounds);
        candidate.x += deltaX;
        clampToWorld(candidate, worldBounds);
        if (!isBlocked(candidate, obstacles)) {
            nextBounds.x = candidate.x;
        }
    }

    /**
     * Resolves movement on vertical axis with world clamp and obstacle checks.
     *
     * @param nextBounds mutable destination bounds
     * @param deltaY vertical movement delta
     * @param worldBounds playable world bounds
     * @param obstacles collision obstacles
     */
    private void resolveVerticalMovement(Rectangle nextBounds, float deltaY, Rectangle worldBounds,
                                         List<Obstacle> obstacles) {
        Rectangle candidate = new Rectangle(nextBounds);
        candidate.y += deltaY;
        clampToWorld(candidate, worldBounds);
        if (!isBlocked(candidate, obstacles)) {
            nextBounds.y = candidate.y;
        }
    }

    /**
     * Clamps bounds so they stay inside world bounds.
     *
     * @param bounds bounds to clamp
     * @param worldBounds playable world bounds
     */
    private void clampToWorld(Rectangle bounds, Rectangle worldBounds) {
        bounds.x = MathUtils.clamp(bounds.x, worldBounds.x, worldBounds.x + worldBounds.width - bounds.width);
        bounds.y = MathUtils.clamp(bounds.y, worldBounds.y, worldBounds.y + worldBounds.height - bounds.height);
    }

    /**
     * Checks if candidate bounds overlap any obstacle.
     *
     * @param candidate candidate bounds
     * @param obstacles collision obstacles
     * @return {@code true} when blocked
     */
    private boolean isBlocked(Rectangle candidate, List<Obstacle> obstacles) {
        return obstacles.stream()
            .anyMatch(obstacle -> obstacle.getBounds().overlaps(candidate));
    }
}

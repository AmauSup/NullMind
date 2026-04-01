package fr.supdevinci.games.logic;

import com.badlogic.gdx.math.Rectangle;
import fr.supdevinci.games.world.Obstacle;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerMovementServiceTest {
    private static final Rectangle WORLD_BOUNDS = new Rectangle(0f, 0f, 300f, 300f);
    private static final float SPEED = 100f;
    private static final float DELTA = 1f;

    private final PlayerMovementService movementService = new PlayerMovementService();

    private Rectangle compute(Rectangle currentBounds, MovementIntent movementIntent, List<Obstacle> obstacles) {
        return movementService.computeNextBounds(
            currentBounds,
            movementIntent,
            SPEED,
            DELTA,
            WORLD_BOUNDS,
            obstacles
        );
    }

    @Test
    void shouldMoveInsideWorldBounds() {
        Rectangle result = compute(new Rectangle(20f, 20f, 28f, 28f), new MovementIntent(1f, 0f), List.of());

        assertEquals(120f, result.x);
        assertEquals(20f, result.y);
    }

    @Test
    void shouldClampMovementAgainstWorldEdge() {
        Rectangle result = compute(new Rectangle(280f, 20f, 28f, 28f), new MovementIntent(1f, 0f), List.of());

        assertEquals(272f, result.x);
        assertEquals(20f, result.y);
    }

    @Test
    void shouldBlockHorizontalMovementWhenObstacleIsHit() {
        Rectangle result = compute(
            new Rectangle(20f, 20f, 28f, 28f),
            new MovementIntent(1f, 0f),
            List.of(new Obstacle(90f, 0f, 40f, 120f))
        );

        assertEquals(20f, result.x);
        assertEquals(20f, result.y);
    }
}

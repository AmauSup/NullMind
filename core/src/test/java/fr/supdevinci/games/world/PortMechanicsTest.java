package fr.supdevinci.games.world;

import fr.supdevinci.games.config.GameConstants;
import fr.supdevinci.games.logic.MovementIntent;
import fr.supdevinci.games.logic.PlayerMovementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PortMechanicsTest {
    private GameWorld gameWorld;

    @BeforeEach
    void setUp() {
        gameWorld = new GameWorld(LevelCatalog.createDefault(), new PlayerMovementService());
        gameWorld.loadLevel(LevelId.PORT, "fromHub");
    }

    @Test
    void shouldJumpFromStartZoneToFirstFloatingPart() {
        gameWorld.getPlayer().setPosition(470f, 340f);

        gameWorld.update(new MovementIntent(0f, 0f), 0.016f, false, true);

        assertEquals(GameConstants.MSG_JUMP_SUCCESS, gameWorld.getLastStatusMessage());
        assertEquals(456f, gameWorld.getPlayer().getX(), 0.001f);
        assertEquals(375f, gameWorld.getPlayer().getY(), 0.001f);
    }

    @Test
    void shouldRespawnToPortEntryWhenFallingInWater() {
        gameWorld.getPlayer().setPosition(500f, 400f);

        gameWorld.update(new MovementIntent(0f, 0f), 0.016f, false, false);

        assertEquals(LevelId.PORT, gameWorld.getCurrentLevel().getId());
        assertEquals(470f, gameWorld.getPlayer().getX(), 0.001f);
        assertEquals(90f, gameWorld.getPlayer().getY(), 0.001f);
        assertEquals(GameConstants.MSG_FELL_IN_WATER, gameWorld.getLastStatusMessage());
    }

    @Test
    void shouldStayInPlaceWhenStandingOnFloatingPart() {
        gameWorld.getPlayer().setPosition(456f, 375f);

        gameWorld.update(new MovementIntent(0f, 0f), 0.016f, false, false);

        assertEquals(LevelId.PORT, gameWorld.getCurrentLevel().getId());
        assertEquals(456f, gameWorld.getPlayer().getX(), 0.001f);
        assertEquals(375f, gameWorld.getPlayer().getY(), 0.001f);
        assertTrue(!GameConstants.MSG_FELL_IN_WATER.equals(gameWorld.getLastStatusMessage()));
    }
}

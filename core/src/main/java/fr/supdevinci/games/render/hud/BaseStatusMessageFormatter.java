package fr.supdevinci.games.render.hud;

import fr.supdevinci.games.world.GameWorld;

/**
 * Base formatter: returns the status message as produced by gameplay logic.
 */
public final class BaseStatusMessageFormatter implements StatusMessageFormatter {
    /**
     * Returns the raw gameplay status message.
     *
     * @param gameWorld current world state
     * @return latest status message
     */
    @Override
    public String format(GameWorld gameWorld) {
        return gameWorld.getLastStatusMessage();
    }
}

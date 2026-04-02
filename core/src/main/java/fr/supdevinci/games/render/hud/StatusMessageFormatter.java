package fr.supdevinci.games.render.hud;

import fr.supdevinci.games.world.GameWorld;

/**
 * Contract used to format HUD status text.
 */
public interface StatusMessageFormatter {
    /**
     * Formats the current world status into a HUD-friendly text.
     *
     * @param gameWorld current world state
     * @return formatted status text
     */
    String format(GameWorld gameWorld);
}

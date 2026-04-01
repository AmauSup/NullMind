package fr.supdevinci.games.render.hud;

import fr.supdevinci.games.world.GameWorld;

/**
 * Contract used to format HUD status text.
 */
public interface StatusMessageFormatter {
    String format(GameWorld gameWorld);
}

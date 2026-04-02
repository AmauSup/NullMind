package fr.supdevinci.games.render.hud;

import fr.supdevinci.games.world.GameWorld;

/**
 * Adds level context to status messages.
 */
public final class LevelPrefixedStatusDecorator extends StatusMessageFormatterDecorator {
    /**
     * Creates a decorator that prefixes status lines with the current level name.
     *
     * @param delegate wrapped formatter
     */
    public LevelPrefixedStatusDecorator(StatusMessageFormatter delegate) {
        super(delegate);
    }

    /**
     * Prefixes the delegate message with current level context.
     *
     * @param gameWorld current world state
     * @return prefixed status text
     */
    @Override
    public String format(GameWorld gameWorld) {
        String base = delegate.format(gameWorld);
        return "[" + gameWorld.getCurrentLevel().getDisplayName() + "] " + base;
    }
}

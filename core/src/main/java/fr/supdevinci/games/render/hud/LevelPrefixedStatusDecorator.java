package fr.supdevinci.games.render.hud;

import fr.supdevinci.games.world.GameWorld;

/**
 * Adds level context to status messages.
 */
public final class LevelPrefixedStatusDecorator extends StatusMessageFormatterDecorator {
    public LevelPrefixedStatusDecorator(StatusMessageFormatter delegate) {
        super(delegate);
    }

    @Override
    public String format(GameWorld gameWorld) {
        String base = delegate.format(gameWorld);
        return "[" + gameWorld.getCurrentLevel().getDisplayName() + "] " + base;
    }
}

package fr.supdevinci.games.render.hud;

import fr.supdevinci.games.config.GameConstants;
import fr.supdevinci.games.world.GameWorld;

/**
 * Replaces empty status content with a readable fallback message.
 */
public final class EmptyStatusFallbackDecorator extends StatusMessageFormatterDecorator {
    /**
     * Creates a fallback decorator around another formatter.
     *
     * @param delegate wrapped formatter
     */
    public EmptyStatusFallbackDecorator(StatusMessageFormatter delegate) {
        super(delegate);
    }

    /**
     * Returns a fallback status text when the delegate output is empty.
     *
     * @param gameWorld current world state
     * @return non-empty status text
     */
    @Override
    public String format(GameWorld gameWorld) {
        String value = delegate.format(gameWorld);
        if (value == null || value.isBlank()) {
            return GameConstants.STATUS_EMPTY;
        }
        return value;
    }
}

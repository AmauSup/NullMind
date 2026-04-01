package fr.supdevinci.games.render.hud;

import fr.supdevinci.games.config.GameConstants;
import fr.supdevinci.games.world.GameWorld;

/**
 * Replaces empty status content with a readable fallback message.
 */
public final class EmptyStatusFallbackDecorator extends StatusMessageFormatterDecorator {
    public EmptyStatusFallbackDecorator(StatusMessageFormatter delegate) {
        super(delegate);
    }

    @Override
    public String format(GameWorld gameWorld) {
        String value = delegate.format(gameWorld);
        if (value == null || value.isBlank()) {
            return GameConstants.STATUS_EMPTY;
        }
        return value;
    }
}

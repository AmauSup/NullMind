package fr.supdevinci.games.render.hud;

/**
 * Base class for formatter decorators.
 */
public abstract class StatusMessageFormatterDecorator implements StatusMessageFormatter {
    protected final StatusMessageFormatter delegate;

    protected StatusMessageFormatterDecorator(StatusMessageFormatter delegate) {
        this.delegate = delegate;
    }
}

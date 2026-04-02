package fr.supdevinci.games.render.hud;

/**
 * Base class for formatter decorators.
 */
public abstract class StatusMessageFormatterDecorator implements StatusMessageFormatter {
    protected final StatusMessageFormatter delegate;

    /**
     * Creates a formatter decorator wrapping another formatter.
     *
     * @param delegate wrapped formatter implementation
     */
    protected StatusMessageFormatterDecorator(StatusMessageFormatter delegate) {
        this.delegate = delegate;
    }
}

package fr.supdevinci.games.world.screamer;

import java.util.Objects;
import java.util.Random;

/**
 * Default implementation creating probabilistic screamer managers.
 */
public final class DefaultScreamerManagerFactory implements ScreamerManagerFactory {
    private final float probability;
    private final float displayDuration;
    private final Random random;

    /**
     * Creates a factory with default screamer settings.
     */
    public DefaultScreamerManagerFactory() {
        this(0.25f, 3f, new Random());
    }

    /**
     * Creates a factory with explicit screamer settings.
     *
     * @param probability screamer fire probability in [0, 1]
     * @param displayDuration screamer duration in seconds
     * @param random random source used by trigger
     */
    public DefaultScreamerManagerFactory(float probability, float displayDuration, Random random) {
        this.probability = probability;
        this.displayDuration = displayDuration;
        this.random = Objects.requireNonNull(random, "random");
    }

    /**
     * Creates a configured {@link ScreamerManager}.
     *
     * @return screamer manager instance
     */
    @Override
    public ScreamerManager create() {
        return new ScreamerManager(new RandomScreamerTrigger(probability, random), displayDuration);
    }
}

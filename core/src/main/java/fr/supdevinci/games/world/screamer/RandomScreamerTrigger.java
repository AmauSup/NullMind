package fr.supdevinci.games.world.screamer;

import java.util.Objects;
import java.util.Random;

/**
 * Triggers a screamer with a fixed probability using a {@link Random} instance.
 *
 * <p>Inject a seeded {@code Random} to get deterministic behaviour in tests.</p>
 */
public final class RandomScreamerTrigger implements ScreamerTrigger {

    private final float probability;
    private final Random random;

    /**
     * @param probability chance of firing in [0.0, 1.0]
     * @param random      source of randomness; inject a seeded instance for tests
     * @throws IllegalArgumentException if probability is outside [0, 1]
     */
    public RandomScreamerTrigger(float probability, Random random) {
        validateProbability(probability);
        this.probability = probability;
        this.random = Objects.requireNonNull(random, "random");
    }

    private static void validateProbability(float probability) {
        if (probability < 0f || probability > 1f) {
            throw new IllegalArgumentException("probability must be in [0, 1], got: " + probability);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean shouldFire() {
        return random.nextFloat() < probability;
    }
}

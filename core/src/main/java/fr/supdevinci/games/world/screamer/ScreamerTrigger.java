package fr.supdevinci.games.world.screamer;

/**
 * Strategy that decides whether a screamer should activate.
 *
 * <p>Implementing this as a functional interface allows simple lambda-based
 * test doubles ({@code () -> false}) without creating extra classes.</p>
 */
@FunctionalInterface
public interface ScreamerTrigger {

    /**
     * Returns {@code true} if a screamer should fire right now.
     *
     * @return whether to trigger the screamer
     */
    boolean shouldFire();
}

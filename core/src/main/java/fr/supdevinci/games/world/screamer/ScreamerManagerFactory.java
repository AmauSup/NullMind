package fr.supdevinci.games.world.screamer;

/**
 * Factory contract used to create {@link ScreamerManager} instances.
 *
 * <p>This abstraction keeps world creation independent from concrete screamer
 * configuration and improves inversion of control.</p>
 */
public interface ScreamerManagerFactory {
    /**
     * Creates a new screamer manager instance.
     *
     * @return configured screamer manager
     */
    ScreamerManager create();
}

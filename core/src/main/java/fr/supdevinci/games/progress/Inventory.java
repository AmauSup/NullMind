package fr.supdevinci.games.progress;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Holds the player's persistent collectibles for the current session.
 */
public final class Inventory {
    private final EnumSet<KeyId> keys;
    private String cachedFormattedKeys;
    private boolean cacheDirty;

    /**
     * Creates an empty inventory.
     */
    public Inventory() {
        this.keys = EnumSet.noneOf(KeyId.class);
        this.cachedFormattedKeys = "";
        this.cacheDirty = true;
    }

    /**
     * Adds a key if the inventory does not already contain it.
     *
     * @param keyId key to add
     * @return true when the key was newly added
     */
    public boolean addKey(KeyId keyId) {
        boolean added = keys.add(keyId);
        if (added) {
            cacheDirty = true;
        }
        return added;
    }

    /**
     * @param keyId key to query
     * @return true if the key exists in inventory
     */
    public boolean hasKey(KeyId keyId) {
        return keys.contains(keyId);
    }

    /**
     * Checks whether all required keys are present.
     *
     * @param required set of keys required by a lock or transition
     * @return {@code true} when all required keys are owned
     */
    public boolean hasAllKeys(java.util.Collection<KeyId> required) {
        return keys.containsAll(required);
    }

    /**
     * @return immutable snapshot of collected keys
     */
    public Set<KeyId> getKeys() {
        return Set.copyOf(keys);
    }

    /**
     * Returns how many keys are currently owned.
     *
     * @return collected key count
     */
    public int getKeyCount() {
        return keys.size();
    }

    /**
     * Returns collected keys as a comma-separated display string.
     *
     * @return formatted key display names, or empty string when none are owned
     */
    public String getFormattedKeys() {
        if (keys.isEmpty()) {
            return "";
        }

        if (cacheDirty) {
            cachedFormattedKeys = keys.stream()
                .sorted()
                .map(KeyId::getDisplayName)
                .collect(Collectors.joining(", "));
            cacheDirty = false;
        }
        return cachedFormattedKeys;
    }
}

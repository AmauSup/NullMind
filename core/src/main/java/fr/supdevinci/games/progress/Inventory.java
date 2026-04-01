package fr.supdevinci.games.progress;

import java.util.EnumSet;
import java.util.Set;

/**
 * Holds the player's persistent collectibles for the current session.
 */
public final class Inventory {
    private final EnumSet<KeyId> keys;

    public Inventory() {
        this.keys = EnumSet.noneOf(KeyId.class);
    }

    /**
     * Adds a key if the inventory does not already contain it.
     *
     * @param keyId key to add
     * @return true when the key was newly added
     */
    public boolean addKey(KeyId keyId) {
        return keys.add(keyId);
    }

    /**
     * @param keyId key to query
     * @return true if the key exists in inventory
     */
    public boolean hasKey(KeyId keyId) {
        return keys.contains(keyId);
    }

    /**
     * @param required les clés dont on veut vérifier la présence
     * @return true si l'inventaire contient TOUTES les clés demandées
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

    public int getKeyCount() {
        return keys.size();
    }
}

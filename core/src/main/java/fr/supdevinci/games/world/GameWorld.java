package fr.supdevinci.games.world;

import com.badlogic.gdx.math.Rectangle;
import fr.supdevinci.games.config.GameConfig;
import fr.supdevinci.games.entity.Player;
import fr.supdevinci.games.logic.MovementIntent;
import fr.supdevinci.games.logic.PlayerMovementService;
import fr.supdevinci.games.progress.Inventory;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Holds the mutable state of a running game session.
 */
public final class GameWorld {
    private final LevelCatalog levelCatalog;
    private final PlayerMovementService movementService;
    private final Player player;
    private final Inventory inventory;
    private final Set<LevelId> visitedLevels;
    private final Set<String> collectedPickupIds;
    private LevelDefinition currentLevel;
    private Optional<String> currentRoomId;
    private String lastStatusMessage;

    /**
     * Creates a new world and starts it on the hub.
     *
     * @param levelCatalog immutable level data
     * @param movementService movement rules for the player
     */
    public GameWorld(LevelCatalog levelCatalog, PlayerMovementService movementService) {
        this.levelCatalog = levelCatalog;
        this.movementService = movementService;
        this.player = new Player(0f, 0f, GameConfig.PLAYER_WIDTH, GameConfig.PLAYER_HEIGHT);
        this.inventory = new Inventory();
        this.visitedLevels = EnumSet.noneOf(LevelId.class);
        this.collectedPickupIds = new HashSet<>();
        this.currentRoomId = Optional.empty(); // kept for compatibility with HUD/API
        this.lastStatusMessage = "";
        loadLevel(LevelId.HOUSE, "start");
    }

    /**
     * Advances the simulation by one frame.
     *
     * @param movementIntent movement requested by the input layer
     * @param delta elapsed time in seconds
     */
    public void update(MovementIntent movementIntent, float delta) {
        List<Obstacle> activeObstacles = getActiveCollisionObstacles();

        Rectangle nextBounds = movementService.computeNextBounds(
            player.toBounds(),
            movementIntent,
            GameConfig.PLAYER_SPEED,
            delta,
            currentLevel.getWorldBounds(),
            activeObstacles
        );
        player.setPosition(nextBounds.x, nextBounds.y);
        collectKeysIfAny();

        updateFlatLevelTransitions();
    }

    /**
     * Returns static obstacles plus collision blockers for locked transition doors.
     * Locked transition zones become physically blocking until required keys are collected.
     */
    private List<Obstacle> getActiveCollisionObstacles() {
        List<Obstacle> obstacles = new ArrayList<>(currentLevel.getObstacles());
        for (TransitionZone transitionZone : currentLevel.getTransitionZones()) {
            if (!transitionZone.getRequiredKeys().isEmpty()
                && !inventory.hasAllKeys(transitionZone.getRequiredKeys())) {
                Rectangle area = transitionZone.getArea();
                obstacles.add(new Obstacle(area.x, area.y, area.width, area.height));
            }
        }
        return obstacles;
    }

    /**
     * Loads the requested level and places the player on the given spawn point.
     *
     * @param levelId target level identifier
     * @param spawnId target spawn identifier
     */
    public void loadLevel(LevelId levelId, String spawnId) {
        currentLevel = levelCatalog.get(levelId);
        visitedLevels.add(levelId);

        currentRoomId = Optional.empty();

        SpawnPoint spawnPoint = currentLevel.resolveSpawn(spawnId);
        player.setPosition(spawnPoint.getX(), spawnPoint.getY());
    }

    /**
     * Handles transitions for flat levels (hub, exterior zones).
     */
    private void updateFlatLevelTransitions() {
        Rectangle playerBounds = player.toBounds();

        for (TransitionZone transitionZone : currentLevel.getTransitionZones()) {
            if (transitionZone.getArea().overlaps(playerBounds)) {
                if (!transitionZone.getRequiredKeys().isEmpty()
                        && !inventory.hasAllKeys(transitionZone.getRequiredKeys())) {
                    long missing = transitionZone.getRequiredKeys().stream()
                        .filter(k -> !inventory.hasKey(k)).count();
                    lastStatusMessage = transitionZone.getLabel() + " verrouillée — " + missing + " clé(s) manquante(s) sur " + transitionZone.getRequiredKeys().size();
                    break;
                }
                loadLevel(transitionZone.getTargetLevelId(), transitionZone.getTargetSpawnId());
                lastStatusMessage = "Transition vers : " + currentLevel.getDisplayName();
                break;
            }
        }
    }

    public LevelDefinition getCurrentLevel() {
        return currentLevel;
    }

    public Player getPlayer() {
        return player;
    }

    public int getVisitedLevelCount() {
        return visitedLevels.size();
    }

    public Set<LevelId> getVisitedLevels() {
        return EnumSet.copyOf(visitedLevels);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String getLastStatusMessage() {
        return lastStatusMessage;
    }

    /**
     * Returns the current room id for interior levels.
     */
    public Optional<String> getCurrentRoomId() {
        return currentRoomId;
    }

    public boolean isPickupCollected(KeyPickup keyPickup) {
        return collectedPickupIds.contains(toPickupToken(currentLevel.getId(), keyPickup.getId()));
    }

    private void collectKeysIfAny() {
        Rectangle playerBounds = player.toBounds();
        for (KeyPickup keyPickup : currentLevel.getKeyPickups()) {
            String token = toPickupToken(currentLevel.getId(), keyPickup.getId());
            if (!collectedPickupIds.contains(token) && keyPickup.getArea().overlaps(playerBounds)) {
                collectedPickupIds.add(token);
                if (inventory.addKey(keyPickup.getKeyId())) {
                    lastStatusMessage = "Nouvelle clé : " + keyPickup.getKeyId().getDisplayName();
                }
            }
        }
    }

    private String toPickupToken(LevelId levelId, String pickupId) {
        return levelId.name() + ":" + pickupId;
    }
}

package fr.supdevinci.games.world;

import com.badlogic.gdx.math.Rectangle;
import fr.supdevinci.games.config.GameConfig;
import fr.supdevinci.games.config.GameConstants;
import fr.supdevinci.games.entity.Player;
import fr.supdevinci.games.logic.MovementService;
import fr.supdevinci.games.logic.MovementIntent;
import fr.supdevinci.games.progress.Inventory;
import fr.supdevinci.games.progress.KeyId;

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
    private static final String FROM_HUB = GameConstants.SPAWN_FROM_HUB;
    private static final float INTERACTION_RANGE = GameConstants.INTERACTION_RANGE;
    private static final Rectangle PORT_WATER_ZONE = new Rectangle(0f, 360f, GameConfig.WORLD_WIDTH, 180f);
    private static final Rectangle PORT_JUMP_START_ZONE = new Rectangle(430f, 330f, 140f, 40f);
    private static final List<Rectangle> PORT_FLOATING_PARTS = List.of(
        new Rectangle(430f, 378f, 80f, 22f),
        new Rectangle(530f, 430f, 70f, 22f),
        new Rectangle(390f, 480f, 70f, 22f)
    );

    private final LevelCatalog levelCatalog;
    private final MovementService movementService;
    private final Player player;
    private final Inventory inventory;
    private final Set<LevelId> visitedLevels;
    private final Set<String> collectedPickupIds;
    private final Set<String> exploredInteractableIds;
    private LevelDefinition currentLevel;
    private String lastStatusMessage;

    /**
     * Creates a new world and starts it on the hub.
     *
     * @param levelCatalog immutable level data
     * @param movementService movement rules for the player
     */
    public GameWorld(LevelCatalog levelCatalog, MovementService movementService) {
        this.levelCatalog = levelCatalog;
        this.movementService = movementService;
        this.player = new Player(0f, 0f, GameConfig.PLAYER_WIDTH, GameConfig.PLAYER_HEIGHT);
        this.inventory = new Inventory();
        this.visitedLevels = EnumSet.noneOf(LevelId.class);
        this.collectedPickupIds = new HashSet<>();
        this.exploredInteractableIds = new HashSet<>();
        this.lastStatusMessage = "";
        loadLevel(LevelId.HOUSE, GameConstants.SPAWN_START);
    }

    public void update(MovementIntent movementIntent, float delta, boolean interactPressed, boolean jumpPressed) {
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

        if (jumpPressed) {
            handlePortJump();
        }

        enforcePortWaterRules();
        collectKeysIfAny();

        if (interactPressed) {
            interactWithNearbyObject();
        }

        updateFlatLevelTransitions();
    }

    private void interactWithNearbyObject() {
        Rectangle playerBounds = player.toBounds();
        for (InteractableObject interactableObject : currentLevel.getInteractableObjects()) {
            if (!isInInteractionRange(playerBounds, interactableObject.getArea())) {
                continue;
            }

            String token = toInteractableToken(currentLevel.getId(), interactableObject.getId());
            if (exploredInteractableIds.contains(token)) {
                lastStatusMessage = GameConstants.MSG_ALREADY_EXPLORED;
                return;
            }

            exploredInteractableIds.add(token);
            Optional<KeyId> hiddenKey = interactableObject.getHiddenKey();
            if (!hiddenKey.isPresent()) {
                lastStatusMessage = GameConstants.MSG_NOTHING_USEFUL;
            }
            else if (inventory.addKey(hiddenKey.get())) {
                lastStatusMessage = GameConstants.MSG_KEY_FOUND + hiddenKey.get().getDisplayName();
            }
            return;
        }

        lastStatusMessage = GameConstants.MSG_NOTHING_TO_INSPECT;
    }

    private boolean isInInteractionRange(Rectangle playerBounds, Rectangle targetArea) {
        Rectangle interactionArea = new Rectangle(targetArea);
        interactionArea.x -= INTERACTION_RANGE;
        interactionArea.y -= INTERACTION_RANGE;
        interactionArea.width += INTERACTION_RANGE * 2f;
        interactionArea.height += INTERACTION_RANGE * 2f;
        return interactionArea.overlaps(playerBounds);
    }

    private void handlePortJump() {
        if (currentLevel.getId() != LevelId.PORT) {
            return;
        }

        Rectangle playerBounds = player.toBounds();
        Rectangle target = null;
        if (PORT_JUMP_START_ZONE.overlaps(playerBounds)) {
            target = PORT_FLOATING_PARTS.get(0);
        } else {
            for (int i = 0; i < PORT_FLOATING_PARTS.size(); i++) {
                if (PORT_FLOATING_PARTS.get(i).overlaps(playerBounds) && i + 1 < PORT_FLOATING_PARTS.size()) {
                    target = PORT_FLOATING_PARTS.get(i + 1);
                    break;
                }
            }
        }

        if (target != null) {
            float targetX = target.x + (target.width - player.getWidth()) / 2f;
            float targetY = target.y + (target.height - player.getHeight()) / 2f;
            player.setPosition(targetX, targetY);
            lastStatusMessage = GameConstants.MSG_JUMP_SUCCESS;
        }
    }

    private void enforcePortWaterRules() {
        if (currentLevel.getId() != LevelId.PORT) {
            return;
        }

        Rectangle playerBounds = player.toBounds();
        if (!PORT_WATER_ZONE.overlaps(playerBounds)) {
            return;
        }

        for (Rectangle floatingPart : PORT_FLOATING_PARTS) {
            if (floatingPart.overlaps(playerBounds)) {
                return;
            }
        }

        loadLevel(LevelId.PORT, FROM_HUB);
        lastStatusMessage = GameConstants.MSG_FELL_IN_WATER;
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

        SpawnPoint spawnPoint = currentLevel.resolveSpawn(spawnId);
        player.setPosition(spawnPoint.getX(), spawnPoint.getY());
    }

    /**
     * Handles transitions for flat levels (hub, exterior zones).
     */
    private void updateFlatLevelTransitions() {
        Rectangle playerBounds = player.toBounds();

        TransitionZone activeTransition = findOverlappingTransition(playerBounds);
        if (activeTransition == null) {
            return;
        }

        if (!activeTransition.getRequiredKeys().isEmpty()
                && !inventory.hasAllKeys(activeTransition.getRequiredKeys())) {
            long missing = activeTransition.getRequiredKeys().stream()
                .filter(k -> !inventory.hasKey(k)).count();
            lastStatusMessage = activeTransition.getLabel()
                + GameConstants.MSG_TRANSITION_LOCKED
                + missing
                + GameConstants.MSG_KEYS_MISSING_1
                + activeTransition.getRequiredKeys().size();
            return;
        }

        loadLevel(activeTransition.getTargetLevelId(), activeTransition.getTargetSpawnId());
        lastStatusMessage = GameConstants.MSG_TRANSITION_TO + currentLevel.getDisplayName();
    }

    private TransitionZone findOverlappingTransition(Rectangle playerBounds) {
        for (TransitionZone transitionZone : currentLevel.getTransitionZones()) {
            if (transitionZone.getArea().overlaps(playerBounds)) {
                return transitionZone;
            }
        }
        return null;
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

    public Inventory getInventory() {
        return inventory;
    }

    public String getLastStatusMessage() {
        return lastStatusMessage;
    }

    public boolean isPickupCollected(KeyPickup keyPickup) {
        return collectedPickupIds.contains(toPickupToken(currentLevel.getId(), keyPickup.getId()));
    }

    public boolean isInteractableExplored(InteractableObject interactableObject) {
        return exploredInteractableIds.contains(toInteractableToken(currentLevel.getId(), interactableObject.getId()));
    }

    private void collectKeysIfAny() {
        Rectangle playerBounds = player.toBounds();
        for (KeyPickup keyPickup : currentLevel.getKeyPickups()) {
            String token = toPickupToken(currentLevel.getId(), keyPickup.getId());
            if (!collectedPickupIds.contains(token) && keyPickup.getArea().overlaps(playerBounds)) {
                collectedPickupIds.add(token);
                if (inventory.addKey(keyPickup.getKeyId())) {
                    lastStatusMessage = GameConstants.MSG_NEW_KEY + keyPickup.getKeyId().getDisplayName();
                }
            }
        }
    }

    private String toPickupToken(LevelId levelId, String pickupId) {
        return levelId.name() + GameConstants.TOKEN_SEPARATOR + pickupId;
    }

    private String toInteractableToken(LevelId levelId, String interactableId) {
        return levelId.name() + GameConstants.TOKEN_SEPARATOR + interactableId;
    }
}

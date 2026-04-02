package fr.supdevinci.games.world;

import com.badlogic.gdx.math.Rectangle;
import fr.supdevinci.games.config.GameConfig;
import fr.supdevinci.games.config.GameConstants;
import fr.supdevinci.games.entity.Player;
import fr.supdevinci.games.logic.MovementService;
import fr.supdevinci.games.logic.MovementIntent;
import fr.supdevinci.games.progress.Inventory;
import fr.supdevinci.games.progress.KeyId;
import fr.supdevinci.games.world.screamer.RandomScreamerTrigger;
import fr.supdevinci.games.world.screamer.ScreamerManager;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

/**
 * Holds the mutable state of a running game session.
 *
 * <p>This class is responsible for movement resolution, object interaction,
 * key collection and level transitions. The screamer effect is managed by an
 * injected {@link ScreamerManager}, allowing deterministic testing.</p>
 */
public final class GameWorld {
    private static final float INTERACTION_RANGE = GameConstants.INTERACTION_RANGE;

    private final LevelCatalog levelCatalog;
    private final MovementService movementService;
    private final ScreamerManager screamManager;
    private final Player player;
    private final Inventory inventory;
    private final Set<LevelId> visitedLevels;
    private final Set<LevelId> levelsWithTriggeredScreamer;
    private final Set<String> collectedPickupIds;
    private final Set<String> exploredInteractableIds;
    private LevelDefinition currentLevel;
    private String lastStatusMessage;

    /**
     * Creates a new world with a randomly-seeded screamer.
     * Use the three-argument constructor to inject a controlled {@link ScreamerManager} in tests.
     *
     * @param levelCatalog    immutable level data
     * @param movementService movement rules for the player
     */
    public GameWorld(LevelCatalog levelCatalog, MovementService movementService) {
        this(levelCatalog, movementService,
            new ScreamerManager(new RandomScreamerTrigger(0.25f, new Random()), 3f));
    }

    /**
     * Full constructor for testing, accepting an explicit {@link ScreamerManager}.
     *
     * @param levelCatalog    immutable level data
     * @param movementService movement rules for the player
     * @param screamManager   controls screamer activation (inject {@code () -> false} in tests)
     */
    public GameWorld(LevelCatalog levelCatalog, MovementService movementService, ScreamerManager screamManager) {
        this.levelCatalog = levelCatalog;
        this.movementService = movementService;
        this.screamManager = screamManager;
        this.player = new Player(0f, 0f, GameConfig.PLAYER_WIDTH, GameConfig.PLAYER_HEIGHT);
        this.inventory = new Inventory();
        this.visitedLevels = EnumSet.noneOf(LevelId.class);
        this.levelsWithTriggeredScreamer = EnumSet.noneOf(LevelId.class);
        this.collectedPickupIds = new HashSet<>();
        this.exploredInteractableIds = new HashSet<>();
        this.lastStatusMessage = "";
        loadLevel(LevelId.HOUSE, GameConstants.SPAWN_START);
    }

    /**
     * Advances one game frame: resolves movement, collects pickups, handles interaction
     * and checks level transitions.
     *
     * @param movementIntent  directional input for this frame
     * @param delta           elapsed time in seconds since the last frame
     * @param interactPressed whether the player pressed the interact key this frame
     */
    public void update(MovementIntent movementIntent, float delta, boolean interactPressed) {
        screamManager.update(delta);

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

        if (interactPressed) {
            interactWithNearbyObject();
        }

        updateFlatLevelTransitions();
    }

    /**
     * Tries to interact with the closest interactable in range.
     */
    private void interactWithNearbyObject() {
        InteractableObject target = findNearbyInteractable(player.toBounds());
        if (target == null) {
            lastStatusMessage = GameConstants.MSG_NOTHING_TO_INSPECT;
            return;
        }

        processInteraction(target);
    }

    /**
     * Finds the first interactable object inside interaction range.
     *
     * @param playerBounds current player bounds
     * @return nearby interactable, or {@code null} if none found
     */
    private InteractableObject findNearbyInteractable(Rectangle playerBounds) {
        for (InteractableObject interactableObject : currentLevel.getInteractableObjects()) {
            if (isInInteractionRange(playerBounds, interactableObject.getArea())) {
                return interactableObject;
            }
        }
        return null;
    }

    /**
     * Processes a first-time interaction and prevents reprocessing explored objects.
     *
     * @param interactableObject interactable being processed
     */
    private void processInteraction(InteractableObject interactableObject) {
        String token = toInteractableToken(currentLevel.getId(), interactableObject.getId());
        if (exploredInteractableIds.contains(token)) {
            lastStatusMessage = GameConstants.MSG_ALREADY_EXPLORED;
            return;
        }

        exploredInteractableIds.add(token);
        processInteractableKey(interactableObject);
    }

    /**
     * Resolves the key hidden inside an interactable and possibly triggers a screamer.
     * Screamers fire on first exploration of graves or books.
     *
     * @param interactableObject the interactable that was just explored
     */
    private void processInteractableKey(InteractableObject interactableObject) {
        updateInteractionStatus(interactableObject.getHiddenKey());
        tryTriggerScreamer(interactableObject);
    }

    /**
     * Attempts to activate screamer logic for an eligible interactable.
     *
     * @param interactableObject interactable that was explored
     */
    private void tryTriggerScreamer(InteractableObject interactableObject) {
        if (!canTriggerScreamer(interactableObject) || hasScreamerAlreadyTriggeredInCurrentLevel()) {
            return;
        }

        boolean wasActiveBefore = screamManager.isActive();
        screamManager.tryActivate();
        if (!wasActiveBefore && screamManager.isActive()) {
            levelsWithTriggeredScreamer.add(currentLevel.getId());
        }
    }

    /**
     * Checks whether a screamer already triggered in the current level.
     *
     * @return {@code true} when current level already consumed its screamer
     */
    private boolean hasScreamerAlreadyTriggeredInCurrentLevel() {
        return levelsWithTriggeredScreamer.contains(currentLevel.getId());
    }

    /**
     * Updates interaction status and grants hidden key when present.
     *
     * @param hiddenKey optional key hidden in the interactable
     */
    private void updateInteractionStatus(Optional<KeyId> hiddenKey) {
        if (!hiddenKey.isPresent()) {
            lastStatusMessage = GameConstants.MSG_NOTHING_USEFUL;
            return;
        }

        KeyId keyId = hiddenKey.get();
        if (inventory.addKey(keyId)) {
            lastStatusMessage = GameConstants.MSG_KEY_FOUND + keyId.getDisplayName();
        }
    }

    /**
     * Indicates whether an interactable type can trigger a screamer.
     *
     * @param interactableObject interactable to evaluate
     * @return {@code true} for graves and books
     */
    private boolean canTriggerScreamer(InteractableObject interactableObject) {
        InteractableType type = interactableObject.getType();
        return type == InteractableType.GRAVE || type == InteractableType.BOOK;
    }

    /**
     * Computes overlap between player bounds and an expanded interaction area.
     *
     * @param playerBounds current player bounds
     * @param targetArea interactable area
     * @return {@code true} when interaction range overlaps player
     */
    private boolean isInInteractionRange(Rectangle playerBounds, Rectangle targetArea) {
        Rectangle interactionArea = new Rectangle(targetArea);
        interactionArea.x -= INTERACTION_RANGE;
        interactionArea.y -= INTERACTION_RANGE;
        interactionArea.width += INTERACTION_RANGE * 2f;
        interactionArea.height += INTERACTION_RANGE * 2f;
        return interactionArea.overlaps(playerBounds);
    }

    /**
     * Returns static obstacles plus collision blockers for locked transition doors.
     * Locked transition zones become physically blocking until required keys are collected.
     */
    private List<Obstacle> getActiveCollisionObstacles() {
        List<Obstacle> obstacles = new ArrayList<>(currentLevel.getObstacles());
        addLockedTransitionObstacles(obstacles);
        return obstacles;
    }

    /**
     * Adds temporary blocking obstacles for currently locked transitions.
     *
     * @param obstacles destination obstacle list
     */
    private void addLockedTransitionObstacles(List<Obstacle> obstacles) {
        for (TransitionZone transitionZone : currentLevel.getTransitionZones()) {
            if (isTransitionLocked(transitionZone)) {
                Rectangle area = transitionZone.getArea();
                obstacles.add(new Obstacle(area.x, area.y, area.width, area.height));
            }
        }
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
        handleTransition(findOverlappingTransition(player.toBounds()));
    }

    /**
     * Resolves transition logic for an overlapping transition zone.
     *
     * @param transition overlapping transition, or {@code null}
     */
    private void handleTransition(TransitionZone transition) {
        if (transition == null) {
            return;
        }

        if (isTransitionLocked(transition)) {
            setLockedTransitionMessage(transition);
        } else {
            applyTransition(transition);
        }
    }

    /**
     * Applies a transition and updates status message.
     *
     * @param transition transition to apply
     */
    private void applyTransition(TransitionZone transition) {
        loadLevel(transition.getTargetLevelId(), transition.getTargetSpawnId());
        lastStatusMessage = GameConstants.MSG_TRANSITION_TO + currentLevel.getDisplayName();
    }

    /**
     * Checks if transition requirements are currently unmet.
     *
     * @param transition transition to evaluate
     * @return {@code true} when transition is locked
     */
    private boolean isTransitionLocked(TransitionZone transition) {
        return !transition.getRequiredKeys().isEmpty() && !inventory.hasAllKeys(transition.getRequiredKeys());
    }

    /**
     * Sets a user-facing message describing missing keys for a locked transition.
     *
     * @param transition locked transition
     */
    private void setLockedTransitionMessage(TransitionZone transition) {
        long missing = transition.getRequiredKeys().stream()
            .filter(k -> !inventory.hasKey(k)).count();
        lastStatusMessage = transition.getLabel()
            + GameConstants.MSG_TRANSITION_LOCKED
            + missing
            + GameConstants.MSG_KEYS_MISSING_1
            + transition.getRequiredKeys().size();
    }

    /**
     * Finds the transition zone overlapping the player bounds.
     *
     * @param playerBounds current player bounds
     * @return overlapping transition, or {@code null}
     */
    private TransitionZone findOverlappingTransition(Rectangle playerBounds) {
        for (TransitionZone transitionZone : currentLevel.getTransitionZones()) {
            if (transitionZone.getArea().overlaps(playerBounds)) {
                return transitionZone;
            }
        }
        return null;
    }

    /**
     * Returns current level definition.
     *
     * @return current level
     */
    public LevelDefinition getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Returns current player entity.
     *
     * @return player entity
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns number of unique visited levels.
     *
     * @return visited level count
     */
    public int getVisitedLevelCount() {
        return visitedLevels.size();
    }

    /**
     * Returns the current inventory.
     *
     * @return player inventory
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Returns the latest gameplay status message.
     *
     * @return last status text
     */
    public String getLastStatusMessage() {
        return lastStatusMessage;
    }

    /**
     * Returns the screamer manager so that renderers can query its active state.
     *
     * @return the screamer manager for this world session
     */
    public ScreamerManager getScreamerManager() {
        return screamManager;
    }

    /**
     * Indicates whether a key pickup in current level was already collected.
     *
     * @param keyPickup pickup to check
     * @return {@code true} when pickup was collected
     */
    public boolean isPickupCollected(KeyPickup keyPickup) {
        return collectedPickupIds.contains(toPickupToken(currentLevel.getId(), keyPickup.getId()));
    }

    /**
     * Indicates whether an interactable in current level was already explored.
     *
     * @param interactableObject interactable to check
     * @return {@code true} when interactable was explored
     */
    public boolean isInteractableExplored(InteractableObject interactableObject) {
        return exploredInteractableIds.contains(toInteractableToken(currentLevel.getId(), interactableObject.getId()));
    }

    /**
     * Processes all key pickups that overlap the player for this frame.
     */
    private void collectKeysIfAny() {
        Rectangle playerBounds = player.toBounds();
        for (KeyPickup keyPickup : currentLevel.getKeyPickups()) {
            tryCollectKey(keyPickup, playerBounds);
        }
    }

    /**
     * Attempts to collect one key pickup.
     *
     * @param keyPickup pickup candidate
     * @param playerBounds current player bounds
     */
    private void tryCollectKey(KeyPickup keyPickup, Rectangle playerBounds) {
        String token = toPickupToken(currentLevel.getId(), keyPickup.getId());
        if (!canCollectKey(token, keyPickup, playerBounds)) {
            return;
        }

        markCollected(token);
        updateStatusIfInventoryChanged(keyPickup);
    }

    /**
     * Checks whether a pickup can be collected now.
     *
     * @param token unique pickup token
     * @param keyPickup pickup candidate
     * @param playerBounds current player bounds
     * @return {@code true} when pickup is new and overlaps player
     */
    private boolean canCollectKey(String token, KeyPickup keyPickup, Rectangle playerBounds) {
        return !isAlreadyCollected(token) && isOverlappingPlayer(keyPickup, playerBounds);
    }

    /**
     * Checks whether pickup token was already collected.
     *
     * @param token unique pickup token
     * @return {@code true} when token is already stored
     */
    private boolean isAlreadyCollected(String token) {
        return collectedPickupIds.contains(token);
    }

    /**
     * Checks overlap between pickup and player.
     *
     * @param keyPickup pickup candidate
     * @param playerBounds current player bounds
     * @return {@code true} when rectangles overlap
     */
    private boolean isOverlappingPlayer(KeyPickup keyPickup, Rectangle playerBounds) {
        return keyPickup.getArea().overlaps(playerBounds);
    }

    /**
     * Marks a pickup token as collected.
     *
     * @param token unique pickup token
     */
    private void markCollected(String token) {
        collectedPickupIds.add(token);
    }

    /**
     * Adds key to inventory and updates status message when inventory changed.
     *
     * @param keyPickup collected key pickup
     */
    private void updateStatusIfInventoryChanged(KeyPickup keyPickup) {
        if (inventory.addKey(keyPickup.getKeyId())) {
            lastStatusMessage = GameConstants.MSG_NEW_KEY + keyPickup.getKeyId().getDisplayName();
        }
    }

    /**
     * Builds a unique token for key pickups.
     *
     * @param levelId level id
     * @param pickupId pickup identifier
     * @return unique pickup token
     */
    private String toPickupToken(LevelId levelId, String pickupId) {
        return levelId.name() + GameConstants.TOKEN_SEPARATOR + pickupId;
    }

    /**
     * Builds a unique token for interactables.
     *
     * @param levelId level id
     * @param interactableId interactable identifier
     * @return unique interactable token
     */
    private String toInteractableToken(LevelId levelId, String interactableId) {
        return levelId.name() + GameConstants.TOKEN_SEPARATOR + interactableId;
    }
}

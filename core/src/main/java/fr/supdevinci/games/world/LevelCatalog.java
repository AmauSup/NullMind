package fr.supdevinci.games.world;

import com.badlogic.gdx.graphics.Color;
import fr.supdevinci.games.config.GameConfig;
import fr.supdevinci.games.config.GameConstants;
import fr.supdevinci.games.progress.KeyId;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores the immutable set of level definitions available to the game.
 */
public final class LevelCatalog {
    private static final String START = GameConstants.SPAWN_START;
    private static final String FROM_HOUSE = GameConstants.SPAWN_FROM_HOUSE;
    private static final String FROM_LIBRARY = GameConstants.SPAWN_FROM_LIBRARY;
    private static final String FROM_PORT = GameConstants.SPAWN_FROM_PORT;
    private static final String FROM_CEMETERY = GameConstants.SPAWN_FROM_CEMETERY;
    private static final String FROM_HUB = GameConstants.SPAWN_FROM_HUB;
    private static final String FROM_CELLAR = GameConstants.SPAWN_FROM_CELLAR;
    private static final String RETOUR_HUB = "Retour hub";
    private static final float GRAVE_WIDTH = 32f;
    private static final float GRAVE_HEIGHT = 48f;

    private final Map<LevelId, LevelDefinition> levels;

    private LevelCatalog(Map<LevelId, LevelDefinition> levels) {
        this.levels = Map.copyOf(levels);
    }

    /**
     * Creates the default prototype level set.
     *
     * @return an immutable catalog containing the hub and the five connected maps
     */
    public static LevelCatalog createDefault() {
        EnumMap<LevelId, LevelDefinition> levels = new EnumMap<>(LevelId.class);
        levels.put(LevelId.HUB, createHub());
        levels.put(LevelId.HOUSE, createHouse());
        levels.put(LevelId.CELLAR, createCellar());
        levels.put(LevelId.LIBRARY, createLibrary());
        levels.put(LevelId.PORT, createPort());
        levels.put(LevelId.CEMETERY, createCemetery());
        validateUniqueKeyPlacements(levels);
        return new LevelCatalog(levels);
    }

    /**
     * Returns a level by identifier.
     *
     * @param levelId requested level
     * @return the corresponding definition
     */
    public LevelDefinition get(LevelId levelId) {
        LevelDefinition level = levels.get(levelId);
        if (level == null) {
            throw new IllegalArgumentException("Unknown level: " + levelId);
        }
        return level;
    }

    /**
     * Builds the hub level definition.
     *
     * @return hub level
     */
    private static LevelDefinition createHub() {
        Map<String, SpawnPoint> spawns = spawnMap(
            spawn(START, 470f, 260f),
            spawn(FROM_HOUSE, 470f, 150f),
            spawn(FROM_LIBRARY, 250f, 260f),
            spawn(FROM_PORT, 470f, 340f),
            spawn(FROM_CEMETERY, 700f, 260f)
        );

        return LevelDefinition.flat(
            LevelId.HUB,
            LevelDefinition.Size.of(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT),
            LevelDefinition.Colors.of(Color.valueOf("4A4F57"), Color.valueOf("B8A98A")),
            LevelDefinition.FlatContent.of(
                // Maison (bas)
                java.util.List.of(
                    obstacle(380f, 20f, 200f, 100f),
                // Bibliothèque (gauche)
                    obstacle(20f, 180f, 180f, 180f),
                // Cimetière (droite)
                    obstacle(760f, 180f, 180f, 180f),
                // Port (haut)
                    obstacle(350f, 390f, 260f, 130f)
                ),
                java.util.List.of(
                    transition(TransitionZone.Area.of(440f, 120f, 80f, 16f), LevelId.HOUSE, FROM_HUB, "Maison"),
                    transition(TransitionZone.Area.of(200f, 250f, 16f, 60f), LevelId.LIBRARY, FROM_HUB, "Bibliothèque"),
                    transition(TransitionZone.Area.of(744f, 250f, 16f, 60f), LevelId.CEMETERY, FROM_HUB, "Cimetière"),
                    transition(TransitionZone.Area.of(450f, 370f, 60f, 16f), LevelId.PORT, FROM_HUB, "Port")
                ),
                java.util.List.of()
            ),
            spawns,
            START
        );
    }

    /**
     * Builds the house level definition.
     *
     * @return house level
     */
    private static LevelDefinition createHouse() {
        // Maison = une seule grande map avec pièces suggérées par murs internes.
        Map<String, SpawnPoint> spawns = spawnMap(
            spawn(START, 220f, 180f),
            spawn(FROM_HUB, 470f, 430f),
            spawn(FROM_CELLAR, 470f, 120f)
        );

        return LevelDefinition.flat(
            LevelId.HOUSE,
            LevelDefinition.Size.of(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT),
            LevelDefinition.Colors.of(Color.valueOf("5C3D2E"), Color.valueOf("D2B48C")),
            LevelDefinition.FlatContent.of(
                java.util.List.of(
                // Enveloppe extérieure avec porte bas (cave) et porte haut (hub)
                obstacle(80f, 500f, 360f, 8f),
                obstacle(520f, 500f, 360f, 8f),
                obstacle(80f, 60f, 370f, 8f),
                obstacle(510f, 60f, 370f, 8f),
                obstacle(80f, 60f, 8f, 448f),
                obstacle(872f, 60f, 8f, 448f),

                // Structuration simple des pièces
                obstacle(380f, 140f, 8f, 110f),
                obstacle(380f, 310f, 8f, 130f),
                obstacle(580f, 140f, 8f, 110f),
                obstacle(580f, 310f, 8f, 130f),
                obstacle(120f, 300f, 220f, 8f),
                obstacle(620f, 300f, 220f, 8f)
                ),
                java.util.List.of(
                transition(TransitionZone.Area.of(440f, 60f, 80f, 16f), LevelId.CELLAR, FROM_HOUSE, "Porte cave",
                    List.of(KeyId.LIBRARY_KEY, KeyId.PORT_KEY, KeyId.CEMETERY_KEY))
                ,
                transition(TransitionZone.Area.of(450f, 500f, 60f, 16f), LevelId.HUB, FROM_HOUSE, "Sortie maison")
                ),
                java.util.List.of()
            ),
            spawns,
            START
        );
    }

    /**
     * Builds the cellar level definition.
     *
     * @return cellar level
     */
    private static LevelDefinition createCellar() {
        Map<String, SpawnPoint> spawns = spawnMap(
            spawn(FROM_HOUSE, 452f, 410f)
        );

        return LevelDefinition.flat(
            LevelId.CELLAR,
            LevelDefinition.Size.of(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT),
            LevelDefinition.Colors.of(Color.valueOf("1A1A1D"), Color.valueOf("6A4C93")),
            LevelDefinition.FlatContent.of(
                java.util.List.of(
                obstacle(180f, 140f, 120f, 220f),
                obstacle(390f, 200f, 180f, 60f),
                obstacle(700f, 140f, 120f, 220f)
                ),
                java.util.List.of(
                transition(TransitionZone.Area.of(430f, 470f, 100f, 40f), LevelId.HOUSE, FROM_CELLAR, "Retour maison")
                ),
                java.util.List.of()
            ),
            spawns,
            FROM_HOUSE
        );
    }

    /**
     * Builds the library level definition.
     *
     * @return library level
     */
    private static LevelDefinition createLibrary() {
        Map<String, SpawnPoint> spawns = spawnMap(
            spawn(FROM_HUB, 840f, 260f)
        );

        java.util.List<InteractableObject> libraryBooks = java.util.List.of(
            interactable(GameConstants.BOOK_1, 210f, 110f, 34f, 40f, InteractableType.BOOK, null),
            interactable(GameConstants.BOOK_2, 290f, 170f, 34f, 40f, InteractableType.BOOK, null),
            interactable(GameConstants.BOOK_WITH_KEY, 370f, 230f, 34f, 40f, InteractableType.BOOK, KeyId.LIBRARY_KEY),
            interactable(GameConstants.BOOK_4, 450f, 280f, 34f, 40f, InteractableType.BOOK, null),
            interactable(GameConstants.BOOK_5, 530f, 130f, 34f, 40f, InteractableType.BOOK, null),
            interactable(GameConstants.BOOK_6, 610f, 210f, 34f, 40f, InteractableType.BOOK, null)
        );

        return LevelDefinition.flat(
            LevelId.LIBRARY,
            LevelDefinition.Size.of(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT),
            LevelDefinition.Colors.of(Color.valueOf("2E241C"), Color.valueOf("D8B56A")),
            LevelDefinition.FlatContent.of(
                java.util.List.of(
                // Rayons
                obstacle(210f, 110f, 34f, 300f),
                obstacle(290f, 110f, 34f, 300f),
                obstacle(370f, 110f, 34f, 300f),
                obstacle(450f, 110f, 34f, 300f),
                obstacle(530f, 110f, 34f, 300f),
                obstacle(610f, 110f, 34f, 300f),
                // Comptoir bibliothécaire
                obstacle(700f, 330f, 170f, 70f)
                ),
                java.util.List.of(
                transition(TransitionZone.Area.of(890f, 220f, 40f, 100f), LevelId.HUB, FROM_LIBRARY, RETOUR_HUB)
                ),
                java.util.List.of(),
                libraryBooks
            ),
            spawns,
            FROM_HUB
        );
    }

    /**
     * Builds the port level definition.
     *
     * @return port level
     */
    private static LevelDefinition createPort() {
        Map<String, SpawnPoint> spawns = spawnMap(
            spawn(FROM_HUB, 470f, 90f)
        );

        return LevelDefinition.flat(
            LevelId.PORT,
            LevelDefinition.Size.of(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT),
            LevelDefinition.Colors.of(Color.valueOf("4A5D6E"), Color.valueOf("8F6B45")),
            LevelDefinition.FlatContent.of(
                java.util.List.of(
                // Bâtiments en bas (zone quai)
                obstacle(80f,  70f,  180f, 90f),
                obstacle(300f, 80f,  140f, 80f),
                obstacle(500f, 70f,  180f, 90f),
                obstacle(740f, 80f,  140f, 80f),

                // Eau sous le pont — centre bloqué, approches latérales (x=0-120 et x=840-960) libres
                obstacle(120f, 360f, 720f, 50f),

                // Eau au-dessus du pont — toute la largeur
                obstacle(0f,   460f, GameConfig.WORLD_WIDTH, 80f)
                ),
                java.util.List.of(
                transition(TransitionZone.Area.of(440f, 30f, 80f, 24f), LevelId.HUB, FROM_PORT, RETOUR_HUB)
                ),
                java.util.List.of(
                // La clé de port est sur le pont (y=410–460), accessible via les approches latérales
                keyPickup("portToken", 465f, 422f, 26f, 26f, KeyId.PORT_KEY)
                )
            ),
            spawns,
            FROM_HUB
        );
    }

    /**
     * Builds the cemetery level definition.
     *
     * @return cemetery level
     */
    private static LevelDefinition createCemetery() {
        Map<String, SpawnPoint> spawns = spawnMap(
            spawn(FROM_HUB, 110f, 250f)
        );

        java.util.List<InteractableObject> graves = java.util.List.of(
            interactable(GameConstants.GRAVE_1, 180f, 120f, GRAVE_WIDTH, GRAVE_HEIGHT, InteractableType.GRAVE, null),
            interactable(GameConstants.GRAVE_2, 340f, 120f, GRAVE_WIDTH, GRAVE_HEIGHT, InteractableType.GRAVE, null),
            interactable(GameConstants.GRAVE_3, 500f, 200f, GRAVE_WIDTH, GRAVE_HEIGHT, InteractableType.GRAVE, null),
            interactable(GameConstants.GRAVE_WITH_KEY, 700f, 280f, GRAVE_WIDTH, GRAVE_HEIGHT, InteractableType.GRAVE, KeyId.CEMETERY_KEY),
            interactable(GameConstants.GRAVE_5, 260f, 200f, GRAVE_WIDTH, GRAVE_HEIGHT, InteractableType.GRAVE, null)
        );

        return LevelDefinition.flat(
            LevelId.CEMETERY,
            LevelDefinition.Size.of(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT),
            LevelDefinition.Colors.of(Color.valueOf("2D3430"), Color.valueOf("6E7B73")),
            LevelDefinition.FlatContent.of(
                createCemeteryObstacles(),
                java.util.List.of(
                transition(TransitionZone.Area.of(30f, 220f, 30f, 100f), LevelId.HUB, FROM_CEMETERY, RETOUR_HUB)
                ),
                java.util.List.of(),
                graves
            ),
            spawns,
            FROM_HUB
        );
    }

    /**
     * Creates the cemetery obstacle layout.
     *
     * @return cemetery obstacles
     */
    private static java.util.List<Obstacle> createCemeteryObstacles() {
        return java.util.List.of(
            // Église en haut
            obstacle(360f, 390f, 240f, 120f),

            // Tombes première rangée (y=120)
            obstacle(180f, 120f, GRAVE_WIDTH, GRAVE_HEIGHT),
            obstacle(260f, 120f, GRAVE_WIDTH, GRAVE_HEIGHT),
            obstacle(340f, 120f, GRAVE_WIDTH, GRAVE_HEIGHT),
            obstacle(420f, 120f, GRAVE_WIDTH, GRAVE_HEIGHT),
            obstacle(500f, 120f, GRAVE_WIDTH, GRAVE_HEIGHT),
            obstacle(580f, 120f, GRAVE_WIDTH, GRAVE_HEIGHT),
            obstacle(660f, 120f, GRAVE_WIDTH, GRAVE_HEIGHT),
            obstacle(740f, 120f, GRAVE_WIDTH, GRAVE_HEIGHT),

            // Tombes deuxième rangée (y=200)
            obstacle(180f, 200f, GRAVE_WIDTH, GRAVE_HEIGHT),
            obstacle(260f, 200f, GRAVE_WIDTH, GRAVE_HEIGHT),
            obstacle(340f, 200f, GRAVE_WIDTH, GRAVE_HEIGHT),
            obstacle(500f, 200f, GRAVE_WIDTH, GRAVE_HEIGHT),
            obstacle(580f, 200f, GRAVE_WIDTH, GRAVE_HEIGHT),
            obstacle(660f, 200f, GRAVE_WIDTH, GRAVE_HEIGHT),
            obstacle(740f, 200f, GRAVE_WIDTH, GRAVE_HEIGHT),

            // Tombes troisième rangée (y=280)
            obstacle(220f, 280f, GRAVE_WIDTH, GRAVE_HEIGHT),
            obstacle(300f, 280f, GRAVE_WIDTH, GRAVE_HEIGHT),
            obstacle(380f, 280f, GRAVE_WIDTH, GRAVE_HEIGHT),
            obstacle(460f, 280f, GRAVE_WIDTH, GRAVE_HEIGHT),
            obstacle(620f, 280f, GRAVE_WIDTH, GRAVE_HEIGHT),
            obstacle(700f, 280f, GRAVE_WIDTH, GRAVE_HEIGHT)
        );
    }

    /**
     * Creates an obstacle helper instance.
     *
     * @param x left position
     * @param y bottom position
     * @param width obstacle width
     * @param height obstacle height
     * @return obstacle instance
     */
    private static Obstacle obstacle(float x, float y, float width, float height) {
        return new Obstacle(x, y, width, height);
    }

    /**
     * Creates a spawn-point helper instance.
     *
     * @param id spawn id
     * @param x spawn x coordinate
     * @param y spawn y coordinate
     * @return spawn point instance
     */
    private static SpawnPoint spawn(String id, float x, float y) {
        return new SpawnPoint(id, x, y);
    }

    /**
     * Builds a linked map of spawn ids to spawn points.
     *
     * @param spawnPoints spawn entries
     * @return ordered spawn map
     */
    private static Map<String, SpawnPoint> spawnMap(SpawnPoint... spawnPoints) {
        Map<String, SpawnPoint> spawns = new LinkedHashMap<>();
        for (SpawnPoint spawnPoint : spawnPoints) {
            spawns.put(spawnPoint.getId(), spawnPoint);
        }
        return spawns;
    }

    /**
     * Creates an unlocked transition helper instance.
     *
     * @param area transition area
     * @param targetLevelId destination level
     * @param targetSpawnId destination spawn id
     * @param label transition label
     * @return transition instance
     */
    private static TransitionZone transition(TransitionZone.Area area, LevelId targetLevelId, String targetSpawnId, String label) {
        return new TransitionZone(area, targetLevelId, targetSpawnId, label);
    }

    /**
     * Creates a locked transition helper instance.
     *
     * @param area transition area
     * @param targetLevelId destination level
     * @param targetSpawnId destination spawn id
     * @param label transition label
     * @param requiredKeys required keys to unlock transition
     * @return transition instance
     */
    private static TransitionZone transition(TransitionZone.Area area, LevelId targetLevelId, String targetSpawnId,
                                             String label, java.util.List<KeyId> requiredKeys) {
        return new TransitionZone(area, targetLevelId, targetSpawnId, label, requiredKeys);
    }

    /**
     * Creates a key pickup helper instance.
     *
     * @param id pickup id
     * @param x left position
     * @param y bottom position
     * @param width pickup width
     * @param height pickup height
     * @param keyId key granted by pickup
     * @return key pickup instance
     */
    private static KeyPickup keyPickup(String id, float x, float y, float width, float height, KeyId keyId) {
        return new KeyPickup(id, x, y, width, height, keyId);
    }

    /**
     * Creates an interactable helper instance.
     *
     * @param id interactable id
     * @param x left position
     * @param y bottom position
     * @param width interactable width
     * @param height interactable height
     * @param type interactable type
     * @param hiddenKey optional hidden key
     * @return interactable instance
     */
    private static InteractableObject interactable(String id, float x, float y, float width, float height,
                                                   InteractableType type, KeyId hiddenKey) {
        return new InteractableObject(id, x, y, width, height, type, hiddenKey);
    }

    /**
     * Validates that each key id is placed exactly once in the whole catalog.
     *
     * @param levels level map to validate
     */
    private static void validateUniqueKeyPlacements(Map<LevelId, LevelDefinition> levels) {
        EnumSet<KeyId> seen = EnumSet.noneOf(KeyId.class);
        for (LevelDefinition level : levels.values()) {
            validatePickupKeys(seen, level);
            validateInteractableKeys(seen, level);
        }
    }

    /**
     * Registers key pickups for uniqueness validation.
     *
     * @param seen set of already seen keys
     * @param level level to scan
     */
    private static void validatePickupKeys(EnumSet<KeyId> seen, LevelDefinition level) {
        for (KeyPickup keyPickup : level.getKeyPickups()) {
            registerUniqueKeyPlacement(seen, keyPickup.getKeyId(), level.getId(), keyPickup.getId());
        }
    }

    /**
     * Registers hidden interactable keys for uniqueness validation.
     *
     * @param seen set of already seen keys
     * @param level level to scan
     */
    private static void validateInteractableKeys(EnumSet<KeyId> seen, LevelDefinition level) {
        for (InteractableObject interactableObject : level.getInteractableObjects()) {
            interactableObject.getHiddenKey().ifPresent(keyId ->
                registerUniqueKeyPlacement(seen, keyId, level.getId(), interactableObject.getId())
            );
        }
    }

    /**
     * Registers a key placement and throws on duplicates.
     *
     * @param seen set of already seen keys
     * @param keyId key to register
     * @param levelId level containing the key
     * @param sourceId source object identifier
     * @throws IllegalStateException when key placement is duplicated
     */
    private static void registerUniqueKeyPlacement(EnumSet<KeyId> seen, KeyId keyId, LevelId levelId, String sourceId) {
        if (!seen.add(keyId)) {
            throw new IllegalStateException("Duplicate key placement for " + keyId + " at " + levelId + "/" + sourceId);
        }
    }
}

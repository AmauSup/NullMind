package fr.supdevinci.games.world;

import com.badlogic.gdx.graphics.Color;
import fr.supdevinci.games.config.GameConfig;
import fr.supdevinci.games.progress.KeyId;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Stores the immutable set of level definitions available to the game.
 */
public final class LevelCatalog {
    private static final String START = "start";
    private static final String FROM_HOUSE = "fromHouse";
    private static final String FROM_LIBRARY = "fromLibrary";
    private static final String FROM_PORT = "fromPort";
    private static final String FROM_CEMETERY = "fromCemetery";
    private static final String FROM_HUB = "fromHub";
    private static final String FROM_CELLAR = "fromCellar";
    private static final String RETOUR_HUB = "Retour hub";

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

    public Map<LevelId, LevelDefinition> getLevels() {
        return levels;
    }

    private static LevelDefinition createHub() {
        Map<String, SpawnPoint> spawns = new LinkedHashMap<>();
        spawns.put(START, spawn(START, 470f, 260f));
        spawns.put(FROM_HOUSE, spawn(FROM_HOUSE, 470f, 150f));
        spawns.put(FROM_LIBRARY, spawn(FROM_LIBRARY, 250f, 260f));
        spawns.put(FROM_PORT, spawn(FROM_PORT, 470f, 340f));
        spawns.put(FROM_CEMETERY, spawn(FROM_CEMETERY, 700f, 260f));

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

    private static LevelDefinition createHouse() {
        // Maison = une seule grande map avec pièces suggérées par murs internes.
        Map<String, SpawnPoint> spawns = new LinkedHashMap<>();
        spawns.put(START, spawn(START, 220f, 180f));
        spawns.put(FROM_HUB, spawn(FROM_HUB, 470f, 430f));
        spawns.put(FROM_CELLAR, spawn(FROM_CELLAR, 470f, 120f));

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
                    java.util.List.of(KeyId.LIBRARY_KEY, KeyId.PORT_KEY, KeyId.CEMETERY_KEY))
                ,
                transition(TransitionZone.Area.of(450f, 500f, 60f, 16f), LevelId.HUB, FROM_HOUSE, "Sortie maison")
                ),
                java.util.List.of()
            ),
            spawns,
            START
        );
    }

    private static LevelDefinition createCellar() {
        Map<String, SpawnPoint> spawns = new LinkedHashMap<>();
        spawns.put(FROM_HOUSE, spawn(FROM_HOUSE, 452f, 410f));

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

    private static LevelDefinition createLibrary() {
        Map<String, SpawnPoint> spawns = new LinkedHashMap<>();
        spawns.put(FROM_HUB, spawn(FROM_HUB, 840f, 260f));

        java.util.List<InteractableObject> libraryBooks = java.util.List.of(
            interactable("book_1", 210f, 110f, 34f, 40f, InteractableType.BOOK, null),
            interactable("book_2", 290f, 170f, 34f, 40f, InteractableType.BOOK, null),
            interactable("book_3", 370f, 230f, 34f, 40f, InteractableType.BOOK, KeyId.LIBRARY_KEY),
            interactable("book_4", 450f, 280f, 34f, 40f, InteractableType.BOOK, null),
            interactable("book_5", 530f, 130f, 34f, 40f, InteractableType.BOOK, null),
            interactable("book_6", 610f, 210f, 34f, 40f, InteractableType.BOOK, null)
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

    private static LevelDefinition createPort() {
        Map<String, SpawnPoint> spawns = new LinkedHashMap<>();
        spawns.put(FROM_HUB, spawn(FROM_HUB, 470f, 90f));

        return LevelDefinition.flat(
            LevelId.PORT,
            LevelDefinition.Size.of(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT),
            LevelDefinition.Colors.of(Color.valueOf("4A5D6E"), Color.valueOf("8F6B45")),
            LevelDefinition.FlatContent.of(
                java.util.List.of(
                // Zone pêche / bâtiments en bas
                obstacle(80f, 70f, 180f, 90f),
                obstacle(300f, 80f, 140f, 80f),
                obstacle(500f, 70f, 180f, 90f),
                obstacle(740f, 80f, 140f, 80f),

                // Pont central (rambardes)
                obstacle(390f, 170f, 30f, 170f),
                obstacle(540f, 170f, 30f, 170f),

                // Mer au-dessus du pont
                obstacle(0f, 360f, GameConfig.WORLD_WIDTH, 180f)
                ),
                java.util.List.of(
                transition(TransitionZone.Area.of(440f, 30f, 80f, 24f), LevelId.HUB, FROM_PORT, RETOUR_HUB)
                ),
                java.util.List.of(
                keyPickup("portToken", 470f, 320f, 26f, 26f, KeyId.PORT_KEY)
                )
            ),
            spawns,
            FROM_HUB
        );
    }

    private static LevelDefinition createCemetery() {
        Map<String, SpawnPoint> spawns = new LinkedHashMap<>();
        spawns.put(FROM_HUB, spawn(FROM_HUB, 110f, 250f));

        java.util.List<InteractableObject> graves = java.util.List.of(
            interactable("grave_1", 180f, 120f, 40f, 28f, InteractableType.GRAVE, null),
            interactable("grave_2", 340f, 120f, 40f, 28f, InteractableType.GRAVE, null),
            interactable("grave_3", 500f, 200f, 40f, 28f, InteractableType.GRAVE, null),
            interactable("grave_4", 700f, 280f, 40f, 28f, InteractableType.GRAVE, KeyId.CEMETERY_KEY),
            interactable("grave_5", 260f, 200f, 40f, 28f, InteractableType.GRAVE, null)
        );

        return LevelDefinition.flat(
            LevelId.CEMETERY,
            LevelDefinition.Size.of(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT),
            LevelDefinition.Colors.of(Color.valueOf("2D3430"), Color.valueOf("6E7B73")),
            LevelDefinition.FlatContent.of(
                java.util.List.of(
                // Église en haut
                obstacle(360f, 390f, 240f, 120f),

                // Tombes (grille)
                obstacle(180f, 120f, 40f, 28f),
                obstacle(260f, 120f, 40f, 28f),
                obstacle(340f, 120f, 40f, 28f),
                obstacle(420f, 120f, 40f, 28f),
                obstacle(500f, 120f, 40f, 28f),
                obstacle(580f, 120f, 40f, 28f),
                obstacle(660f, 120f, 40f, 28f),
                obstacle(740f, 120f, 40f, 28f),

                obstacle(180f, 200f, 40f, 28f),
                obstacle(260f, 200f, 40f, 28f),
                obstacle(340f, 200f, 40f, 28f),
                obstacle(500f, 200f, 40f, 28f),
                obstacle(580f, 200f, 40f, 28f),
                obstacle(660f, 200f, 40f, 28f),
                obstacle(740f, 200f, 40f, 28f),

                obstacle(220f, 280f, 40f, 28f),
                obstacle(300f, 280f, 40f, 28f),
                obstacle(380f, 280f, 40f, 28f),
                obstacle(460f, 280f, 40f, 28f),
                obstacle(620f, 280f, 40f, 28f),
                obstacle(700f, 280f, 40f, 28f)
                ),
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

    private static Obstacle obstacle(float x, float y, float width, float height) {
        return new Obstacle(x, y, width, height);
    }

    private static SpawnPoint spawn(String id, float x, float y) {
        return new SpawnPoint(id, x, y);
    }

    private static TransitionZone transition(TransitionZone.Area area, LevelId targetLevelId, String targetSpawnId, String label) {
        return new TransitionZone(area, targetLevelId, targetSpawnId, label);
    }

    private static TransitionZone transition(TransitionZone.Area area, LevelId targetLevelId, String targetSpawnId,
                                             String label, java.util.List<KeyId> requiredKeys) {
        return new TransitionZone(area, targetLevelId, targetSpawnId, label, requiredKeys);
    }

    private static KeyPickup keyPickup(String id, float x, float y, float width, float height, KeyId keyId) {
        return new KeyPickup(id, x, y, width, height, keyId);
    }

    private static InteractableObject interactable(String id, float x, float y, float width, float height,
                                                   InteractableType type, KeyId hiddenKey) {
        return new InteractableObject(id, x, y, width, height, type, hiddenKey);
    }
}

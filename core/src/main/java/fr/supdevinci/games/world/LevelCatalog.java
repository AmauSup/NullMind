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
        spawns.put("start",        spawn("start",        470f, 260f));
        spawns.put("fromHouse",    spawn("fromHouse",    470f, 150f));
        spawns.put("fromLibrary",  spawn("fromLibrary",  250f, 260f));
        spawns.put("fromPort",     spawn("fromPort",     470f, 340f));
        spawns.put("fromCemetery", spawn("fromCemetery", 700f, 260f));

        return new LevelDefinition(
            LevelId.HUB,
            GameConfig.WORLD_WIDTH,
            GameConfig.WORLD_HEIGHT,
            Color.valueOf("4A4F57"),
            Color.valueOf("B8A98A"),
            java.util.List.of(
                // Maison (bas)
                obstacle(380f, 20f, 200f, 100f),
                // Bibliothèque (gauche)
                obstacle(20f, 180f, 180f, 180f),
                // Cimetière (droite)
                obstacle(760f, 180f, 180f, 180f),
                // Port (haut)
                obstacle(350f, 390f, 260f, 130f)
            ),
            java.util.List.of(
                transition(440f, 120f, 80f, 16f, LevelId.HOUSE, "fromHub", "Maison"),
                transition(200f, 250f, 16f, 60f, LevelId.LIBRARY, "fromHub", "Bibliothèque"),
                transition(744f, 250f, 16f, 60f, LevelId.CEMETERY, "fromHub", "Cimetière"),
                transition(450f, 370f, 60f, 16f, LevelId.PORT, "fromHub", "Port")
            ),
            java.util.List.of(),
            spawns,
            "start"
        );
    }

    private static LevelDefinition createHouse() {
        // Maison = une seule grande map avec pièces suggérées par murs internes.
        Map<String, SpawnPoint> spawns = new LinkedHashMap<>();
        spawns.put("start",      spawn("start",      220f, 180f));
        spawns.put("fromHub",    spawn("fromHub",    470f, 120f));
        spawns.put("fromCellar", spawn("fromCellar", 470f, 430f));

        return new LevelDefinition(
            LevelId.HOUSE,
            GameConfig.WORLD_WIDTH,
            GameConfig.WORLD_HEIGHT,
            Color.valueOf("5C3D2E"),
            Color.valueOf("D2B48C"),
            java.util.List.of(
                // Enveloppe extérieure avec porte bas (hub) et porte haut (cave)
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
                transition(450f, 60f, 60f, 16f, LevelId.HUB, "fromHouse", "Sortie maison"),
                transition(440f, 500f, 80f, 16f, LevelId.CELLAR, "fromHouse", "Porte cave",
                    java.util.List.of(KeyId.LIBRARY_KEY, KeyId.PORT_KEY, KeyId.CEMETERY_KEY))
            ),
            java.util.List.of(),
            spawns,
            "start"
        );
    }

    private static LevelDefinition createCellar() {
        Map<String, SpawnPoint> spawns = new LinkedHashMap<>();
        spawns.put("fromHouse", spawn("fromHouse", 452f, 410f));

        return new LevelDefinition(
            LevelId.CELLAR,
            GameConfig.WORLD_WIDTH,
            GameConfig.WORLD_HEIGHT,
            Color.valueOf("1A1A1D"),
            Color.valueOf("6A4C93"),
            java.util.List.of(
                obstacle(180f, 140f, 120f, 220f),
                obstacle(390f, 200f, 180f, 60f),
                obstacle(700f, 140f, 120f, 220f)
            ),
            java.util.List.of(
                transition(430f, 470f, 100f, 40f, LevelId.HOUSE, "fromCellar", "Retour maison")
            ),
            java.util.List.of(),
            spawns,
            "fromHouse"
        );
    }

    private static LevelDefinition createLibrary() {
        Map<String, SpawnPoint> spawns = new LinkedHashMap<>();
        spawns.put("fromHub", spawn("fromHub", 840f, 260f));

        return new LevelDefinition(
            LevelId.LIBRARY,
            GameConfig.WORLD_WIDTH,
            GameConfig.WORLD_HEIGHT,
            Color.valueOf("2E241C"),
            Color.valueOf("D8B56A"),
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
                transition(890f, 220f, 40f, 100f, LevelId.HUB, "fromLibrary", "Retour hub")
            ),
            java.util.List.of(
                keyPickup("libraryKey", 760f, 290f, 26f, 26f, KeyId.LIBRARY_KEY)
            ),
            spawns,
            "fromHub"
        );
    }

    private static LevelDefinition createPort() {
        Map<String, SpawnPoint> spawns = new LinkedHashMap<>();
        spawns.put("fromHub", spawn("fromHub", 470f, 90f));

        return new LevelDefinition(
            LevelId.PORT,
            GameConfig.WORLD_WIDTH,
            GameConfig.WORLD_HEIGHT,
            Color.valueOf("4A5D6E"),
            Color.valueOf("8F6B45"),
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
                transition(440f, 30f, 80f, 24f, LevelId.HUB, "fromPort", "Retour hub")
            ),
            java.util.List.of(
                keyPickup("portToken", 470f, 320f, 26f, 26f, KeyId.PORT_KEY)
            ),
            spawns,
            "fromHub"
        );
    }

    private static LevelDefinition createCemetery() {
        Map<String, SpawnPoint> spawns = new LinkedHashMap<>();
        spawns.put("fromHub", spawn("fromHub", 110f, 250f));

        return new LevelDefinition(
            LevelId.CEMETERY,
            GameConfig.WORLD_WIDTH,
            GameConfig.WORLD_HEIGHT,
            Color.valueOf("2D3430"),
            Color.valueOf("6E7B73"),
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
                transition(30f, 220f, 30f, 100f, LevelId.HUB, "fromCemetery", "Retour hub")
            ),
            java.util.List.of(
                keyPickup("cemeteryKey", 790f, 320f, 26f, 26f, KeyId.CEMETERY_KEY)
            ),
            spawns,
            "fromHub"
        );
    }

    private static Obstacle obstacle(float x, float y, float width, float height) {
        return new Obstacle(x, y, width, height);
    }

    private static SpawnPoint spawn(String id, float x, float y) {
        return new SpawnPoint(id, x, y);
    }

    private static TransitionZone transition(
        float x,
        float y,
        float width,
        float height,
        LevelId targetLevelId,
        String targetSpawnId,
        String label
    ) {
        return new TransitionZone(x, y, width, height, targetLevelId, targetSpawnId, label);
    }

    private static TransitionZone transition(
        float x,
        float y,
        float width,
        float height,
        LevelId targetLevelId,
        String targetSpawnId,
        String label,
        java.util.List<KeyId> requiredKeys
    ) {
        return new TransitionZone(x, y, width, height, targetLevelId, targetSpawnId, label, requiredKeys);
    }

    private static KeyPickup keyPickup(String id, float x, float y, float width, float height, KeyId keyId) {
        return new KeyPickup(id, x, y, width, height, keyId);
    }
}

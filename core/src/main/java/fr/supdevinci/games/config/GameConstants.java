package fr.supdevinci.games.config;

/**
 * Centralized constants for the entire game.
 * Avoids hardcoded strings and magic numbers throughout the codebase.
 */
public final class GameConstants {
    
    // ==================== Spawn Point IDs ====================
    public static final String SPAWN_START = "start";
    public static final String SPAWN_FROM_HUB = "fromHub";
    public static final String SPAWN_FROM_HOUSE = "fromHouse";
    public static final String SPAWN_FROM_LIBRARY = "fromLibrary";
    public static final String SPAWN_FROM_CEMETERY = "fromCemetery";
    public static final String SPAWN_FROM_PORT = "fromPort";
    public static final String SPAWN_FROM_CELLAR = "fromCellar";
    // ==================== UI Messages ====================
    public static final String MSG_ALREADY_EXPLORED = "Déjà exploré.";
    public static final String MSG_NOTHING_USEFUL = "Rien d'utile ici.";
    public static final String MSG_NOTHING_TO_INSPECT = "Rien à inspecter ici.";
    public static final String MSG_KEY_FOUND = "Tu as trouvé : ";
    public static final String MSG_TRANSITION_LOCKED = " verrouillée — ";
    public static final String MSG_KEYS_MISSING_1 = " clé(s) manquante(s) sur ";
    public static final String MSG_TRANSITION_TO = "Transition vers : ";
    public static final String MSG_JUMP_SUCCESS = "Saut réussi.";
    public static final String MSG_FELL_IN_WATER = "Tu es tombé à l'eau.";
    public static final String MSG_NEW_KEY = "Nouvelle clé : ";

    // ==================== HUD Text ====================
    public static final String HUD_CONTROLS = "Déplacement : WASD / flèches | Interagir : E | Saut : Espace";
    public static final String HUD_KEYS = "Clés : ";
    public static final String HUD_POSITION = "Position : (";
    public static final String HUD_LEVEL = "Niveau : ";
    public static final String HUD_VISITED_LEVELS = "Niveaux visités : ";
    public static final String HUD_STATUS = "Statut : ";
    public static final String HUD_TRANSITIONS = "Transitions :";
    public static final String HUD_NO_KEYS = "aucune";
    public static final String HUD_REQUIRED_KEYS_SUFFIX = " clé(s) requise(s)]";
    public static final String HUD_REQUIRED_KEYS_PREFIX = " [";
    public static final String TITLE_SCREEN_TEXT = "POC libGDX\n\nPeurs et traumatismes\n\nEntrée / Espace : démarrer\nÉchap : revenir ici pendant le jeu";
    public static final String STATUS_EMPTY = "Aucun événement.";

    // ==================== Interaction & Physics ====================
    public static final float INTERACTION_RANGE = 26f;
    public static final float CAMERA_LERP_SPEED = 6f;

    // ==================== Token Separator ====================
    public static final String TOKEN_SEPARATOR = ":";

    // ==================== Interactable IDs ====================
    public static final String BOOK_1 = "book_1";
    public static final String BOOK_2 = "book_2";
    public static final String BOOK_3 = "book_3";
    public static final String BOOK_4 = "book_4";
    public static final String BOOK_5 = "book_5";
    public static final String BOOK_6 = "book_6";
    public static final String BOOK_WITH_KEY = "book_3";

    public static final String GRAVE_1 = "grave_1";
    public static final String GRAVE_2 = "grave_2";
    public static final String GRAVE_3 = "grave_3";
    public static final String GRAVE_4 = "grave_4";
    public static final String GRAVE_5 = "grave_5";
    public static final String GRAVE_WITH_KEY = "grave_4";

    private GameConstants() {
        // Utility class
    }
}

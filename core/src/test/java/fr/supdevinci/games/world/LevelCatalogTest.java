package fr.supdevinci.games.world;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelCatalogTest {
    @Test
    void shouldContainAllRequiredPrototypeLevels() {
        LevelCatalog catalog = LevelCatalog.createDefault();

        for (LevelId levelId : LevelId.values()) {
            assertTrue(catalog.get(levelId) != null);
        }
    }

    @Test
    void hubShouldLinkToFourExplorationLevels() {
        LevelCatalog catalog = LevelCatalog.createDefault();

        Set<LevelId> targets = catalog.get(LevelId.HUB).getTransitionZones().stream()
            .map(TransitionZone::getTargetLevelId)
            .collect(Collectors.toSet());

        assertTrue(targets.contains(LevelId.HOUSE));
        assertTrue(targets.contains(LevelId.LIBRARY));
        assertTrue(targets.contains(LevelId.PORT));
        assertTrue(targets.contains(LevelId.CEMETERY));
    }

    @Test
    void houseShouldLinkToCellarWithThreeKeysRequired() {
        LevelCatalog catalog = LevelCatalog.createDefault();

        TransitionZone caveTransition = catalog.get(LevelId.HOUSE).getTransitionZones().stream()
            .filter(t -> t.getTargetLevelId() == LevelId.CELLAR)
            .findFirst()
            .orElseThrow();

        assertEquals(3, caveTransition.getRequiredKeys().size());
    }

    @Test
    void shouldFallbackToDefaultSpawnWhenSpawnIdIsUnknown() {
        LevelCatalog catalog = LevelCatalog.createDefault();

        SpawnPoint fallback = catalog.get(LevelId.HOUSE).resolveSpawn("unknown-spawn");

        assertNotNull(fallback);
        assertEquals("start", fallback.getId());
    }

    @Test
    void shouldHaveAtLeastOneLockedTransitionInGameFlow() {
        LevelCatalog catalog = LevelCatalog.createDefault();

        long lockedTransitions = java.util.Arrays.stream(LevelId.values())
            .flatMap(levelId -> catalog.get(levelId).getTransitionZones().stream())
            .filter(transition -> !transition.getRequiredKeys().isEmpty())
            .count();

        assertTrue(lockedTransitions >= 1);
    }
}

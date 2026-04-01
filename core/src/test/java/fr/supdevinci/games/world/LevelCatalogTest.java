package fr.supdevinci.games.world;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelCatalogTest {
    @Test
    void shouldContainAllRequiredPrototypeLevels() {
        LevelCatalog catalog = LevelCatalog.createDefault();

        assertEquals(Set.of(LevelId.values()), catalog.getLevels().keySet());
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
}

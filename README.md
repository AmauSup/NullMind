# Trauma Worlds POC

Prototype jouable en Java avec libGDX, pensé comme base propre et extensible pour un jeu centré sur des niveaux symboliques liés aux peurs et traumatismes.

## Hypothèses retenues

- Le POC privilégie une base saine plutôt qu'un gameplay complet.
- Les maps utilisent des rectangles et des couleurs pour éviter toute dépendance à des assets artistiques.
- Une seule entité jouable est nécessaire à ce stade.
- Les niveaux sont fixes à l'écran pour garder la navigation simple et lisible.

## Arborescence utile

- `core/src/main/java/fr/supdevinci/games/Main.java` : point d'entrée métier libGDX.
- `core/src/main/java/fr/supdevinci/games/screen` : écrans `TitleScreen` et `GameScreen`.
- `core/src/main/java/fr/supdevinci/games/world` : définitions de maps, transitions, obstacles et état du monde.
- `core/src/main/java/fr/supdevinci/games/logic` : logique de déplacement testable hors rendu.
- `core/src/main/java/fr/supdevinci/games/render` : rendu du monde et du HUD.
- `core/src/main/java/fr/supdevinci/games/input` : lecture clavier.
- `core/src/test/java/fr/supdevinci/games` : tests unitaires de logique.
- `lwjgl3/src/main/java/fr/supdevinci/games/lwjgl3/Lwjgl3Launcher.java` : launcher desktop.

## Architecture

- `Main` instancie les ressources partagées et gère le changement d'écran.
- `TitleScreen` démontre le système de screens sans complexité inutile.
- `GameScreen` orchestre la boucle de jeu : input, update, rendu, HUD.
- `GameWorld` contient l'état mutable de la session et les transitions entre maps.
- `LevelCatalog` décrit les 6 maps du POC : Hub, Maison, Cave, Bibliothèque, Aquarium, Cimetière.
- `PlayerMovementService` porte la logique testable de déplacement, de collision simple et de clamp dans la map.
- `GameAssets` centralise les ressources libGDX à libérer dans `dispose()`.

## Dépendances Gradle

### Plugins

- `java-library` via le build parent pour le module `core`
- `application` pour le launcher `lwjgl3`

### Dépendances principales

- `com.badlogicgames.gdx:gdx`
- `com.badlogicgames.gdx:gdx-backend-lwjgl3`
- `com.badlogicgames.gdx:gdx-platform:natives-desktop`

### Dépendances de test

- `org.junit.jupiter:junit-jupiter-api`
- `org.junit.jupiter:junit-jupiter-engine`

## Commandes utiles

- Lancer le jeu : `./gradlew lwjgl3:run` ou `gradlew.bat lwjgl3:run`
- Lancer les tests : `./gradlew core:test` ou `gradlew.bat core:test`
- Construire le projet : `./gradlew build` ou `gradlew.bat build`

## Ce que couvre le POC

- déplacement au clavier avec `WASD` ou flèches
- 6 maps simples mais réelles dans la structure du jeu
- transitions par zones réutilisables
- collisions simples avec obstacles et bords de map
- HUD minimal avec nom de la map et sorties visibles
- base testable sur la logique non graphique

## Stratégie de test

- Test unitaire du déplacement libre
- Test unitaire du clamp sur les bords du monde
- Test unitaire du blocage par obstacle
- Test du catalogue de niveaux et des transitions du hub
- Test de transition du monde entre le hub et la maison

Le rendu libGDX n'est pas testé ici : il dépend fortement de la boucle graphique. La priorité est de tester les règles métier qui risquent de casser lors des évolutions futures.

## Extension naturelle après le POC

- Ennemis : ajouter une interface d'entité mise à jour par `GameWorld`
- Inventaire / clés : introduire un `Inventory` dans l'état de session
- Triggers narratifs : ajouter des zones d'événements proches de `TransitionZone`
- Son / bruit : créer un système d'événements de gameplay consommé par l'audio et l'IA
- Score / progression : enrichir l'état de session avec objectifs, flags et sauvegarde
- IA simple : injecter des comportements par niveau sans mélanger logique et rendu

## Cycle de vie des ressources

- Création : dans `Main.create()`
- Utilisation : par injection légère via `GameContext`
- Libération : dans `Main.dispose()` pour les ressources partagées, et via `Screen.dispose()` lors d'un changement d'écran

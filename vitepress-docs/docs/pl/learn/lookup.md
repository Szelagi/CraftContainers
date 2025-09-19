# Pobieranie instancji

## Container

### Wszystkie instancje
Zwraca niezmienialny zbiór wszystkich aktywnych kontenerów.
```java
var instances = Container.containers();
```

### Pobieranie kontenera, w którym jest gracz

#### Ogólny kontener
Zwraca instancję `Container`, w której aktualnie znajduje się dany gracz.
Jeśli gracz nie należy do żadnego kontenera, metoda zwraca `null`.
```java
var container = Container.getForPlayer(player);
```

#### Kontener określonego typu (generyczny)
Zwraca instancję typu generycznego dziedziczącego po `Container`.
Jeśli gracz nie jest w żadnym kontenerze lub kontener nie jest instancją podanego typu, metoda zwraca `null`.
```java
var skyWarsContainer = Container.getForPlayer(player, SkyWarsContainer.class);
if (skyWarsContainer != null) {
    // The player is in a container of type SkyWars
} else {
    // The player is not in a container of type SkyWars
}
```

## GameMap

### Wszystkie instancje
Zwraca niezmienialny zbiór wszystkich aktywnych map gier.
```java
var instances = GameMap.gameMaps();
```

### Pobieranie instancji na podstawie lokalizacji, entity lub bloku

#### Dla lokalizacji
Zwraca instancję `GameMap`, w której znajduje się podana lokalizacja.
Jeśli lokalizacja nie należy do żadnej mapy, zwróci null.
```java
var gameMap = GameMap.getForLocation(location);
```
Można także bezpośrednio pobrać kontener powiązany z daną lokalizacją
```java
var container = GameMap.getContainerForLocation(location);
```

#### Dla entity
```java
var gameMap = GameMap.getForEntity(entity);
var container = GameMap.getContainerForEntity(entity);
```


#### Dla bloku
```java
var gameMap = GameMap.getForBlock(block);
var container = GameMap.getContainerForBlock(block);
```
# Allokatory
Allokatory odpowiadają za przydzielanie przestrzeni dla map, na których będzie rozgrywać się gra.
Możesz modyfikować wbudowane allokatory, zmieniając w nich środowisko lub domyślną logikę, albo tworzyć własne, np. działające na SlimeWorld.

## Wbudowane allokatory
Wbudowane allokatory działają na zasadzie przydzielania fragmentów specjalnego świata.

### RecyclingRegionAllocator
- Najlepiej sprawdza się przy długotrwałych alokacjach.
- Działa w obrębie jednego świata Minecraft.
- Ponownie wykorzystuje teren, który został wcześniej zwolniony.
- Wymaga ręcznego oczyszczenia terenu przed jego zwolnieniem.

```java
Allocators.defaultRecyclingAllocator();
```

### LazyRegionAllocator
- Idealny do krótkotrwałych i częstych alokacji.
- Automatycznie i bardzo wydajnie czyści teren, więc ręczne czyszczenie nie jest potrzebne.
- Zużywa więcej miejsca na dysku.

```java
Allocators.defaultLazyAllocator();
```

::: danger
Nie używaj tego allokatora do długotrwałych kontenerów ani kontenerów działających przez cały czas działania serwera.
Może to spowodować zablokowanie świata, który będzie czekał na zwolnienie terenu, który nigdy się nie zwolni.
:::

### Modyfikowanie wbudowanych allokatorów

#### Możesz zmieniać różne parametry wbudowanych allokatorów:
- `worldMaxAlloc` – po ilu przydzieleniach mapy świat ma zostać zresetowany.
- `regionSize` – rozmiar przydzielonych obszarów.
- `spaceGap` – odległość między obszarami.
- `worldCreatorConsumer` – konsumer do modyfikowania WorldCreator.
- `worldConsumer` – konsumer wywoływany po utworzeniu nowego świata.
- `worldDestroyConsumer` – konsumer wywoływany po zniszczeniu świata.

#### Domyślna logika
- `Allocators::registerDefaultWorldLogic` - ustawia domyślną logikę przy tworzeniu świata (np. zawsze noc, brak naturalnego spawnu mobów).
- `Allocators::unregisterDefaultWorldLogic` - usuwa domyślną logikę przy usuwaniu świata.

::: tip Jeśli domyślna logika nie pasuje do Twojego projektu, możesz:
- Utworzyć nowy allokator na bazie domyślnego z włąsną logiką.
- Zaprojektować własny allokator.
:::

#### Przykłady modyfikacji wbudowanych allokatorów:
```java
var modyfiedAlloator = new LazyRegionAllocator(
        worldMaxAlloc, 
        regionSize,
        spaceGap,
        (worldCreator) -> worldCreator.environment(World.Environment.NETHER),
        Allocators::registerWorld,
        Allocators::unregisterWorld));
```

```java
var modyfiedAlloator = new RecyclingRegionAllocator(
        regionSize,
        spaceGap,
        (worldCreator) -> worldCreator.environment(World.Environment.END),
        Allocators::registerWorld,
        Allocators::unregisterWorld));
```

::: warning
Dla tego samego typu allokatora używaj jednej wspólnej instancji dla wszystkich map.
Tworzenie wielu instancji tego samego typu jest nieefektywne.
:::

### Tworzenie własnej logiki świata
Poniżej przykład własnej logiki, która ustawia czas na noc i rejestruje listener dla świata:

```java
private static Map<World, BukkitTask> worldBukkitTaskMap = new HashMap<>();
private static Map<World, Listener> worldListenerMap = new HashMap<>();

public static void registerDefaultWorldLogic(World world) {
    var task = Scheduler.runTaskTimer(
    () -> world.setTime(13000L),
    Time.zero(),
    Time.seconds(450));
    worldBukkitTaskMap.put(world, task);

    var listener = new MyWorldListener(world);
    Bukkit.getPluginManager().registerEvents(listener, CraftContainers.instance());
    worldListenerMap.put(world, listener);
}
    
public static void unregisterDefaultWorldLogic(World world) {
    var task = worldBukkitTaskMap.remove(world);
    task.cancel();

    var listener = worldListenerMap.remove(world);
    HandlerList.unregisterAll(listener);
}
```
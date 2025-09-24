# Koncepcja komponentów

## Czym są komponenty?
Komponenty to samodzielne jednostki w projekcie realizujące jedną, konkretną funkcjonalność. Ich projektowanie powinno zapewniać maksymalną niezależność i możliwość ponownego użycia w różnych projektach.

## Cykl życia komponentu

|                                  | NOT_INITIALIZED | RUNNING | SHUTDOWN |
|:---------------------------------|:---------------:|:-------:|:--------:|
| **Obecność w drzewie kontenera** | ❌              | ✅      | ❌       |
| Był już kiedyś aktywny           | ❌              | ❔      | ✅       |
| Posiada referencję do rodzica    | ✅              | ✅      | ✅       |

### Obecność w drzewie kontenera oznacza, że komponent:
- jest aktywny i wpływa na kontener,
- jest możliwy do wyszukania w strukturze,
- może posiadać zadania (Bukkit),
- może zarządzać zdarzeniami (Bukkit),
- może nasłuchiwać zdarzeń (@SAPIEvent).
- może posiadać podrzędne komponenty,

## Dodatkowe informacje i powiązania

- [Zdarzenia komponentów](/pl/learn/internal-events.md) – każdy komponent reaguje na zdarzenia związane ze swoim stanem oraz graczami.
- [Zadania bukkit](/pl/learn/tasks.md) – komponenty mogą posiadać przypisane zadania Bukkit aktywne podczas działania komponentu.
- [Zdarzenia bukkit](/pl/learn/listeners.md) - komponenty mogą reagować na zdarzenia Bukkit.
- [Hierarchia i podrzędne komponenty](/pl/learn/nested-trees.md) – komponenty są zorganizowane w hierarchię z przetwarzaniem zdarzeń w strukturze drzewa.
- [Własne zdarzenia](/pl/learn/custom-events.md) – komponenty mogą emitować i odbierać własne zdarzenia.


## Typy komponentów

#### Container
Główny komponent projektu, pełni rolę korzenia w strukturze i zarządza graczami. Do niego dołączane są wszystkie pozostałe komponenty.

- `addPlayer(player)` - dodaje gracza do kontenera
- `removePlayer(player)` - usuwa gracza z kontenera
- `setGameMap(gameMap)` - zmienia mapę rozgrywki na inną

```java
public class MyContainer extends Container {
    public MyContainer(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    protected @NotNull GameMap defaultBoard() {
        return new MyGameMap(this);
    }

    // optional
    @Override
    public void onComponentInit(ComponentConstructor event) {
        new NoCreatureSpawn(this).start();
    }
}
```

#### GameMap
Komponent odpowiedzialny za reprezentację mapy w kontenerze.
Przykłady użycia mapy znajdziesz w sekcji [Generowanie mapy](/pl/learn/gamemap-generating.md).

#### Controller
Komponent umożliwiający implementację dodatkowej logiki lub zadań.

```java
public class NoCreatureNaturalSpawn extends Controller {
    public NoCreatureNaturalSpawn(Component component) {
        super(component);
    }

    @Override
    public Listeners defineListeners() {
        return super.defineListeners().add(MyListener.class);
    }

    private static final class MyListener implements Listener {
        @EventHandler(ignoreCancelled = true)
        public void onCreatureSpawn(CreatureSpawnEvent event) {
            if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL)
                return;
            var session = GameMapManager.container(event.getEntity());
            ListenerManager.first(session, getClass(), NoCreatureNaturalSpawn.class, noCreatureDrop -> {
                event.setCancelled(true);
            });
        }

    }
}
```

### Uruchamianie

```java
var container = new MyContainer(plugin);
container.start();
container.addPlayer(player);
```
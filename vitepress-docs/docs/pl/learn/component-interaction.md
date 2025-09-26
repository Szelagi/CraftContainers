# Komunikacja i współpraca komponentów
::: warning Uwaga
Jeżeli komponent nie znajduje się w [cyklu życia](/pl/learn/components.md)
**`RUNNING`**, nie może zostać wyszukany i nie otrzyma powiadomienia o zdarzeniu.
Pamiętaj o tym przy projektowaniu systemu, ponieważ może to prowadzić do niespójności stanu między komponentami.
:::

## Relacje między komponentami

### Relacja rodzic → dziecko
Wystarczy przechowywać referencję do dziecka. Jest to bezpieczne, gdy komponenty są w bliskiej relacji.

### Relacja dziecko → rodzic
Wystarczy przechowywać referencję do rodzica. Takie podejście jest bezpieczne przy bliskiej relacji komponentów.

::: warning
Jeżeli relacje między komponentami nie są bezpośrednie, nie przechowuj referencji do odległych komponentów.
Zamiast tego korzystaj z [przeszukiwania drzewa](/pl/learn/search.md)
i mechanizmów komunikacji komponentów.
:::

## Tree Event (broadcast)
Pozwala rozgłaszać zdarzenia w całym drzewie komponentów, bez określania konkretnych odbiorców.

### Zalety
- Nie wymaga definiowania konkretnych odbiorców.
- Event trafia do wszystkich komponentów w drzewie.

### Wady
- Iteracja po całym drzewie jest liniowa *(w przyszłości planowane indeksowanie)*.
- Struktura odbiorców jest mniej przewidywalna.


[Dowiedz się więcej o Tree Event.](/pl/learn/custom-events.md)

## Search & Invoke
Mechanizm Search & Invoke pozwala wyszukiwać konkretne komponenty i wywoływać na nich zdarzenia.

### Zalety
- Wysoka wydajność dzięki indeksowanemu wyszukiwaniu.
- Komponent może być wyszukiwany po swojej klasie, jej nadklasach oraz implementowanych interfejsach.
- Powiadamiane są tylko komponenty, które faktycznie powinny otrzymać zdarzenie.

### Wady
- Trzeba zdefiniować konkretnych odbiorców.
- Należy ręcznie wywołać metodę eventu dla każdego komponentu.

### Przykład implementacji – BedWars

#### Interfejs obsługi zdarzenia zniszczenia łóżka
```java
public interface IBedDestroyedHandler {
    void invokeBedDestroyed(Team attacker, Team victim);
}
```
Można też stworzyć ogólny interfejs, np. `IBedWarsHandler`, który obsłuży wszystkie zdarzenia.

#### Główny helper – core rozgrywki
Udostępnia funkcje i stan gry.
```java
public interface IBedWarsCore {
    List<Team> getTeams();

    @Nullable Team getTeamForPlayer(Player player);

    @Nullable Team getTeamForBed(Block block);
    
    void setItemGeneratorTier(int tier);

    // ...
}
```
Zmiana stanów w core może wywoływać eventy do innych komponentów zależnych.
Zaleca się umieszczenie core jak najbliżej korzenia drzewa i korzystanie z adnotacji `@Dependancy` w komponentach, które korzystają z niego.

#### Komponent logiki łóżek
Wykrywa zniszczenie łóżka, wprowadza logikę i informuje o tym inne komponenty.

```java
@SingletonComponent
public class BedLogic extends Controller {
    public static boolean isBedMaterial(Material material) {
        // some logic...
        return true;
    }

    public BedLogic(@NotNull Component parent) {
        super(parent);
    }

    @Override
    public Listeners defineListeners() {
        return super.defineListeners().add(MyListener.class);
    }

    private static class MyListener implements AdaptedListener {
        @EventHandler(ignoreCancelled = true)
        public void onBlockBreak(BlockBreakEvent event) {
            var player = event.getPlayer();
            var block = event.getBlock();

            if (!BedLogic.isBedMaterial(block.getType()))
                return;

            var container = Container.getForPlayer(player);
            first(container, BedLogic.class, bedLogic -> {

                var core = container.getComponent(IBedWarsCore.class);
                if (core == null) return;

                var attackerTeam = core.getTeamForPlayer(player);
                var victimTeam = core.getTeamForBed(block);

                if (attackerTeam == null || victimTeam == null) return;

                if (attackerTeam.equals(victimTeam)) {
                    event.setCancelled(true);
                    player.sendMessage("§cNie możesz zniszczyć swojego łóżka!");
                    return;
                }

                container.forEachComponents(IBedDestroyed.class, iBedDestroyed -> {
                    iBedDestroyed.invokeBedDestroyed(attackerTeam, victimTeam);
                });

            });
        }
    }
}
```

#### Komponent wyświetlający wiadomości
Reaguje na zdarzenie zniszczenia łóżka i wyświetla komunikat na czacie.

```java
@SingletonComponent
public class GameMessages extends Controller implements IBedDestroyed {
    private static final String BED_DESTROYED_MESSAGE = "%s%s Team §fdestroyed the bed of %s%s Team";

    public GameMessages(@NotNull Component parent) {
        super(parent);
    }

    @Override
    public void invokeBedDestroyed(Team attacker, Team victim) {
        var message = String.format(
                BED_DESTROYED_MESSAGE,
                attacker.getTextColor(),
                attacker.getName(),
                victim.getTextColor(),
                victim.getName());

        players().forEach(player -> player.sendMessage(message));
    }
}
```
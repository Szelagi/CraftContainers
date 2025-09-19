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

### Container
Główny komponent projektu, pełni rolę korzenia w strukturze i zarządza graczami. Do niego dołączane są wszystkie pozostałe komponenty.

- addPlayer(player) -- dodaje gracza do kontenera
- removePlayer(player) -- usuwa gracza z kontenera
- setGameMap(gameMap) -- zmienia mapę rozgrywki na inną

```java

```

### GameMap
Komponent odpowiedzialny za reprezentację mapy w kontenerze.


### Controller
Komponent umożliwiający implementację dodatkowej funkcjonalności.

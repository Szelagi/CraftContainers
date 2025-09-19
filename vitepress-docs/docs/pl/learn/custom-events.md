# Własne zdarzenia
Komponenty mogą komunikować się między sobą w ramach tego samego kontenera za pomocą zdarzeń.

::: info
Nasłuchiwanie zdarzeń jest możliwe wyłącznie dla komponentów aktywnych, czyli obecnych w drzewie kontenera.
*Zobacz więcej w [cyklu życia komponentu](/pl/learn/components.md#cykl-zycia-komponentu).*
:::

## Tworzenie własnego eventu

```java
public class TeamEliminatedEvent extends TreeEvent {
    private final Team team;
    private final Cause cause;

    public TeamEliminatedEvent(Team team, Cause cause) {
        this.team = team;
        this.cause = cause;
    }

    public Team getTeam() {
        return team;
    }

    public Cause getCause() {
        return cause;
    }
}
```

## Rozgłaszanie everntu

#### Tworzenie obiektu
```java
var event = new TeamEliminatedEvent(team, cause);
```

### Kolejność rozgłaszania w drzewie
Struktura komponentów jest reprezentowana jako drzewo. Możesz wybrać kierunek rozgłaszania eventu.
Więcej informacji o algorytmach znajdziesz w sekcji [kolejność zdarzeń w drzewie](/pl/learn/nested-trees.md#generalizacji-specyfikacja).

#### W kierunku specjalizacji
```java
container().callSpecialization(event);
```
#### W kierunku generalizacji
```java
container().callGeneralization(event);
```

::: warning Musisz wywołać metodę `callSpecialization(event)` lub `callGeneralization(event)` na obiekcie `Container`, aby zdarzenie zostało rozgłoszone w całym drzewie.
:::

### Rozgłaszanie we fragmencie drzewa
Event zostanie rozgłoszony tylko na wskazanym komponencie oraz wszystkich jego dzieciach i poddrzewach rekurencyjnie.
```java
component.callGeneralization(event);
// or
component.callGeneralization(event);
```

### Rozgłaszanie dla konkretnego komponentu
Event zostanie obsłużony tylko przez komponent, z którego wywołano metodę.
```java
component.callSelf(event)
```

## Nasłuchiwanie eventu
```java
// Method inside KickEliminatedLogic (Controller)
@TreeEventHandler
public void onTeamEliminated(TeamEliminatedEvent event) {
    var eliminatedPlayers = event.getTeam().players();
    for (var player : eliminatedPlayers) {
        container().removePlayer(player);    
    }
}
```
```java
// Method inside EliminatedMessages (Controller)
@TreeEventHandler
public void onTeamEliminated(TeamEliminatedEvent event) {
    var teamName = event.getTeam().getName();
    var message = "Team " + teamName + " has been eliminated!";

    var containerPlayers = players();
    for (var player : containerPlayers) {
        var teamName = event.getTeam().getName();
        player.sendMessage(message);    
    }
}
```

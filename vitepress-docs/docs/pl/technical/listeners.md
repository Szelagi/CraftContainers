# Listenery Bukkit
Framework umożliwia przypisanie listenerów Bukkit do komponentów.

## Jak to działa?

- Listenery są aktywowane tylko wtedy, gdy są potrzebne; w przeciwnym razie są wyłączane.
- Każdy komponent może zdefiniować swoje listenery.
- Jedna instancja listenera obsługuje wiele instancji tego samego komponentu, eliminując zbędne duplikaty.

## Przykład zastosowania
Ten przykład pokazuje, że logika w sesji jest oddzielona od reszty gry. Komponent dodany do sesji wpływa tylko na jej uczestników.

![przykładowy listener](../../img/example-listener.gif)

```java
// MyComponent.java
@Override
public Listeners defineListeners() {
    // Dodajemy niestandardowy nasłuchiwacz do systemu zdarzeń
    return super.defineListeners().add(MyListener.class);
}

private static class MyListener implements Listener {
    /**
     * Obsługa skoku gracza – wysyła wiadomość, gdy gracz skoczy.
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJump(PlayerJumpEvent event) {
        var player = event.getPlayer();
        var session = SessionManager.session(player);

        ListenerManager.each(session, getClass(), MyComponent.class, myComponent -> {
            player.sendMessage("§eYou jumped! §7(Event captured by MyComponent)");
        });
    }

    /**
     * Obsługa wyrzucania przedmiotów – blokuje wyrzucanie przedmiotów przez gracza.
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        var player = event.getPlayer();
        var session = SessionManager.session(player);

        ListenerManager.first(session, getClass(), MyComponent.class, myComponent -> {
            event.setCancelled(true);
            player.sendMessage("§eYou cannot drop items! §7(Event captured by MyComponent)");
        });
    }
}
```

## Rejestrowanie listenera
Każdy komponent może zdefiniować listenery, których będzie używać.

```java
@Override
public Listeners defineListeners() {
    // Dodajemy MyListener do listy nasłuchiwaczy
    return super.defineListeners().add(MyListener.class);
}
```

::: tip Porada
Nie rejestruj klasy MyListener w Bukkit. Framework zarządza tym automatycznie – aktywuje listener,
gdy jest potrzebny, i usuwa go, gdy przestaje być używany.
:::

## Filtrowanie zdarzeń
Zdarzenia Bukkit domyślnie dotyczą całego serwera, dlatego należy je filtrować.
Framework zapewnia efektywne metody do filtrowania zdarzeń.

### Metody filtrowania


#### Wykonaj dla każdego komponentu konkretnego typu
Wykonuje consumer dla wszystkich komponentów określonego typu posiadających listener.
```java
ListenerManager.each(session, MyListener.class, MyComponent.class, myComponent -> {});
```


#### Wykonaj dla pierwszego komponentu konkretnego typu
Działa podobnie jak each, ale wykonuje consumer tylko dla pierwszego pasującego komponentu.
```java
ListenerManager.first(session, MyListener.class, MyComponent.class, myComponent -> {});
```


#### Wykonaj dla każdego komponentu używającego listenera
Wykonuje **consumer** dla wszystkich komponentów posiadających listener.
```java
ListenerManager.each(session, MyListener.class, component -> {});
```


#### Wykonaj dla pierwszego komponentu używającego listenera
```java
ListenerManager.first(session, MyListener.class, component -> {});
```

### Metody wyszukiwania

#### Znajdź wszystkie komponenty konkretnego typu
```java
var myComponents = ListenerManager.components(session, MyListener.class, MyComponent.class);
```

#### Znajdź pierwszy komponent konkretnego typu
```java
var myComponent = ListenerManager.first(session, MyListener.class, MyComponent.class);
```

#### Znajdź wszystkie komponenty używające listenera
```java
var components = ListenerManager.components(session, MyListener.class);
```

#### Znajdź pierwszy komponent używający listenera
```java
var component = ListenerManager.first(session, MyListener.class);
```

::: warning Uwaga
Powyższych metod nie należy używać do ogólnego wyszukiwania komponentów w drzewie sesji – służą wyłącznie do filtrowania zdarzeń w listenerach.
:::
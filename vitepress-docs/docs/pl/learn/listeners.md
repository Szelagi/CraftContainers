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

private static class MyListener implements AdaptedListener {
    /**
     * Obsługa skoku gracza – wysyła wiadomość, gdy gracz skoczy.
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJump(PlayerJumpEvent event) {
        var player = event.getPlayer();
        var container = Container.getForPlayer(player);

        each(container, MyComponent.class, myComponent -> {
            player.sendMessage("§eYou jumped! §7(Event captured by MyComponent)");
        });
    }

    /**
     * Obsługa wyrzucania przedmiotów – blokuje wyrzucanie przedmiotów przez gracza.
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        var player = event.getPlayer();
        var container = Container.getForPlayer(player);

        first(container, MyComponent.class, myComponent -> {
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
each(container, MyComponent.class, myComponent -> {})
        
// statyczne
AdaptedListener.eachFor(container, MyListener.class, MyComponent.class, myComponent -> {});
```


#### Wykonaj dla pierwszego komponentu konkretnego typu
Działa podobnie jak each, ale wykonuje consumer tylko dla pierwszego pasującego komponentu.
```java
first(container, MyComponent.class, myComponent -> {});

// statyczne
AdaptedListener.firstFor(container, MyListener.class, MyComponent.class, myComponent -> {});
```


#### Wykonaj dla każdego komponentu używającego listenera
Wykonuje **consumer** dla wszystkich komponentów posiadających listener.
```java
each(container, component -> {});

// statyczne
AdaptedListener.eachFor(container, MyListener.class, component -> {});
```


#### Wykonaj dla pierwszego komponentu używającego listenera
```java
first(container, component -> {});

// statyczne
AdaptedListener.firstFor(container, MyListener.class, component -> {});
```

### Metody wyszukiwania

#### Znajdź wszystkie komponenty konkretnego typu
```java
var myComponents = each(container, MyComponent.class);

// statyczne
var myComponents = AdaptedListener.eachFor(container, MyListener.class, MyComponent.class);
```

#### Znajdź pierwszy komponent konkretnego typu
```java
var myComponent = first(container, MyComponent.class);

// statyczne
var myComponent = AdaptedListener.firstFor(container, MyListener.class, MyComponent.class);
```

#### Znajdź wszystkie komponenty używające listenera
```java
var components = each(container);

// statyczne
var components = AdaptedListener.eachFor(container, MyListener.class);
```

#### Znajdź pierwszy komponent używający listenera
```java
var component = first(container);

// statyczne
var component = AdaptedListener.firstFor(container, MyListener.class);
```

::: warning Uwaga
Nie używaj tych metod do ogólnego przeszukiwania komponentów w drzewie kontenera.  
Służą one wyłącznie do **filtrowania zdarzeń w listenerach**.

Jeśli chcesz przeszukać całe drzewo kontenera, zobacz sekcję [Przeszukiwanie drzewa](/pl/learn/search.md).
:::

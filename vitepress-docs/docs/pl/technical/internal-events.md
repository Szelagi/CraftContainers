# Zdarzenie wewnętrzne
Każdy komponent posiada wbudowane zdarzenia wewnętrzne, które odpowiadają za różne etapy
jego cyklu życia oraz interakcję z graczami.


Rozróżniamy kolejność wykonywania zdarzeń [**lokalną**](/pl/technical/sequence-event-execution.md) *(wewnątrz komponentu)* oraz [**globalną**](/pl/technical/nested-trees.md) *(w całej strukturze drzewa)*.




## Uruchomienie komponentu
Wywoływane automatycznie podczas uruchamiania komponentu. Służy do jego inicjalizacji (np. tworzenie zasobów, uruchamianie wątków, inicjalizacja stanu).

```java
// MyComponent.java
@Override
public void onComponentInit(ComponentConstructor event) {
    super.onComponentInit(event);
    // Komponent został uruchomiony
}
```


## Zakończenie komponentu
Wywoływane automatycznie podczas wyłączania komponentu. Służy do zwalniania zasobów i usuwania elementów utworzonych przez komponent.

::: tip Automatycznie zwalniane zasoby:
- zadania i wątki
- podrzędne komponenty
- listenery Bukkit
:::

```java
// MyComponent.java
@Override
public void onComponentDestroy(ComponentDestructor event) {
    super.onComponentInit(event);
    // Komponent został wyłączony
}
```


## Inicjacja gracza
Służy do przypisywania graczowi właściwości lub stanu, wymaganych przez komponent.

::: tip Uruchamiane, gdy:
- gracz dołącza do sesji
- komponent jest uruchamiany, a gracze byli już w sesji
:::

```java
// MyComponent.java
@Override
public void onPlayerInit(PlayerConstructor event) {
    super.onComponentInit(event);
    // Inicjalizacja gracza
}
```

## Deinicjacja gracza
Służy do usuwania właściwości lub stanu gracza przypisanego przez komponent.

::: tip Uruchamiane, gdy:
- gracz opuszcza sesję
- komponent jest wyłączany – usuwa stan przypisany do graczy
- sesja jest zamykana
:::

```java
// MyComponent.java
@Override
public void onPlayerDestroy(PlayerDestructor event) {
  super.onComponentInit(event);
    // Deinicjalizacja gracza
}
```

## Prośba o dołączenie gracza
Pozwala określić, czy gracz może dołączyć do sesji. Wywoływane przy próbie dołączenia.

```java
// MyComponent.java
@Override
public void onPlayerJoinRequest(PlayerJoinRequest event) {
  super.onComponentInit(event);
  // Decyzja, czy gracz może dołączyć
  var newPlayerCount = players().size() + 1;
  if (newPlayerCount > MY_PLAYER_LIMIT) {
      var reason = new Reason(this, "Nie możesz dołączyć, ponieważ sesja jest już pełna!");
      event.setCanceled(reason);
  }
}
```

## Odzyskiwanie komponentu (Recovery)
Wbudowany mechanizm odzyskiwania pozwala zachować spójność stanu nawet w przypadku awarii serwera.
Można przypisać zadania, które wykonają się automatycznie po restarcie, jeśli komponent nie został poprawnie wyłączony.

```java
// MyComponent.java
@Override
public void onComponentRecovery(ComponentRecovery event) {
    event.register(this, () -> {
        // Wykonywane tylko w przypadku awarii
    });
}
```

::: warning
Mechanizm działa na zasadzie serializacji Javy. Przypisuj tylko obiekty, które można serializować.
:::


## Odzyskiwanie stanu gracza (Recovery)
System odzyskiwania graczy pozwala zachować ich stan po awarii serwera.
Można przypisać zadania, które zostaną wykonane automatycznie, gdy gracz ponownie dołączy do serwera, jeśli komponent nie usunął poprawnie jego stanu.

```java
// MyComponent.java - przykład awaryjnego usuwania efektów gracza
@Override
public void onPlayerRecovery(PlayerRecovery event) {
    var owner = event.owner();
    // Konwersja nieserializowalnych obiektów do serializowanych
    var potionTypes = owner.getActivePotionEffects()
            .stream()
            .map(PotionEffect::getType)
            .collect(Collectors.toSet());

    // Argument 'player' nie jest serializowalny, ale zostanie dostarczony przez framework
    // Używamy owner(), aby pobrać dane gracza – 'player' to ten sam gracz w momencie odzyskiwania
    event.register(this, (player) -> {
        for (var type : potionTypes) {
            player.removePotionEffect(type);
        }
    });
    
}
```

::: warning
Mechanizm działa na zasadzie serializacji Javy. Przypisuj tylko obiekty, które można serializować.
:::




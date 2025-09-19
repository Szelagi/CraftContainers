# Adnotacje

## Komponenty

### @SingletoneComponent
Adnotacja zapewnia, że komponent może istnieć maksymalnie jeden dla danego kontenera. Próba uruchomienia więcej niż jednej instancji tego samego komponentu w tym samym kontenerze skutkuje wyjątkiem.


Stosuje się ją do oznaczania komponentów, które nie powinny być uruchamiane wielokrotnie w ramach jednej instancji kontenera, aby uniknąć błędów.


#### Przykład
Kontroler `NoPlaceAndBreak` blokuje wszystkie zdarzenia związane ze stawianiem i kładzeniem bloków. Uruchomienie wielu instancji tego samego kontrolera nie jest wskazane, dlatego oznacza się go adnotacją `@SingletonComponent`.

```java
@SingletonComponent
public class MyLogic extends Controller {...}
```

### @Dependency(`component`)
Adnotacja zapewnia, że wskazany jako argument komponent będzie aktywny przez cały cykl życia oznaczonego komponentu.
Jeżeli zależność nie będzie dostępna w jakim kolwiek momencie, zostanie zgłoszony wyjątek.


Stosuje się ją, gdy komponent musi zawsze współpracować z innym komponentem – brak dostępnej zależności powoduje natychmiastowy błąd.


::: warning
Nie tworzyć zależności cyklicznych ani nie wymagać od rodzica jego własnego dziecka jako zależności.
:::

```java
@Dependency(component = MyComponent.class)
public class MyLogic extends Controller {...}
```

### Pobieranie komponentu zależnego
Metody te różnią się od [wyszukiwania komponentów](/pl/learn/search.md), ponieważ zawsze zwracają komponenty i nie zwracają `null`.

- **useComponent(`componentClass`)** - Zwraca dokładnie jeden komponent.


  1. `componentClass` musi być klasą zadeklarowaną jako zależność w komponencie wywołującym metodę
    ```java
     @Dependency(component = componentClass)
     ```
  2. Klasa docelowa *(ta, która jest pobierana)* musi być oznaczona jako `@SingletonComponent`.


- **useComponents(`componentClass`)** - Zwraca listę komponentów, która zawsze zawiera przynajmniej jeden element.

  1. `componentClass` musi być klasą zadeklarowaną jako zależność w komponencie wywołującym metodę
  ```java
   @Dependency(component = componentClass)
   ```

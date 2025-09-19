# Przeszukiwanie drzewa
Kontener składa się z hierarchii komponentów ułożonych w strukturę drzewa.
Drzewo jest dynamiczne – komponenty mogą być dodawane lub usuwane w trakcie działania kontenera.
Z tego powodu w celu uzyskania dostępu do komponentów należy przeszukiwać drzewo.

## Komponenty zgodne z typem

### Pierwszy komponent (indeksowany)
Zwraca pierwszy element będący instancjami podanego typu lub jego podtypów.
Typ sprawdzany jest na podstawie operatora `instanceof` i nie jest skorelowany z kolejnością komponentów.
Kiedy nie znajdzie, żadnego danego typu zwraca null.
```java
var myComponent = container().getComponent(MyComponent.class);
// or
var instances = container().getComponent(MyInterface.class);
```

### Wszystkie komponenty (indeksowany)
Zwraca zbiór elementów będących instancjami podanego typu lub jego podtypów.
Zbiór nie uwzględnia kolejności elementów.
Typ sprawdzany jest na podstawie operatora `instanceof`.
Kiedy nie znajdzie zadnego danego typu zwraca pusty zbiór.
```java
var myComponents = container().getComponents(MyComponent.class);
// or
var instances = container().getComponents(MyInterface.class);
```

::: tip
Jeśli potrzebujesz zawsze jednego komponentu albo niepustego zbioru,
zobacz adnotację [@Dependency i wyszukiwanie komponentu zależnego](/pl/learn/annotations.html#dependency-component).
:::

### Pierwszy komponent w określonej kolejności
Przeszukuje drzewo w kolejności `specjalizacji` lub `generalizacji` i zwraca pierwszy komponent zgodny z typem. 
Traktuje komponent na którym zostałą wykonana metoda jako korzeń *(nie chodzi do jego rodziców)*.
Kiedy nie znajdzie, żadnego danego typu zwraca `null`.

```java
// Przeszukuje drzewo w kolejności Generalizacja -> Specjalizacja
var myComponent = component.findComponentSpecOrd(MyComponent.class);
var instance = component.findComponentSpecOrd(MyInterface.class);

// Przeszukuje drzewo w kolejności Specjalizacaj -> Generalizacja
var myComponent = component.findComponentGeneOrd(MyComponent.class);
var instance = component.findComponentGeneOrd(MyInterface.class);
```

### Wszystkie komponenty w określonej kolejności
Przeszukuje drzewo i zwraca listę komponentów zgodnych z typem w kolejności `specjalizacji` lub `generalizacji`.
Traktuje komponent na którym zostałą wykonana metoda jako korzeń *(nie chodzi do jego rodziców)*.
Kiedy nie znajdzie, żadnego danego typu zwraca null.

```java
// Przeszukuje drzewo w kolejności Generalizacja -> Specjalizacja
var myComponents = component.findComponentsSpecOrd(MyComponent.class);
var instances = component.findComponentsSpecOrd(MyInterface.class);

// Przeszukuje drzewo w kolejności Specjalizacaj -> Generalizacja
var myComponents = component.findComponentGeneOrd(MyComponent.class);
var instances = component.findComponentsGeneOrd(MyInterface.class);
```
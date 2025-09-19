# Obsługa wielu wersji gry
Podczas projektowania komponentów należy uwzględnić różnice pomiędzy wersjami gry.

## Obsługa wielu wersji
Jeśli chcemy, aby komponent był uniwersalny, możemy zaimplementować różne rozwiązania w zależności od wersji Minecrafta.

```java
public class Hologram extends Controller {
    private final net.kyori.adventure.text.Component text;
    private final Location location;

    public Hologram(Component component, Location location, net.kyori.adventure.text.Component text) {
        super(component);
        this.text = text;
        this.location = location;
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        // If version >= 1.19.4
        if (MinecraftVersion.isGreaterOrEqual(1, 19, 4)) {
            // Use TextDisplay entity
            new TextDisplayHologram(this, location, text).start();
        } else {
            // Use ArmorStand entity
            new ArmorStandHologram(this, location, text).start();
        }
    }
}
```

## Wykluczanie wersji
Jeśli komponent działa wyłącznie w określonej wersji gry, należy to jasno zakomunikować poprzez rzucenie wyjątku.

```java
public class TextDisplayHologram extends Controller {
    private final net.kyori.adventure.text.Component text;
    private final Location location;

    public TextDisplayHologram(Component component, Location location, net.kyori.adventure.text.Component text) {
        super(component);

        // If version < 1.19.4 → throw exception
        if (!MinecraftVersion.isGreaterOrEqual(1, 19, 4)) {
            throw new UnsupportedMinecraftVersion(
                    "TextDisplayHologram requires Minecraft version 1.19.4 or newer."
            );
        }

        this.text = text;
        this.location = location;
    }
}
```
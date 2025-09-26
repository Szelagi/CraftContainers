# Obsługa wielu wersji gry
Podczas projektowania komponentów należy uwzględnić różnice pomiędzy wersjami gry.

## Obsługa wielu wersji
Jeśli chcemy, aby komponent był uniwersalny, możemy zaimplementować różne rozwiązania w zależności od wersji Minecrafta.

```java
public class Hologram extends Controller {
    private final Component hologram;
    public Hologram(Component component, Location location, net.kyori.adventure.text.Component text) {
        super(component);

        // If version > 1.19.4
        if (MinecraftVersion.isGreaterOrEqual(1, 19, 4)) {
            // Use TextDisplay Entity
            hologram = new HologramTD(this, location, text);
        } else {
            // Use ArmorSand Entity
            hologram = new HologramAS(this, location, text);
        }
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        hologram.start();
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
    
    // ...
}
```
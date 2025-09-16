package pl.szelagi.buildin.controller.hologram;

import org.bukkit.Location;
import pl.szelagi.component.base.Component;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.component.Controller;
import pl.szelagi.minecraftVersion.MinecraftVersion;

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
        // If version > 1.19.4
        if (MinecraftVersion.isGreaterOrEqual(1, 19, 4)) {
            // Use TextDisplay Entity
            new HologramTD(this, location, text).start();
        } else {
            // Use ArmorSand Entity
            new HologramAS(this, location, text).start();
        }
    }
}

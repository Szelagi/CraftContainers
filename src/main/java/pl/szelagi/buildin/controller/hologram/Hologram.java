package pl.szelagi.buildin.controller.hologram;

import org.bukkit.Location;
import pl.szelagi.component.base.Component;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.component.Controller;
import pl.szelagi.minecraftVersion.MinecraftVersion;

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

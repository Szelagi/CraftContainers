package pl.szelagi.buildin.controller.hologram;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.component.Controller;
import pl.szelagi.manager.VersionManager;

public class Hologram extends Controller {
    private final Component text;
    private final Location location;
    public Hologram(BaseComponent baseComponent, Location location, Component text) {
        super(baseComponent);
        this.text = text;
        this.location = location;
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        // If version > 1.19.4
        if (VersionManager.isGreaterOrEqual(1, 19, 4)) {
            // Use TextDisplay Entity
            new HologramTD(this, location, text).start();
        } else {
            // Use ArmorSand Entity
            new HologramAS(this, location, text).start();
        }
    }

}

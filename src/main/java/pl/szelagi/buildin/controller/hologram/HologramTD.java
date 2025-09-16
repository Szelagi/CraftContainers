package pl.szelagi.buildin.controller.hologram;

import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import pl.szelagi.component.base.Component;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.event.internal.component.ComponentDestructor;
import pl.szelagi.component.Controller;

public class HologramTD extends Controller {
    private final net.kyori.adventure.text.Component text;
    private final Location location;
    private TextDisplay textDisplay;
    public HologramTD(Component component, Location location, net.kyori.adventure.text.Component text) {
        super(component);
        this.text = text;
        this.location = location;
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        var world = location.getWorld();
        textDisplay = world.spawn(location, TextDisplay.class, td -> {
            td.setBillboard(Display.Billboard.CENTER);
            td.setSeeThrough(true);
            td.text(text);
        });
    }

    @Override
    public void onComponentDestroy(ComponentDestructor event) {
        super.onComponentDestroy(event);
        if (textDisplay != null && !textDisplay.isDead()) {
            textDisplay.remove();
        }
    }

}

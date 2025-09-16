package pl.szelagi.buildin.controller.hologram;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.Vector;
import pl.szelagi.component.base.Component;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.event.internal.component.ComponentDestructor;
import pl.szelagi.component.Controller;

public class HologramAS extends Controller {
    private final net.kyori.adventure.text.Component text;
    private final Location location;
    private ArmorStand armorStand;
    public HologramAS(Component component, Location location, net.kyori.adventure.text.Component text) {
        super(component);
        this.text = text;
        this.location = location.clone().add(new Vector(0, -0.2, 0));
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        var world = location.getWorld();
        armorStand = world.spawn(location, ArmorStand.class, as -> {
            as.setInvisible(true);
            as.setGravity(false);
            as.setInvulnerable(true);
            as.setMarker(true);
            as.setCustomNameVisible(true);
            as.customName(text);
        });
    }

    @Override
    public void onComponentDestroy(ComponentDestructor event) {
        super.onComponentDestroy(event);
        if (armorStand != null && !armorStand.isDead()) {
            armorStand.remove();
        }
    }

}

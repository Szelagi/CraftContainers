package pl.szelagi.buildin.controller.hologram;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.Vector;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.event.internal.component.ComponentDestructor;
import pl.szelagi.component.Controller;

public class HologramAS extends Controller {
    private final Component text;
    private final Location location;
    private ArmorStand armorStand;
    public HologramAS(BaseComponent baseComponent, Location location, Component text) {
        super(baseComponent);
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

package pl.szelagi.buildin.controller.rotBlockHologram;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.event.internal.component.ComponentDestructor;
import pl.szelagi.component.Controller;
import pl.szelagi.util.timespigot.Time;

public class RotHologramBlockAS extends Controller {
    private float yaw;
    private final Location location;
    private final Material material;
    private ArmorStand armorStand;
    public RotHologramBlockAS(BaseComponent baseComponent, Location location, Material material) {
        super(baseComponent);
        this.location = location.clone().add(new Vector(0, -1.7, 0));
        this.material = material;
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        yaw = 0;
        var world = location.getWorld();
        armorStand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);
        armorStand.setCustomNameVisible(false);
        armorStand.setCustomName("FloatingBlockAS");
        armorStand.getEquipment().setHelmet(new ItemStack(material));

        runTaskTimer(() -> {
            if (armorStand == null || armorStand.isDead()) return;
            yaw += 5;
            if (yaw >= 360) yaw = 0;
            armorStand.setRotation(yaw, 0);
        }, Time.ticks(1), Time.ticks(1));
    }

    @Override
    public void onComponentDestroy(ComponentDestructor event) {
        super.onComponentDestroy(event);
        if (armorStand != null && !armorStand.isDead()) {
            armorStand.remove();
        }
    }

}


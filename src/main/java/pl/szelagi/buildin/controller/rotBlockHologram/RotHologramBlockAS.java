package pl.szelagi.buildin.controller.rotBlockHologram;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import pl.szelagi.component.base.Component;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.event.internal.component.ComponentDestructor;
import pl.szelagi.component.Controller;
import pl.szelagi.util.timespigot.Time;

public class RotHologramBlockAS extends Controller {
    private float yaw;
    private final Location location;
    private final ItemStack displayItemStack;
    private ArmorStand armorStand;

    public RotHologramBlockAS(Component parent, Location location, ItemStack displayItemStack) {
        super(parent);
        this.location = location;
        this.displayItemStack = displayItemStack.clone();
    }

    public RotHologramBlockAS(Component parent, Location location, Material displayMaterial) {
        this(parent, location, new ItemStack(displayMaterial));
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
        armorStand.getEquipment().setHelmet(displayItemStack.clone());

        runTaskTimer(() -> {
            if (armorStand == null) return;
            yaw += 5;
            if (yaw >= 360) yaw = 0;
            armorStand.setRotation(yaw, 0);
        }, Time.ticks(1), Time.ticks(1));
    }

    @Override
    public void onComponentDestroy(ComponentDestructor event) {
        super.onComponentDestroy(event);
        if (armorStand != null) {
            armorStand.remove();
            armorStand = null;
        }
    }

}


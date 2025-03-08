package pl.szelagi.buildin.controller.rotBlockHologram;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.component.baseComponent.internalEvent.component.ComponentConstructor;
import pl.szelagi.component.baseComponent.internalEvent.component.ComponentDestructor;
import pl.szelagi.component.controller.Controller;
import pl.szelagi.util.timespigot.Time;

public class RotHologramBlockID extends Controller {
    private float multiplier = 0;
    private final Location location;
    private final Material material;
    private ItemDisplay itemDisplay;
    public RotHologramBlockID(BaseComponent baseComponent, Location location, Material material) {
        super(baseComponent);
        this.location = location;
        this.material = material;
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        var world = location.getWorld();
        itemDisplay = (ItemDisplay) world.spawnEntity(location, EntityType.ITEM_DISPLAY);
        itemDisplay.setCustomNameVisible(false);
        itemDisplay.setCustomName("FloatingBlockBD");
        itemDisplay.setItemStack(new ItemStack(material));

        runTaskTimer(() -> {
            if (itemDisplay == null || itemDisplay.isDead()) return;

            Vector3f translation = new Vector3f(0, 0, 0);
            AxisAngle4f axisAngleRotMat = new AxisAngle4f((float) -(Math.PI/2) * multiplier++ , new Vector3f(0, 1, 0));
            Transformation transformation = new Transformation(
                    translation,
                    axisAngleRotMat,
                    new Vector3f(0.6f,0.6f,0.6f),
                    axisAngleRotMat
            );
            itemDisplay.setInterpolationDelay(0);
            itemDisplay.setInterpolationDuration(32);
            itemDisplay.setTransformation(transformation);
        }, Time.ticks(0), Time.ticks(32));
    }

    @Override
    public void onComponentDestroy(ComponentDestructor event) {
        super.onComponentDestroy(event);
        if (itemDisplay != null && !itemDisplay.isDead()) {
            itemDisplay.remove();
        }
    }
}


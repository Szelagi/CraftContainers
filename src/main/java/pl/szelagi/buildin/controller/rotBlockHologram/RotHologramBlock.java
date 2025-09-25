package pl.szelagi.buildin.controller.rotBlockHologram;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pl.szelagi.component.base.Component;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.component.Controller;
import pl.szelagi.minecraftVersion.MinecraftVersion;

public class RotHologramBlock extends Controller {
    private final Component rotHologramBlock;

    public RotHologramBlock(Component component, Location location, ItemStack displayItemStack) {
        super(component);

        if (MinecraftVersion.isGreaterOrEqual(1, 19, 4)) {
            rotHologramBlock = new RotHologramBlockID(this, location, displayItemStack);
        } else {
            rotHologramBlock = new RotHologramBlockAS(this, location, displayItemStack);
        }
    }

    public RotHologramBlock(Component parent, Location location, Material material) {
        this(parent,location, new ItemStack(material));
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        rotHologramBlock.start();
    }

}


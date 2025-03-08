package pl.szelagi.buildin;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import pl.szelagi.buildin.controller.hologram.Hologram;
import pl.szelagi.buildin.controller.rotBlockHologram.RotHologramBlock;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.component.baseComponent.internalEvent.component.ComponentConstructor;
import pl.szelagi.component.controller.Controller;
import pl.szelagi.util.timespigot.Time;

public class ItemGenerator extends Controller {
    private final Component text;
    private final Material visualMaterial;
    private final Material itemMaterial;
    private final Location location;
    private final Time spawnDelay;

    public ItemGenerator(BaseComponent baseComponent, Component text, Location location, Material visualMaterial, Material itemMaterial, Time spawnDelay) {
        super(baseComponent);
        this.text = text;
        this.visualMaterial = visualMaterial;
        this.itemMaterial = itemMaterial;
        this.location = location;
        this.spawnDelay = spawnDelay;
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        var fallingLocation = location.clone().add(0, 2.9, 0);
        var hologramLocation = location.clone().add(0, 2, 0);
        new RotHologramBlock(this, fallingLocation, visualMaterial).start();
        new Hologram(this, hologramLocation, text).start();
        runTaskTimer(this::generate, spawnDelay, spawnDelay);
    }

    private void generate() {
        final var world = location.getWorld();
        var itemStack = new ItemStack(itemMaterial);
        world.dropItem(location, itemStack, i -> {
            i.setVelocity(new Vector(0, 0, 0));
            i.setPickupDelay(0);
            i.setUnlimitedLifetime(false);
        });
    }
}

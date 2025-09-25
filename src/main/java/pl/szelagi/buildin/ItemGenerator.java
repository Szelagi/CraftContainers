package pl.szelagi.buildin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import pl.szelagi.buildin.controller.hologram.Hologram;
import pl.szelagi.buildin.controller.rotBlockHologram.RotHologramBlock;
import pl.szelagi.component.base.Component;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.component.Controller;
import pl.szelagi.util.timespigot.Time;

public class ItemGenerator extends Controller {
    private final ItemStack spawnItem;
    private final Location location;
    private final Time spawnDelay;
    private final RotHologramBlock rotHologramBlock;
    private final Hologram hologram;

    public ItemGenerator(Component parent, net.kyori.adventure.text.Component label, Location location, ItemStack displayItem, ItemStack spawnItem, Time spawnDelay) {
        super(parent);
        this.spawnItem = spawnItem;
        this.location = location;
        this.spawnDelay = spawnDelay;

        var fallingLocation = location.clone().add(0, 2.9, 0);
        var hologramLocation = location.clone().add(0, 2, 0);

        // Możesz utworzyć obiekty komponentów podrzędnych w konstruktorze, ale nie możesz ich uruchomić z poziomu konstruktora
        // poniważ w konstruktorze rodzic (this) nie jest włączony więc sam nie może uruchamiać komponentów podrzędnych
        this.rotHologramBlock = new RotHologramBlock(this, fallingLocation, displayItem);
        this.hologram = new Hologram(this, hologramLocation, label);
    }
    public ItemGenerator(Component parent, net.kyori.adventure.text.Component label, Location location, Material displayMaterial, ItemStack spawnItem, Time spawnDelay) {
        this(parent, label, location, new ItemStack(displayMaterial), spawnItem, spawnDelay);
    }

    public ItemGenerator(Component parent, net.kyori.adventure.text.Component label, Location location, ItemStack displayItemStack, Material spawnMaterial, Time spawnDelay) {
        this(parent, label, location, displayItemStack, new ItemStack(spawnMaterial), spawnDelay);
    }

    public ItemGenerator(Component parent, net.kyori.adventure.text.Component label, Location location, Material displayMaterial, Material spawnMaterial, Time spawnDelay) {
        this(parent, label, location, new ItemStack(displayMaterial), new ItemStack(spawnMaterial), spawnDelay);
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        // rodzic (this) uruchomił się, więc uruchamiamy komponenty podrzędne
        // craftcontainers śledzi uruchomione komponenty więc zostaną wyłączone automatycznie wraz z rodzicem
        rotHologramBlock.start();
        hologram.start();

        // uruchamiamy zadanie, które spawnuje item
        runTaskTimer(this::spawnItem, spawnDelay, spawnDelay);
    }

    // prosty kod na generowanie itemów ma mapie
    private void spawnItem() {
        final var world = location.getWorld();
        var itemStack = new ItemStack(spawnItem);
        world.dropItem(location, itemStack, i -> {
            i.setVelocity(new Vector(0, 0, 0));
            i.setPickupDelay(0);
            i.setUnlimitedLifetime(false);
        });
    }
}

package pl.szelagi.buildin.controller.rotBlockHologram;

import org.bukkit.Location;
import org.bukkit.Material;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.component.Controller;
import pl.szelagi.manager.VersionManager;

public class RotHologramBlock extends Controller {
    private final Location location;
    private final Material material;
    public RotHologramBlock(BaseComponent baseComponent, Location location, Material material) {
        super(baseComponent);
        this.location = location;
        this.material = material;
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        if (VersionManager.isGreaterOrEqual(1, 19, 4)) {
            new RotHologramBlockID(this, location, material).start();
        } else {
            new RotHologramBlockAS(this, location, material).start();
        }
    }

}


/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.buildin.controller.hologram.Hologram;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.component.baseComponent.internalEvent.component.ComponentConstructor;
import pl.szelagi.component.controller.Controller;

public class MarkerHologram extends Controller {
    private final Component name;
    private final Location location;

    public MarkerHologram(@NotNull BaseComponent parent, @NotNull String name, @NotNull Location location) {
        super(parent);
        this.name = Component.text("§2" + name);
        this.location = location;
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        var l3 = location.clone().add(0, 0.3, 0);
        var l2 = location.clone().add(0, 0.6, 0);
        var l1 = location.clone().add(0, 0.9, 0);
        new Hologram(this, l1, name).start();
        new Hologram(this, l2, locationToXYZ(location)).start();
        new Hologram(this, l3, locationToYawPitch(location)).start();
        new Hologram(this, location, Component.text("§a•")).start();
    }

    private Component locationToXYZ(Location location) {
        var template = "§7[%.1f %.1f %.1f]";
        var formatted = String.format(template, location.getX(), location.getY(), location.getZ());
        return Component.text(formatted);
    }

    private Component locationToYawPitch(Location location) {
        var template = "§7[%.1f %.1f]";
        var formatted = String.format(template, location.getYaw(), location.getPitch());
        return Component.text(formatted);
    }
}

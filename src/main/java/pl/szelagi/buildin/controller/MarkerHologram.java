/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.buildin.controller.hologram.Hologram;
import pl.szelagi.component.base.Component;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.component.Controller;
import pl.szelagi.marker.Marker;

public class MarkerHologram extends Controller {
    private final net.kyori.adventure.text.Component label;
    private final net.kyori.adventure.text.Component metadata;
    private final Location location;

    public MarkerHologram(@NotNull Component parent, @NotNull Marker marker) {
        super(parent);
        this.label = net.kyori.adventure.text.Component.text(marker.getName() + " §8ID" + marker.getId());
        this.metadata = net.kyori.adventure.text.Component.text("§7" + marker.getMetadata().size() + " metadata");
        this.location = marker.getLocation();
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        var l3 = location.clone().add(0, 0.3, 0);
        var l2 = location.clone().add(0, 0.6, 0);
        var l1 = location.clone().add(0, 0.9, 0);
        new Hologram(this, l1, label).start();
        new Hologram(this, l2, metadata).start();
        new Hologram(this, l3, locationToYawPitch(location)).start();
        new Hologram(this, location, net.kyori.adventure.text.Component.text("§a•")).start();
    }

    private net.kyori.adventure.text.Component locationToXYZ(Location location) {
        var template = "§7[%.1f %.1f %.1f]";
        var formatted = String.format(template, location.getX(), location.getY(), location.getZ());
        return net.kyori.adventure.text.Component.text(formatted);
    }

    private net.kyori.adventure.text.Component locationToYawPitch(Location location) {
        var template = "§7[%.1f %.1f]";
        var formatted = String.format(template, location.getYaw(), location.getPitch());
        return net.kyori.adventure.text.Component.text(formatted);
    }
}

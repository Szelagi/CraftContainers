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
import pl.szelagi.component.Controller;
import pl.szelagi.component.base.Component;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.marker.Marker;

public class CenterHologram extends Controller {
    private final Location location;
    private final net.kyori.adventure.text.Component label;

    public CenterHologram(@NotNull Component parent, @NotNull Location location) {
        super(parent);
        this.location = location;
        this.label = net.kyori.adventure.text.Component.text("§2center");
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        var l1 = location.clone().add(0, 0.3, 0);
        new Hologram(this, l1, label).start();
        new Hologram(this, location, net.kyori.adventure.text.Component.text("§a•")).start();
    }
}

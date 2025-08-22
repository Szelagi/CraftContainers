/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.component.baseComponent.internalEvent.component.ComponentConstructor;
import pl.szelagi.component.baseComponent.internalEvent.player.PlayerConstructor;
import pl.szelagi.component.baseComponent.internalEvent.player.PlayerDestructor;
import pl.szelagi.component.controller.Controller;

public class FakeWorldBorder extends Controller {
    private final Location center;
    private WorldBorder border;
    private final double initialRadius;

    public FakeWorldBorder(@NotNull BaseComponent parent, Location center, double initialRadius) {
        super(parent);
        this.center = center;
        this.initialRadius = initialRadius;
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        border = Bukkit.createWorldBorder();
        border.setCenter(center);
        setRadius(initialRadius);
        border.setWarningDistance(0);
        border.setDamageAmount(0.0);
        border.setDamageBuffer(100);
    }

    @Override
    public void onPlayerInit(PlayerConstructor event) {
        super.onPlayerInit(event);
        event.player().setWorldBorder(border);
    }

    @Override
    public void onPlayerDestroy(PlayerDestructor event) {
        super.onPlayerDestroy(event);
        var player = event.player();
        var border = player.getWorld().getWorldBorder();
        event.player().setWorldBorder(border);
    }

    public void setRadius(double radius) {
        var size = 2 * radius - 1;
        border.setSize(size);
    }
}

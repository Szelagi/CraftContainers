/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.editor;

import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.Scheduler;
import pl.szelagi.fawe.ISchematic;
import pl.szelagi.fawe.Schematics;
import pl.szelagi.marker.DisplayableMarkers;
import pl.szelagi.buildin.controller.FakeWorldBorder;
import pl.szelagi.buildin.controller.MarkerHologram;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.component.GameMap;
import pl.szelagi.allocator.Allocators;
import pl.szelagi.fawe.FaweOperations;

public class BlueprintGameMap extends GameMap {
    private int radius = 50;
    private @Nullable ISchematic<?> schematic;

    private FakeWorldBorder worldBorder;
    private DisplayableMarkers markers;

    @Override
    public @NotNull BlueprintContainer container() {
        return (BlueprintContainer) super.container();
    }

    @Override
    public @Nullable BlueprintGameMap gameMap() {
        return (BlueprintGameMap) super.gameMap();
    }

    public BlueprintGameMap(@NotNull BlueprintContainer session) {
        super(session, Allocators.developmentAllocator());
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        new MarkerHologram(this, "center", center()).start();

        worldBorder = new FakeWorldBorder(this, center(), radius);
        worldBorder.start();

        var markersFile = container().getMarkersFile();
        if (markersFile.exists()) {
            markers = DisplayableMarkers.from(this, markersFile, center());
        } else {
            markers = DisplayableMarkers.empty(this, center());
        }
        markers.start();

        new MarkerBlockLogic(this).start();
    }

    @Override
    protected void generate() {
        var session = container();
        var schematicFile = session.getSchematicFile();
        if (schematicFile.exists()) {
            schematic = Schematics.newMassive(schematicFile, space(), center());
            radius = (schematic.maxSizeXZ() + 1) / 2;
        }

        if (schematic != null) {
            schematic.load();
        } else {
            Scheduler.runAndWait(() -> {
                center().getBlock()
                        .setType(Material.TINTED_GLASS);
            });
        }
    }

    @Override
    protected void degenerate() {
        if (schematic != null) {
            schematic.clean();
        }

        var min = space().getMin();
        var max = space().getMax();
        FaweOperations.massiveClearRegion(min, max);
    }

    private Location minLocation() {
        var center = center();
        var world = center.getWorld();
        int offset = radius - 1;

        var minX = center.getBlockX() - offset;
        var minZ = center.getBlockZ() - offset;
        return new Location(world, minX, world.getMinHeight(), minZ);
    }

    private Location maxLocation() {
        var center = center();
        var world = center.getWorld();
        int offset = radius - 1;

        var maxX = center.getBlockX() + offset;
        var maxZ = center.getBlockZ() + offset;
        return new Location(world, maxX, world.getMaxHeight(), maxZ);
    }

    public void setRadius(int radius) {
        this.radius = radius;
        this.worldBorder.setRadius(radius);
    }

    public void save() {
        var min = minLocation();
        var max = maxLocation();
        var schematicFile = container().getSchematicFile();
        try (var clipboard = FaweOperations.copy(min, max, center())) {
            FaweOperations.save(schematicFile, clipboard);
        }

        var markersFile = container().getMarkersFile();
        markers.save(markersFile, center());
    }

    public DisplayableMarkers getMarkers() {
        return markers;
    }

    public int getRadius() {
        return radius;
    }
}

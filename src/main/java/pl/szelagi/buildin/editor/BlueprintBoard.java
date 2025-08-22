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
import pl.szelagi.buildin.controller.FakeWorldBorder;
import pl.szelagi.buildin.controller.MarkerHologram;
import pl.szelagi.buildin.schematic.*;
import pl.szelagi.component.baseComponent.internalEvent.component.ComponentConstructor;
import pl.szelagi.component.board.Board;
import pl.szelagi.file.FAWESchematicLoader;
import pl.szelagi.space.Allocators;
import pl.szelagi.spatial.ISpatial;

public class BlueprintBoard extends Board {
    private int radius = 50;
    private @Nullable Schematic schematic;

    private FakeWorldBorder worldBorder;
    private DisplayableMarkers markers;

    @Override
    public @NotNull BlueprintSession session() {
        return (BlueprintSession) super.session();
    }

    @Override
    public @Nullable BlueprintBoard board() {
        return (BlueprintBoard) super.board();
    }

    public BlueprintBoard(@NotNull BlueprintSession session) {
        super(session, Allocators.developmentAllocator());
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        new MarkerHologram(this, "center", center()).start();

        worldBorder = new FakeWorldBorder(this, center(), radius);
        worldBorder.start();

        var markersFile = session().getMarkersFile();
        if (markersFile.exists()) {
            markers = DisplayableMarkers.from(this, markersFile, center());
        } else {
            markers = DisplayableMarkers.empty(this);
        }
        markers.start();

        new MarkerBlockLogic(this).start();
    }

    @Override
    public ISpatial defineSecureZone() {
        return ISpatial.clone(space());
    }

    @Override
    protected void generate() {
        var session = session();
        var schematicFile = session.getSchematicFile();
        if (schematicFile.exists()) {
            schematic = new Schematic(schematicFile, space(), center());
            radius = schematic.size();
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
            schematic.unload();
        }

        var min = space().getFirstPoint();
        var max = space().getSecondPoint();
        FAWESchematicLoader.clearRegion(min, max);
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
        var schematicFile = session().getSchematicFile();
        try (var clipboard = FAWESchematicLoader.copy(min, max, center())) {
            FAWESchematicLoader.save(schematicFile, clipboard);
        }

        var markersFile = session().getMarkersFile();
        markers.save(markersFile, center());
    }

    public DisplayableMarkers getMarkers() {
        return markers;
    }

    public int getRadius() {
        return radius;
    }
}

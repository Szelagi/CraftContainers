/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.system.testSession;

import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.Scheduler;
import pl.szelagi.SessionAPI;
import pl.szelagi.allocator.Allocators;
import pl.szelagi.allocator.ISpaceAllocator;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.fawe.ISchematic;
import pl.szelagi.fawe.Schematics;
import pl.szelagi.marker.AbstractMarkers;
import pl.szelagi.component.GameMap;
import pl.szelagi.component.container.Container;
import pl.szelagi.marker.IMarkers;
import pl.szelagi.marker.Markers;
import pl.szelagi.transform.RotAxis;
import pl.szelagi.transform.Degree;
import pl.szelagi.transform.Rotation;

import java.io.File;
import java.util.*;

public class TestGameMap extends GameMap {
    private final static String NAME = "MyGameMap";
    private final static File SCHEMATIC_FILE = ISchematic.getFile(SessionAPI.getInstance(), NAME);
    private final static File MARKERS_FILE = IMarkers.getFile(SessionAPI.getInstance(), NAME);

    private ISchematic<?> schematic;
    private Markers markers;

    public TestGameMap(@NotNull Container container, ISpaceAllocator allocator) {
        super(container, allocator);
    }

    public TestGameMap(@NotNull Container container) {
        super(container, Allocators.defaultAllocator());
    }

    @Override
    protected void generate() {
        schematic = Schematics.newMassive(SCHEMATIC_FILE, space(), center());
        schematic.load();

        markers = Markers.read(MARKERS_FILE, center());
    }

    @Override
    protected void degenerate() {
        // Allocator przydziela przestrzeń dla GameMap.
        // Jeśli ponownie używa tego samego obszaru, musisz zadbać o jego wyczyszczenie.
        // Aby tego uniknąć, zdefinuj na sztywno allocator, który automatycznie czyści przestrzeń.

        // Sprawdzamy czy allokator zamuje się czyszczeniem przestrzeni
        if (!space().requiresCleanup()) return;

        if (schematic != null)
            schematic.clean();
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        // Pobieramy unikalny marker dla generatora przedmiotów.
        var itemGeneratorMarker = markers.requireOneByName("itemGenerator");

        // Pobieramy wszystkie lokalizacje oznaczone jako "chest" i ustawiamy tam skrzynie.
        var chestLocations = markers.requireAnyLocationsByName("chest");
        for (var chestLocation : chestLocations)
            chestLocation.getBlock().setType(Material.CHEST);
    }



//    private final List<ISchematic<?>> schematics = new ArrayList<>();
//
//    public TestGameMap(Container container) {
//        super(container);
//    }
//
//    @Override
//    protected void generate() {
//        final int rooms = 4;
//        var sapi = SessionAPI.instance();
//        var spawnSchemFile = ISchematic.getFile(sapi, "droom3");
//        var spawnMarkerFile = IMarkers.getFile(sapi, "droom3");
//
//        var spawnSchematic = Schematics.newMassive(spawnSchemFile, space(), center());
//        var spawnMarkers = Markers.read(spawnMarkerFile, center());
//        var connector = spawnMarkers.getByName("connector").getFirst().getLocation();
//
//
//        for (int i = 0; i < rooms; i++) {
//            schematics.add(spawnSchematic);
//            spawnSchematic.load();
//            connector = spawnMarkers.getByName("connector").getFirst().getLocation();
//
//            final Location c = connector;
//            Scheduler.runAndWait(() -> {
//                c.getBlock().setType(Material.BEDROCK);
//            });
//
//            spawnMarkers = spawnMarkers.translateAbsoluteTo(connector).rotate(Degree.DEG_90, Rotation.CLOCKWISE, RotAxis.YAW_Y);
//            spawnSchematic = spawnSchematic.translateAbsoluteTo(connector).rotate(Degree.DEG_90, Rotation.CLOCKWISE, RotAxis.YAW_Y);
//
//        }
//    }
//
//    @Override
//    protected void degenerate() {
//        schematics.forEach(ISchematic::clean);
//    }
}

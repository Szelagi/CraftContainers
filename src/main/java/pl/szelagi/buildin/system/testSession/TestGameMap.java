/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.system.testSession;

import org.bukkit.Location;
import org.bukkit.Material;
import pl.szelagi.Scheduler;
import pl.szelagi.SessionAPI;
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

import java.util.*;

public class TestGameMap extends GameMap {
    private final List<ISchematic<?>> schematics = new ArrayList<>();

    public TestGameMap(Container container) {
        super(container);
    }

    @Override
    protected void generate() {
        final int rooms = 4;
        var sapi = SessionAPI.instance();
        var spawnSchemFile = ISchematic.getFile(sapi, "droom3");
        var spawnMarkerFile = IMarkers.getFile(sapi, "droom3");

        var spawnSchematic = Schematics.newMassive(spawnSchemFile, space(), center());
        var spawnMarkers = Markers.read(spawnMarkerFile, center());
        var connector = spawnMarkers.getByName("connector").getFirst().getLocation();


        for (int i = 0; i < rooms; i++) {
            schematics.add(spawnSchematic);
            spawnSchematic.load();
            connector = spawnMarkers.getByName("connector").getFirst().getLocation();

            final Location c = connector;
            Scheduler.runAndWait(() -> {
                c.getBlock().setType(Material.BEDROCK);
            });

            spawnMarkers = spawnMarkers.translateAbsoluteTo(connector).rotate(Degree.DEG_90, Rotation.CLOCKWISE, RotAxis.YAW_Y);
            spawnSchematic = spawnSchematic.translateAbsoluteTo(connector).rotate(Degree.DEG_90, Rotation.CLOCKWISE, RotAxis.YAW_Y);

        }
    }

    @Override
    protected void degenerate() {
        schematics.forEach(ISchematic::clean);
    }
}

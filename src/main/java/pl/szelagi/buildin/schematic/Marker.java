/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.schematic;

import org.bukkit.Location;

public class Marker {
    private final int id;
    private final String name;
    private final Location location;

    protected Marker(int id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public MarkerData toMarkerData(Location base) {
        var dx = location.getX() - base.getX();
        var dy = location.getY() - base.getY();
        var dz = location.getZ() - base.getZ();
        return new MarkerData(id, name, dx, dy, dz, location.getYaw(), location.getPitch());
    }

    @Override
    public String toString() {
        return String.format(
                "Marker #%d: %s at [x=%.0f, y=%.0f, z=%.0f]",
                id,
                name,
                location.getX(),
                location.getY(),
                location.getZ()
        );
    }
}

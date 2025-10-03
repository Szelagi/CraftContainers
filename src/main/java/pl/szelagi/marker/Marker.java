/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.marker;

import org.bukkit.Location;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class Marker {
    private final int id;
    private final String name;
    private final Location location;
    private final Metadata metadata;

    protected Marker(int id, String name, Location location, IMetadata metadata) {
        this.id = id;
        this.name = name;
        this.location = location.clone();
        this.metadata = new Metadata(metadata);
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

    public IMetadata getMetadata() {
        return metadata;
    }

    public MarkerData toMarkerData(Location base) {
        var dx = location.getX() - base.getX();
        var dy = location.getY() - base.getY();
        var dz = location.getZ() - base.getZ();
        return new MarkerData(id, name, dx, dy, dz, location.getYaw(), location.getPitch(), metadata.toMap());
    }

    @Override
    public String toString() {
        return String.format(
                "Marker %s #%d\nLocation: [x=%.0f, y=%.0f, z=%.0f]\nRotation: [yaw=%.0f, pitch=%.0f]\nMetadata:\n%s",
                name,
                id,
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch(),
                getMetadata()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Marker marker)) return false;
        return id == marker.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

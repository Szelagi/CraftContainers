/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.schematic;

import org.bukkit.Location;

import java.io.Serializable;

public class MarkerData implements Serializable {
    private final int id;
    private final String name;
    private final double xOffset;
    private final double yOffset;
    private final double zOffset;
    private final double yaw;
    private final double pitch;

    protected MarkerData(int id, String name, double xOffset, double yOffset, double zOffset, double yaw, double pitch) {
        this.id = id;
        this.name = name;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    protected Marker toMarker(Location base) {
        var ax = xOffset + base.getX();
        var ay = yOffset + base.getY();
        var az = zOffset + base.getZ();
        var location = new Location(base.getWorld(), ax, ay, az);
        return new Marker(id, name, location);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getxOffset() {
        return xOffset;
    }

    public double getyOffset() {
        return yOffset;
    }

    public double getzOffset() {
        return zOffset;
    }

    public double getYaw() {
        return yaw;
    }

    public double getPitch() {
        return pitch;
    }
}

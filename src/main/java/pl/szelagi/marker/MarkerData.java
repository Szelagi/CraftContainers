/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.marker;

import com.google.gson.annotations.SerializedName;
import org.bukkit.Location;

import java.io.Serializable;
import java.util.Map;

public class MarkerData implements Serializable {
    @SerializedName("id")
    private final int id;
    @SerializedName("name")
    private final String name;

    @SerializedName("x_offset")
    private final double xOffset;

    @SerializedName("y_offset")
    private final double yOffset;

    @SerializedName("z_offset")
    private final double zOffset;

    @SerializedName("yaw")
    private final double yaw;

    @SerializedName("pitch")
    private final double pitch;

    @SerializedName("metadata")
    private final Map<String, String> metadataMap;

    public MarkerData(int id, String name, double xOffset, double yOffset, double zOffset, double yaw, double pitch, Map<String, String> metadataMap) {
        this.id = id;
        this.name = name;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
        this.yaw = yaw;
        this.pitch = pitch;
        this.metadataMap = metadataMap;
    }

    protected Marker toMarker(Location base) {
        var ax = xOffset + base.getX();
        var ay = yOffset + base.getY();
        var az = zOffset + base.getZ();
        var location = new Location(base.getWorld(), ax, ay, az);
        var metadata = new Metadata(metadataMap);
        return new Marker(id, name, location, metadata);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getXOffset() {
        return xOffset;
    }

    public double getYOffset() {
        return yOffset;
    }

    public double getZOffset() {
        return zOffset;
    }

    public double getYaw() {
        return yaw;
    }

    public double getPitch() {
        return pitch;
    }

    public Map<String, String> getMetadataMap() {
        return metadataMap;
    }
}

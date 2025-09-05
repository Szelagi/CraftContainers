/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.spatial;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

import java.util.concurrent.atomic.AtomicInteger;

@Deprecated
public class SpatialOptimizer implements ISpatial {
    private Location first;
    private Location second;

    public SpatialOptimizer(Location first, Location second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public @NotNull Location getMin() {
        return first;
    }

    @Override
    public @NotNull Location getMax() {
        return second;
    }

    public SpatialOptimizer optimize() {
        var a = getMin();
        var b = getMax();
        var minX = (int) Math.min(a.getX(), b.getX());
        var minY = (int) Math.min(a.getY(), b.getY());
        var minZ = (int) Math.min(a.getZ(), b.getZ());
        var maxX = (int) Math.max(a.getX(), b.getX());
        var maxY = (int) Math.max(a.getY(), b.getY());
        var maxZ = (int) Math.max(a.getZ(), b.getZ());

        var min = new Vector3i(maxX, maxY, maxZ);
        var max = new Vector3i(minX, minY, minZ);

        AtomicInteger noAirCount = new AtomicInteger();

        eachBlocks(block -> {
            var material = block.getType();
            var x = block.getX();
            var y = block.getY();
            var z = block.getZ();
            if (BlockMethods.isAirMaterial(material))
                return;
            noAirCount.getAndIncrement();
            // x
            if (x < min.x)
                min.x = x;
            else if (x > max.x)
                max.x = x;
            // y
            if (y < min.y)
                min.y = y;
            else if (y > max.y)
                max.y = y;
            // z
            if (z < min.z)
                min.z = z;
            else if (z > max.z)
                max.z = z;
        });

        World world = getCenter().getWorld();

        if (noAirCount.get() >= 2) {
            this.first = new Location(world, min.x, min.y, min.z);
            this.second = new Location(world, max.x, max.y, max.z);
        } else {
            var tempCenter = getCenter();
            this.first = tempCenter;
            this.second = tempCenter;
        }

        return this;
    }
}

/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.spatial;

import org.bukkit.Location;
import org.jetbrains.annotations.TestOnly;

import java.util.HashSet;
import java.util.Set;

@Deprecated
public class SpatialPartition {
    public static Set<ISpatial> partition(ISpatial spatial, int length) {
        var first = spatial.getMin();
        var second = spatial.getMax();
        var parts = new HashSet<ISpatial>();
        var xParts = (int) Math.ceil(spatial.sizeX() / (double) length);
        var zParts = (int) Math.ceil(spatial.sizeZ() / (double) length);

        var minX = Math.min(first.getX(), second.getX());
        var minZ = Math.min(first.getZ(), second.getZ());
        var minY = Math.min(first.getY(), second.getY());
        var maxY = Math.max(first.getY(), second.getY());
        var maxX = Math.max(first.getX(), second.getX());
        var maxZ = Math.max(first.getZ(), second.getZ());
        var world = first.getWorld();

        var currentX = minX;
        for (int x = 0; x < xParts; x++) {
            var currentZ = minZ;
            final var deltaX = Math.min(length, maxX - currentX + 1);

            for (int z = 0; z < zParts; z++) {
                final var deltaZ = Math.min(length, maxZ - currentZ + 1);

                var firstPointPart = new Location(world, currentX, minY, currentZ);
                var secondPointPart = new Location(world, currentX + deltaX - 1, maxY, currentZ + deltaZ - 1);
                var spatialPart = new Spatial(firstPointPart, secondPointPart);
                parts.add(spatialPart);

                currentZ += deltaZ;
            }
            currentX += deltaX;
        }
        return parts;
    }

    @TestOnly
    public static boolean testPartition(ISpatial spatial, Set<ISpatial> parts) {
        var first = spatial.getMin();
        var second = spatial.getMax();
        var minX = (int) Math.min(first.getX(), second.getX());
        var minZ = (int) Math.min(first.getZ(), second.getZ());
        var maxX = (int) Math.max(first.getX(), second.getX());
        var maxZ = (int) Math.max(first.getZ(), second.getZ());

        boolean[][] covered = new boolean[maxX - minX + 1][maxZ - minZ + 1];
        for (var part : parts) {
            int startX = (int) part.getMin().getX();
            int endX = (int) part.getMax().getX();
            int startZ = (int) part.getMin().getZ();
            int endZ = (int) part.getMax().getZ();

            for (int x = startX; x <= endX; x++) {
                for (int z = startZ; z <= endZ; z++) {
                    if (covered[x - minX][z - minZ]) {
                        System.out.println("Overlap found at X: " + x + ", Z: " + z);
                        return false;
                    }
                    covered[x - minX][z - minZ] = true;
                }
            }
        }
        for (int x = 0; x < covered.length; x++) {
            for (int z = 0; z < covered[x].length; z++) {
                if (!covered[x][z]) {
                    System.out.println("Gap found at X: " + (x + minX) + ", Z: " + (z + minZ));
                    return false;
                }
            }
        }
        System.out.println("Verification complete.");
        return true;
    }
}

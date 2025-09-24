/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.spatial;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.joml.Vector3i;
import pl.szelagi.CraftContainers;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Deprecated
public class SpatialMinimalize {
    private static final int PART_SIZE = 70;

    public static void async(ISpatial spatial, Consumer<ISpatial> callback) {
        var parts = spatial.partition(PART_SIZE);
        var iterator = parts.iterator();
        var resolves = new HashSet<SpatialResolve>();
        var instance = CraftContainers.instance();

        var nextElement = new Runnable() {
            @Override
            public void run() {
                var resolved = resolve(iterator.next());
                resolves.add(resolved);
                if (iterator.hasNext()) {
                    Bukkit.getScheduler().runTaskLater(instance, this, 1L);
                } else {
                    var minimalized = minimalize(spatial.getMin().getWorld(), resolves);
                    callback.accept(minimalized);
                }
            }
        };

        Bukkit.getScheduler().runTask(instance, nextElement);
    }

    public static ISpatial sync(ISpatial spatial) {
        var parts = spatial.partition(PART_SIZE);
        var resolves = new HashSet<SpatialResolve>();
        for (var part : parts) {
            var resolved = resolve(part);
            resolves.add(resolved);
        }
        return minimalize(spatial.getMin().getWorld(), resolves);
    }

    private static ISpatial minimalize(World world, Collection<SpatialResolve> resolves) {
        boolean isInitialized = false;
        var globalMin = new Vector3i(0, 0, 0);
        var globalMax = new Vector3i(0, 0, 0);

        for (var res : resolves) {
            if (!res.isValid()) continue;
            if (!isInitialized) {
                isInitialized = true;
                globalMin = res.min();
                globalMax = res.max();
                continue;
            }
            globalMin.x = Math.min(globalMin.x, res.min().x);
            globalMax.x = Math.max(globalMax.x, res.max().x);

            globalMin.y = Math.min(globalMin.y, res.min().y);
            globalMax.y = Math.max(globalMax.y, res.max().y);

            globalMin.z = Math.min(globalMin.z, res.min().z);
            globalMax.z = Math.max(globalMax.z, res.max().z);
        }

        var newFirst = new Location(world, globalMin.x, globalMin.y, globalMin.z);
        var newSecond = new Location(world, globalMax.x, globalMax.y, globalMax.z);
        return new Spatial(newFirst, newSecond);
    }


    private static SpatialResolve resolve(ISpatial spatial) {
        var min = new Vector3i(0, 0, 0);
        var max = new Vector3i(0, 0, 0);
        var hasBlock = new AtomicBoolean(false);

        spatial.eachBlocks(block -> {
            var material = block.getType();
            var x = block.getX();
            var y = block.getY();
            var z = block.getZ();
            if (BlockMethods.isAirMaterial(material))
                return;

            if (!hasBlock.get()) {
                hasBlock.set(true);
                min.set(x, y, z);
                max.set(x, y, z);
            }

            min.x = Math.min(min.x, x);
            max.x = Math.max(max.x, x);

            min.y = Math.min(min.y, y);
            max.y = Math.max(max.y, y);

            min.z = Math.min(min.z, z);
            max.z = Math.max(max.z, z);
        });

        return new SpatialResolve(hasBlock.get(), min, max);
    }
}

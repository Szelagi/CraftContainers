/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.space;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import pl.szelagi.ConfigManager;
import pl.szelagi.Scheduler;
import pl.szelagi.SessionAPI;
import pl.szelagi.util.pool.PoolAllocator;
import pl.szelagi.util.pool.GenerativeAllocatorWrapper;
import pl.szelagi.util.pool.SimplePool;
import pl.szelagi.util.timespigot.Time;
import pl.szelagi.world.EmptyChunkGenerator;

import java.util.Set;

public class Allocators {
    private static ISpaceAllocator productionAllocator;
    private static ISpaceAllocator developmentAllocator;

    private static ISpaceAllocator createDefaultAllocator(int minSize) {
        var allocator = new RegionAllocator(ConfigManager.config().maxBoardSize, ConfigManager.config().distanceBetweenMaps, new EmptyChunkGenerator(), world -> {
            Scheduler.runTaskTimer(() -> {
                world.setTime(13000L);
            }, Time.zero(), Time.seconds(450));

            Bukkit.getPluginManager().registerEvents(new WorldEnvironment(world), SessionAPI.instance());
        });

        var generativeAllocator = new GenerativeAllocatorWrapper(allocator);
        var pool = new SimplePool<IAllocate>(minSize) {
            @Override
            protected IAllocate creator() {
                return generativeAllocator.allocate();
            }

            @Override
            protected void releaser(IAllocate allocate) {
                allocate.deallocate();
            }
        };

        return new PoolAllocator(generativeAllocator, pool);
    }

    public static void initialize() {
        productionAllocator = createDefaultAllocator(6);
        developmentAllocator = createDefaultAllocator(2);
    }

    public static ISpaceAllocator productionAllocator() {
        return productionAllocator;
    }

    public static ISpaceAllocator developmentAllocator() {
        return developmentAllocator;
    }

    private record WorldEnvironment(World world) implements Listener {
        @EventHandler(ignoreCancelled = true)
            public void onCreatureSpawn(CreatureSpawnEvent event) {
                // no natural spawn
                if (!event.getEntity().getWorld()
                        .getName()
                        .equals(world.getName()))
                    return;
                if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL)
                    return;
                event.setCancelled(true);
            }
        }
}

/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.allocator;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitTask;
import pl.szelagi.Scheduler;
import pl.szelagi.CraftContainers;
import pl.szelagi.util.timespigot.Time;

import java.util.HashMap;
import java.util.Map;

public class Allocators {
    private static ISpaceAllocator blueprintAllocator;
    private static ISpaceAllocator defaultLazyAllocator;
    private static ISpaceAllocator defaultRecyclingAllocator;
    private static Map<World, BukkitTask> worldBukkitTaskMap = new HashMap<>();
    private static Map<World, Listener> worldListenerMap = new HashMap<>();

    public static void registerDefaultWorldLogic(World world) {
        var task = Scheduler.runTaskTimer(
                () -> world.setTime(13000L),
                Time.zero(),
                Time.seconds(450));
        worldBukkitTaskMap.put(world, task);

        var listener = new WorldEnvironment(world);
        Bukkit.getPluginManager().registerEvents(listener, CraftContainers.instance());
        worldListenerMap.put(world, listener);
    }

    public static void unregisterDefaultWorldLogic(World world) {
        var task = worldBukkitTaskMap.remove(world);
        task.cancel();

        var listener = worldListenerMap.remove(world);
        HandlerList.unregisterAll(listener);
    }

    private static ISpaceAllocator createDefaultLazyAllocator(int worldMaxAlloc) {
        var regionSize = CraftContainers.config().maxRegionSize;
        var spaceGap = CraftContainers.config().distanceBetweenRegions;

        var allocator = new LazyRegionAllocator(worldMaxAlloc, regionSize, spaceGap, null, Allocators::registerDefaultWorldLogic, Allocators::unregisterDefaultWorldLogic);
        return allocator;
    }

    private static ISpaceAllocator createDefaultRecyclingAllocator() {
        var regionSize = CraftContainers.config().maxRegionSize;
        var spaceGap = CraftContainers.config().distanceBetweenRegions;

        var allocator = new RecyclingRegionAllocator(regionSize, spaceGap, null, Allocators::registerDefaultWorldLogic, Allocators::unregisterDefaultWorldLogic);
        return allocator;
    }

    public static void initialize() {
        var lazyBlueprintMaxAlloc = CraftContainers.config().lazyBlueprintMaxAllocs;
        var lazyWorldMaxAlloc = CraftContainers.config().lazyWorldMaxAllocs;

        blueprintAllocator = createDefaultLazyAllocator(lazyBlueprintMaxAlloc);
        defaultLazyAllocator = createDefaultLazyAllocator(lazyWorldMaxAlloc);
        defaultRecyclingAllocator = createDefaultRecyclingAllocator();
    }

    public static ISpaceAllocator blueprintAllocator() {
        return blueprintAllocator;
    }

    public static ISpaceAllocator defaultLazyAllocator() {
        return defaultLazyAllocator;
    }

    public static ISpaceAllocator defaultRecyclingAllocator() {
        return defaultRecyclingAllocator;
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

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
import pl.szelagi.util.timespigot.Time;
import pl.szelagi.world.EmptyChunkGenerator;

public class Allocators {
    private static RegionAllocator regionAllocator;

    public static void initialize() {
        regionAllocator = new RegionAllocator(ConfigManager.config().maxBoardSize, ConfigManager.config().distanceBetweenMaps, new EmptyChunkGenerator(), world -> {
            Scheduler.runTaskTimer(() -> {
                world.setTime(13000L);
            }, Time.zero(), Time.seconds(450));

            Bukkit.getPluginManager().registerEvents(new WorldEnvironment(world), SessionAPI.instance());
        });

        regionAllocator.initialize();
    }

    public static RegionAllocator defaultAllocator() {
        return regionAllocator;
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

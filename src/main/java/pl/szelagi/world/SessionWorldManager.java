/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.world;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.codehaus.plexus.util.FileUtils;
import pl.szelagi.SessionAPI;

import java.io.IOException;

import static org.bukkit.Bukkit.getServer;

public class SessionWorldManager {
    private static final String SESSION_WORLD_NAME = "session_world";
    private static World SESSION_WORLD;

    public static void initialize(JavaPlugin plugin) {
        var baseWorld = Bukkit.getWorld("world");
        assert baseWorld != null;

        var sessionWorld = Bukkit.getWorld(SESSION_WORLD_NAME);
        if (sessionWorld != null) {
            for (var p : plugin.getServer()
                    .getOnlinePlayers()) {
                if (p.getWorld().getName()
                        .equals(sessionWorld.getName())) {
                    p.teleport(baseWorld.getSpawnLocation());
                }
            }
            Bukkit.unloadWorld(sessionWorld, false);
            var directory = sessionWorld.getWorldFolder();
            sessionWorld.getWorldFolder()
                    .deleteOnExit();
            try {
                FileUtils.deleteDirectory(directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        var worldCreator = new WorldCreator(SESSION_WORLD_NAME);
        worldCreator.generator(new EmptyChunkGenerator());
        SESSION_WORLD = worldCreator.createWorld();

        // keep night always
        getServer().getScheduler()
                .runTaskTimer(SessionAPI.getInstance(), () -> SessionWorldManager
                        .getSessionWorld()
                        .setTime(13000L), 0, 9000);

        // start session world environment logic
        // + no natural spawn
        getServer().getPluginManager()
                .registerEvents(new WorldEnvironment(), SessionAPI.getInstance());
    }

    private static class WorldEnvironment implements Listener {
        @EventHandler(ignoreCancelled = true)
        public void onCreatureSpawn(CreatureSpawnEvent event) {
            // no natural spawn
            if (!event.getEntity().getWorld()
                    .getName()
                    .equals(getSessionWorld().getName()))
                return;
            if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL)
                return;
            event.setCancelled(true);
        }
    }

    public static World getSessionWorld() {
        return SESSION_WORLD;
    }
}
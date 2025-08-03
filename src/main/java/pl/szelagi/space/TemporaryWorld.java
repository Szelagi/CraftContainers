/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.space;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;

public class TemporaryWorld {
    private static final String FLAG = ".tmp-world";

    private static void teleportPlayersToPrimaryWorld(World selectedWorld) {
        var primaryWorld = Bukkit.getWorlds().getFirst();
        selectedWorld.getPlayers().forEach(player -> {
            player.teleport(primaryWorld.getSpawnLocation());
        });
    }

    public static void clean() {
        var worldContainer = Bukkit.getWorldContainer();
        var files = worldContainer.listFiles();
        if (files == null) files = new File[] {};
        for (var file : files) {
            if (!file.isDirectory()) continue;
            var levelDat = new File(file, "level.dat");
            if (!levelDat.exists()) continue;

            var flag = new File(file, FLAG);
            if (!flag.exists()) continue;

            var world = Bukkit.getWorld(file.getName());
            if (world != null) {
                teleportPlayersToPrimaryWorld(world);
                Bukkit.unloadWorld(world, false);
            }

            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void markTemporary(World world) {
        var file = world.getWorldFolder();
        var flag = new File(file, FLAG);
        try {
            flag.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void unmarkTemporary(World world) {
        var file = world.getWorldFolder();
        var flag = new File(file, FLAG);
        try {
            flag.delete();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

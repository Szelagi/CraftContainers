/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.allocator;

import net.kyori.adventure.util.TriState;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.function.Consumer;

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
                unloadWorld(world);
            }
            deleteWorld(file);
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

    public static World createTemporaryWorld(@Nullable Consumer<WorldCreator> worldCreatorConsumer,
                                             @Nullable Consumer<World> worldConsumer) {
        var name = "tmp_world_" + UUID.randomUUID().toString().replace("-", "");
        var worldCreator = new WorldCreator(name);
        worldCreator.generator(new EmptyChunkGenerator());

        worldCreator.keepSpawnLoaded(TriState.FALSE);

        if (worldCreatorConsumer != null)
            worldCreatorConsumer.accept(worldCreator);

        var world = worldCreator.createWorld();
        assert world != null;

        world.setAutoSave(false);

        TemporaryWorld.markTemporary(world);

        if (worldConsumer != null)
            worldConsumer.accept(world);
        return world;
    }

    public static void deleteTemporaryWorld(@NotNull World world) {
        var worldFolder = world.getWorldFolder();

        var flag = new File(worldFolder, FLAG);
        if (!flag.exists()) {
            throw new IllegalArgumentException("World is not temporary!");
        }

        unloadWorld(world);
        deleteWorld(worldFolder);
    }

    private static void unloadWorld(World world) {
        teleportPlayersToPrimaryWorld(world);
        Bukkit.unloadWorld(world, false);
    }

    private static void deleteWorld(File worldFolder) {
        try {
            FileUtils.deleteDirectory(worldFolder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

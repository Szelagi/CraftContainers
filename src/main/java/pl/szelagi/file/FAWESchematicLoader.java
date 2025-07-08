/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.file;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.spatial.ISpatial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FAWESchematicLoader {
    public static boolean exists(String filePath) {
        var file = new File(filePath);
        return file.exists() && file.isFile() && file.canRead();
    }

    public static void load(@NotNull String filePath, @NotNull Location toLocation) throws SchematicException {
        File file = new File(filePath);
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        World adaptedWorld = BukkitAdapter.adapt(toLocation.getWorld());
        Clipboard clipboard;
        FileInputStream fis;
        ClipboardReader reader;
        BlockVector3 to = BukkitAdapter.asBlockVector(toLocation);
        try {
            fis = new FileInputStream(file);
            assert format != null;
            reader = format.getReader(fis);
            clipboard = reader.read();
            try (EditSession editSession = WorldEdit
                    .getInstance()
                    .newEditSession(adaptedWorld)) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .copyEntities(false)
                        .copyBiomes(true).to(to)
                        // configure here
                        .build();
                Operations.complete(operation);
            } catch (WorldEditException e) {
                throw new SchematicException(e.getMessage());
            }
        } catch (Exception e) {
            throw new SchematicException(e.getMessage());
        }
    }

    public static void save(@NotNull String filePath, @NotNull Location location1, @NotNull Location location2, @NotNull Location baseLocation) throws SchematicException {
        var file = new File(filePath);
        try {
            file.createNewFile();
        } catch (Exception e) {
            throw new SchematicException(e.getMessage());
        }

        var world = BukkitAdapter.adapt(location1.getWorld());
        var loc1 = BukkitAdapter.asBlockVector(location1);
        var loc2 = BukkitAdapter.asBlockVector(location2);
        var to = BukkitAdapter.asBlockVector(baseLocation);

        BlockVector3 min;
        BlockVector3 max;
        if (location1.getBlockY() < location2.getBlockY()) {
            min = loc1;
            max = loc2;
        } else {
            min = loc2;
            max = loc1;
        }

        CuboidRegion region = new CuboidRegion(world, min, max);
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        clipboard.setOrigin(to);

        var forwardExtentCopy = new ForwardExtentCopy(world, region, clipboard, region.getMinimumPoint());

        forwardExtentCopy.setCopyingBiomes(true);
        forwardExtentCopy.setCopyingEntities(false);
        forwardExtentCopy.setRemovingEntities(true);

        try {
            Operations.complete(forwardExtentCopy);
            try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
                writer.write(clipboard);
            } catch (Exception e) {
                throw new SchematicException(e.getMessage());
            }
        } catch (Exception e) {
            throw new SchematicException(e.getMessage());
        }
    }

    public static @NotNull ISpatial loadToSpatial(@NotNull String filePath, @NotNull Location baseLocation) throws SchematicException {
        File file = new File(filePath);
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        Clipboard clipboard;
        FileInputStream fis;
        ClipboardReader reader;
        try {
            fis = new FileInputStream(file);
            assert format != null;
            reader = format.getReader(fis);
            clipboard = reader.read();

            var min = clipboard.getMinimumPoint();
            var max = clipboard.getMaximumPoint();
            var origin = clipboard.getOrigin();

            var deltaMin = min.subtract(origin);
            var deltaMax = max.subtract(origin);
            var baseVector3 = BukkitAdapter.asBlockVector(baseLocation);

            var world = baseLocation.getWorld();
            var firstPoint = BukkitAdapter.adapt(world, deltaMin.add(baseVector3));
            var secondPoint = BukkitAdapter.adapt(world, deltaMax.add(baseVector3));

            return new ISpatial() {
                @Override
                public @NotNull Location getFirstPoint() {
                    return firstPoint;
                }

                @Override
                public @NotNull Location getSecondPoint() {
                    return secondPoint;
                }
            };
        } catch (Exception e) {
            throw new SchematicException(e.getMessage());
        }
    }

    public static void saveEmptySchematic(@NotNull String filePath, @NotNull Location location1, @NotNull Location location2, @NotNull Location baseLocation) throws SchematicException {
        var file = new File(filePath);
        try {
            file.createNewFile();
        } catch (Exception e) {
            throw new SchematicException(e.getMessage());
        }

        var world = BukkitAdapter.adapt(location1.getWorld());
        var loc1 = BukkitAdapter.asBlockVector(location1);
        var loc2 = BukkitAdapter.asBlockVector(location2);
        var to = BukkitAdapter.asBlockVector(baseLocation);

        BlockVector3 min;
        BlockVector3 max;
        if (location1.getBlockY() < location2.getBlockY()) {
            min = loc1;
            max = loc2;
        } else {
            min = loc2;
            max = loc1;
        }

        CuboidRegion region = new CuboidRegion(world, min, max);
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        clipboard.setOrigin(to);

        try (EditSession editSession = WorldEdit
                .getInstance()
                .newEditSession(world)) {
            for (BlockVector3 vec : region) {
                assert BlockTypes.AIR != null;
                clipboard.setBlock(vec, BlockTypes.AIR.getDefaultState());
            }
        } catch (Exception e) {
            throw new SchematicException("Error while setting blocks to AIR: " + e.getMessage());
        }

        try {
            try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
                writer.write(clipboard);
            } catch (Exception e) {
                throw new SchematicException(e.getMessage());
            }
        } catch (Exception e) {
            throw new SchematicException(e.getMessage());
        }
    }


    public static void setBlocks(@NotNull Location location1, @NotNull Location location2, Material material) {
        var world = BukkitAdapter.adapt(location1.getWorld());

        BlockVector3 min = BlockVector3.at(
                Math.min(location1.getBlockX(), location2.getBlockX()),
                Math.min(location1.getBlockY(), location2.getBlockY()),
                Math.min(location1.getBlockZ(), location2.getBlockZ())
        );

        BlockVector3 max = BlockVector3.at(
                Math.max(location1.getBlockX(), location2.getBlockX()),
                Math.max(location1.getBlockY(), location2.getBlockY()),
                Math.max(location1.getBlockZ(), location2.getBlockZ())
        );

        var region = (Region) new CuboidRegion(world, min, max);
        var blockState = BukkitAdapter.adapt(material.createBlockData());

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            editSession.setBlocks(region, blockState);
        }
    }

}

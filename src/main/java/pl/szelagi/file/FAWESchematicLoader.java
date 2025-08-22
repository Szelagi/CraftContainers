/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.file;

import com.fastasyncworldedit.bukkit.adapter.FaweAdapter;
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
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.spatial.ISpatial;

import java.io.*;
import java.nio.file.Files;

public class FAWESchematicLoader {
    public static boolean exists(String filePath) {
        var file = new File(filePath);
        return file.exists() && file.isFile() && file.canRead();
    }

    // Zamienia współrzędne lokalne punktu minimalnego schematu na współrzędne absolutne bazując na origin absolutnym
    public static Location getAbsoluteMin(Clipboard clipboard, Location absoluteOrigin) {
        return getAbsolutePoint(clipboard, absoluteOrigin, clipboard.getMaximumPoint());
    }

    // Zamienia współrzędne lokalne punktu maksymalnego schematu na współrzędne absolutne bazując na origin absolutnym
    public static Location getAbsoluteMax(Clipboard clipboard, Location absoluteOrigin) {
        return getAbsolutePoint(clipboard, absoluteOrigin, clipboard.getMaximumPoint());
    }

    // Funkcja pomocnicza, która zamienia punkt lokalny na współrzędne absolutne bazując na origin
    private static Location getAbsolutePoint(Clipboard clipboard, Location absoluteOrigin, BlockVector3 localPoint) {
        var originLocal = clipboard.getOrigin();
        int offsetX = absoluteOrigin.getBlockX() - originLocal.x();
        int offsetY = absoluteOrigin.getBlockY() - originLocal.y();
        int offsetZ = absoluteOrigin.getBlockZ() - originLocal.z();
        int absoluteMaxX = localPoint.x() + offsetX;
        int absoluteMaxY = localPoint.y() + offsetY;
        int absoluteMaxZ = localPoint.z() + offsetZ;
        var world = absoluteOrigin.getWorld();
        return new Location(world, absoluteMaxX, absoluteMaxY, absoluteMaxZ);
    }

    // Efektywna funkcja czyszcząca teren (zamiana na bloki powietrza)
    public static void clearRegion(Location minLoc, Location maxLoc) {
        var world = BukkitAdapter.adapt(minLoc.getWorld());
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            var min = BukkitAdapter.asBlockVector(minLoc);
            var max = BukkitAdapter.asBlockVector(maxLoc);
            var region = (Region) new CuboidRegion(world, min, max);

            var blockType = BlockTypes.AIR;
            assert blockType != null;
            var state = blockType.getDefaultState();

            editSession.setBlocks(region, state);
            editSession.flushQueue();
        }
    }

    // Wczytuje plik schematu i zwraca go jako Clipboard
    public static Clipboard read(@NotNull File file) {
        var format = ClipboardFormats.findByFile(file);
        if (format == null) {
            throw new IllegalArgumentException("Invalid schematic format: " + file.getName());
        }

        try (var fis = new FileInputStream(file)) {
            var reader = format.getReader(fis);
            return reader.read();
        } catch (IOException s) {
            throw new RuntimeException("Error reading schematic file: " + file.getName(), s);
        }
    }

    // Zwraca punkt minimalny w formacie WorldEdit
    private static BlockVector3 minPoint(Location pos1, Location pos2) {
        return BlockVector3.at(
                Math.min(pos1.getBlockX(), pos2.getBlockX()),
                Math.min(pos1.getBlockY(), pos2.getBlockY()),
                Math.min(pos1.getBlockZ(), pos2.getBlockZ())
        );
    }

    // Zwraca punkt maksymalny w formacie WorldEdit
    private static BlockVector3 maxPoint(Location pos1, Location pos2) {
        return BlockVector3.at(
                Math.max(pos1.getBlockX(), pos2.getBlockX()),
                Math.max(pos1.getBlockY(), pos2.getBlockY()),
                Math.max(pos1.getBlockZ(), pos2.getBlockZ())
        );
    }


    // Tworzy clipboard z podanego obszaru kopiuje tylko bloki (zwraz z NBT), nie kopiuje biomów i mobów
    public static Clipboard copy(@NotNull Location pos1, @NotNull Location pos2, @NotNull Location originLoc) {
        if (!pos1.getWorld().equals(pos2.getWorld())) {
            throw new IllegalArgumentException("Not same world: " + pos1.getWorld().getName() + " and " + pos2.getWorld().getName());
        }
        var world = BukkitAdapter.adapt(pos1.getWorld());
        var min = minPoint(pos1, pos2);
        var max = maxPoint(pos1, pos2);
        var region = new CuboidRegion(world, min, max);
        var clipboard = new BlockArrayClipboard(region);
        clipboard.setOrigin(BukkitAdapter.asBlockVector(originLoc));

        var copy = new ForwardExtentCopy(world, region, clipboard, region.getMinimumPoint());
        copy.setCopyingBiomes(false);
        copy.setCopyingEntities(false);
        Operations.complete(copy);

        return clipboard;
    }

    // Zapisuje clipboard do pliku
    public static void save(File file, Clipboard clipboard) {
        try {
            var ignore = file.getParentFile().mkdirs();
            try (var writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
                writer.write(clipboard);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

        // Wkleja clipboard w miejsce origin
    public static void paste(@NotNull Clipboard clipboard, @NotNull Location origin) {
        var weWorld = BukkitAdapter.adapt(origin.getWorld());
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
            var pastePosition = BukkitAdapter.asBlockVector(origin);

            var operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(pastePosition)
                    .ignoreAirBlocks(true)
                    .build();

            // Wykonaj operację
            Operations.complete(operation);
        }
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
            var ignore = file.getParentFile().mkdirs();


            try (var fos = new FileOutputStream(file)) {
                var writer = ClipboardFormats.findByExplicitExtension("schem");
                assert writer != null;
            }

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

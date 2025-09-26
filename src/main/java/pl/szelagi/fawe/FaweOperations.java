/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.fawe;

import com.fastasyncworldedit.core.extent.processor.lighting.RelightMode;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.transform.Axis;
import pl.szelagi.transform.RotAxis;
import pl.szelagi.transform.Degree;
import pl.szelagi.transform.Rotation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FaweOperations {
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
            try (var writer = BuiltInClipboardFormat.SPONGE_V3_SCHEMATIC.getWriter(new FileOutputStream(file))) {
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

            Operations.complete(operation);
        }
    }

    public static void massivePaste(@NotNull Clipboard clipboard, @NotNull Location origin) {
        var holder = new ClipboardHolder(clipboard);
        massivePaste(holder, origin);
    }

    // Wkleja clipboard w miejsce origin
    public static void massivePaste(@NotNull ClipboardHolder holder, @NotNull Location origin) {
        var world = BukkitAdapter.adapt(origin.getWorld());
        try (EditSession editSession = WorldEdit.getInstance()
                .newEditSessionBuilder()
                .world(world)
                .fastMode(true)
                .relightMode(RelightMode.OPTIMAL)
                .limitUnlimited()
                .checkMemory(false)
                .build()) {
            editSession.setBatchingChunks(true);
            var pastePosition = BukkitAdapter.asBlockVector(origin);

            var operation = holder
                    .createPaste(editSession)
                    .to(pastePosition)
                    .ignoreAirBlocks(true)
                    .build();

            Operations.complete(operation);
        }
    }

    // Efektywna funkcja czyszcząca teren (zamiana na bloki powietrza)
    public static void massiveClearRegion(Location minLoc, Location maxLoc) {
        var world = BukkitAdapter.adapt(minLoc.getWorld());
        try (EditSession editSession = WorldEdit.getInstance()
                .newEditSessionBuilder()
                .world(world)
                .fastMode(true)
                .relightMode(RelightMode.NONE)
                .limitUnlimited()
                .checkMemory(false)
                .build()) {
            editSession.setBatchingChunks(true);
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

    public static void setBlock(Location pos1, Location pos2, Material material) {
        if (!pos1.getWorld().getName().equals(pos2.getWorld().getName()))
            throw new IllegalArgumentException("Pos1 and pos2 have different world");

        var world = BukkitAdapter.adapt(pos1.getWorld());
        try (EditSession editSession = WorldEdit.getInstance()
                .newEditSessionBuilder()
                .world(world)
                .fastMode(true)
                .relightMode(RelightMode.OPTIMAL)
                .limitUnlimited()
                .checkMemory(false)
                .build()) {

            var min = minPoint(pos1, pos2);
            var max = maxPoint(pos1, pos2);
            var region = (Region) new CuboidRegion(world, min, max);

            var state = BukkitAdapter.asBlockType(material).getDefaultState();
            editSession.setBlocks(region, state);
            editSession.flushQueue();
        }

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

    public static Clipboard getClipboard(ClipboardHolder holder) {
        var clipboards = holder.getClipboards();
        if (clipboards.size() != 1)
            throw new IllegalArgumentException("Must have exactly one clipboard");
        return clipboards.getFirst();
    }

    // ClipboardHolder
    public static Location getAbsoluteMin(ClipboardHolder holder, Location absoluteOrigin) {
        return getAbsolutePoint(holder, absoluteOrigin, getClipboard(holder).getMinimumPoint());
    }

    public static Location getAbsoluteMax(ClipboardHolder holder, Location absoluteOrigin) {
        return getAbsolutePoint(holder, absoluteOrigin, getClipboard(holder).getMaximumPoint());
    }



    public static Location getAbsolutePoint(ClipboardHolder holder, Location absoluteOrigin, BlockVector3 localPoint) {
        var clipboard = getClipboard(holder);
        var originLocal = clipboard.getOrigin();

        // 1. Punkt względem originu schematu
        var relative = localPoint.subtract(originLocal);

        // 2. Zastosuj transformację na tym punkcie
        var transformed = holder.getTransform().apply(relative.toVector3()).toBlockPoint();

        // 3. Przesuń do absolutnego originu w świecie
        var absolute = transformed.add(
                absoluteOrigin.getBlockX(),
                absoluteOrigin.getBlockY(),
                absoluteOrigin.getBlockZ()
        );

        return new Location(absoluteOrigin.getWorld(), absolute.x(), absolute.y(), absolute.z());
    }

    public static void translateRelative(Clipboard clipboard, ClipboardHolder holder, int dx, int dy, int dz) {
        var origin = clipboard.getOrigin();
        var moveToOrigin = new AffineTransform().translate(-origin.x(), -origin.y(), -origin.z());
        var shift = new AffineTransform().translate(dx, dy, dz);
        var moveBack = new AffineTransform().translate(origin.x(), origin.y(), origin.z());
        var transform = moveBack.combine(shift).combine(moveToOrigin);
        holder.setTransform(holder.getTransform().combine(transform));
    }

    public static void rotate(Clipboard clipboard, ClipboardHolder holder, Degree angle, Rotation direction, RotAxis axis) {
        var transform = new AffineTransform();

        var degree = angle.getDegree() * direction.getFactor();
        transform = switch (axis) {
            case PITCH_X -> transform.rotateX(degree);
            case YAW_Y -> transform.rotateY(degree);
            case ROLL_Z -> transform.rotateZ(degree);
        };

        holder.setTransform(holder.getTransform().combine(transform));
    }

    public static void flip(Clipboard clipboard, ClipboardHolder holder, Axis axis) {
        var transform = axis.flipTransform();
        holder.setTransform(holder.getTransform().combine(transform));
    }

}

/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.space;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

public class RegionAllocate implements IAllocate {
    private final ISpaceAllocator allocator;
    private final int slot;
    private final World world;
//    private final Location startPoint;
//    private final Location endPoint;
    private final Location startAreaPoint;
    private final Location endAreaPoint;

    public RegionAllocate(int regionSize, int spaceSize, int slot, World world, ISpaceAllocator allocator) {
        this.slot = slot;
        this.world = world;
        this.allocator = allocator;

        var totalSize = 2 * regionSize + spaceSize;

        final int worldMaxHeight = world.getMaxHeight();
        final int worldMinHeight = world.getMinHeight();

        int startPoint = slot * totalSize;
        int endPoint = (slot + 1) * totalSize - 1;
        int startSpace = startPoint + regionSize;
        int endSpace = endPoint - regionSize;
        this.startAreaPoint = new Location(world, startPoint, worldMinHeight, 0);
        this.endAreaPoint = new Location(world, endPoint, worldMaxHeight, totalSize - 1);
//        this.startPoint = new Location(world, startSpace, worldMinHeight, regionSize);
//        this.endPoint = new Location(world, endSpace, worldMaxHeight, totalSize - regionSize - 1);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RegionAllocate that = (RegionAllocate) o;
        return slot == that.slot && Objects.equals(world, that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slot, world);
    }

    @Override
    public @NotNull Location getFirstPoint() {
        return startAreaPoint;
    }

    @Override
    public @NotNull Location getSecondPoint() {
        return endAreaPoint;
    }

    @Override
    public ISpaceAllocator getAllocator() {
        return allocator;
    }

    public int getSlot() {
        return slot;
    }

    public World getWorld() {
        return world;
    }

}

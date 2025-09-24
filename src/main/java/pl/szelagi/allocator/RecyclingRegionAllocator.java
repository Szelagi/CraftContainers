/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.allocator;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class RecyclingRegionAllocator implements ISpaceAllocator {
    private static final long TIME_BLOCK = 60_000;
    private final int regionSize;
    private final int spaceGap;
    private final @Nullable Consumer<World> destroyWorldConsumer;
    private final Map<Integer, RegionAllocate> allocatedSpaces = new HashMap<>();
    private final Map<Integer, Long> timeLocks = new HashMap<>();
    private World world;
    boolean isDestroyed = false;

    public RecyclingRegionAllocator(int regionSize, int spaceGap) {
        this(regionSize, spaceGap, null, null, null);
    }

    public RecyclingRegionAllocator(int regionSize,
                                    int spaceGap,
                                    @Nullable Consumer<WorldCreator> worldCreatorConsumer,
                                    @Nullable Consumer<World> worldConsumer,
                                    @Nullable Consumer<World> destroyWorldConsumer) {
        this.regionSize = regionSize;
        this.spaceGap = spaceGap;
        this.destroyWorldConsumer = destroyWorldConsumer;
        world = TemporaryWorld.createTemporaryWorld(worldCreatorConsumer, worldConsumer);
    }

    private boolean isAllocatedSlot(int slot) {
        return allocatedSpaces.containsKey(slot);
    }

    private boolean isLockedSlot(int slot) {
        var millis = timeLocks.get(slot);
        if (millis == null) return false;
        var isLocked = System.currentTimeMillis() - millis < TIME_BLOCK;
        if (!isLocked) timeLocks.remove(slot);
        return isLocked;
    }

    @Override
    public IAllocate allocate() {
        if (isDestroyed()) throw new AllocatorDestroyedException(this);

        var slot = 0;
        while (isAllocatedSlot(slot) || isLockedSlot(slot))
            slot++;
        var allocate = new RegionAllocate(regionSize, spaceGap, slot, world, this, true);
        allocatedSpaces.put(slot, allocate);
        return allocate;
    }

    @Override
    public void deallocate(IAllocate allocate) {
        if (!allocate.getAllocator().equals(this) || (!(allocate instanceof RegionAllocate regionAllocate)))
            throw new IllegalArgumentException("This allocator is not responsible for the provided allocation.");
        var slot = regionAllocate.getSlot();
        if (!allocatedSpaces.containsKey(slot))
            throw new IllegalArgumentException("Allocation already released.");
        allocatedSpaces.remove(regionAllocate.getSlot());
        timeLocks.put(regionAllocate.getSlot(), System.currentTimeMillis());
    }

    @Override
    public boolean isAllocated(IAllocate allocate) {
        if (!allocate.getAllocator().equals(this) || !(allocate instanceof RegionAllocate regionAllocate))
            return false;
        return isAllocatedSlot(regionAllocate.getSlot());
    }

    @Override
    public Set<IAllocate> allocatedSpaces() {
        return new HashSet<>(allocatedSpaces.values());
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }

    @Override
    public void destroy() {
        if (isDestroyed())
            throw new AllocatorDestroyedException(this);
        if (!allocatedSpaces().isEmpty())
            throw new AllocatorNotEmptyException(this);
        if (destroyWorldConsumer != null)
            destroyWorldConsumer.accept(world);
        TemporaryWorld.deleteTemporaryWorld(world);
        isDestroyed = true;
    }
}

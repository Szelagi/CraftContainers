/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.allocator;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.generator.ChunkGenerator;

import java.util.*;
import java.util.function.Consumer;

public class RegionAllocator implements ISpaceAllocator {
    private static final long TIME_BLOCK = 60_000;
    private final int regionSize;
    private final int spaceSize;
    private final ChunkGenerator generator;
    private final Consumer<World> worldConfig;
    private final Map<Integer, RegionAllocate> allocatedSpaces = new HashMap<>();
    private final Map<Integer, Long> timeLocks = new HashMap<>();
    private World world;

    public RegionAllocator(int regionSize, int spaceSize, ChunkGenerator chunkGenerator, Consumer<World> worldConfig) {
        this.regionSize = regionSize;
        this.spaceSize = spaceSize;
        this.generator = chunkGenerator;
        this.worldConfig = worldConfig;
    }

    public void initialize() {
        if (world != null) throw new IllegalStateException("World has already been initialized");
        var name = "tmp_world_" + UUID.randomUUID().toString().replace("-", "");
        var worldCreator = new WorldCreator(name);
        worldCreator.generator(generator);
        world = worldCreator.createWorld();
        assert world != null;
        TemporaryWorld.markTemporary(world);
        worldConfig.accept(world);
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
        if (world == null) initialize();
        var slot = 0;
        while (isAllocatedSlot(slot) || isLockedSlot(slot))
            slot++;
        var allocate = new RegionAllocate(regionSize, spaceSize, slot, world, this, true);
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
}

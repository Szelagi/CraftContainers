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

public class LazyRegionAllocator implements ISpaceAllocator {
    private final int worldMaxAlloc;
    private final int regionSize;
    private final int spaceGap;
    private final @Nullable Consumer<WorldCreator> worldCreatorConsumer;
    private final @Nullable Consumer<World> worldConsumer;
    private final @Nullable Consumer<World> destroyWorldConsumer;
    private final List<LazyWorld> worlds = new ArrayList<>();
    private final Map<IAllocate, LazyWorld> allocateLazyWorldMap = new HashMap<>();
    private boolean isDestroyed = false;

    public LazyRegionAllocator(int worldMaxAlloc,
                               int regionSize,
                               int spaceGap,
                               @Nullable Consumer<WorldCreator> worldCreatorConsumer,
                               @Nullable Consumer<World> worldConsumer,
                               @Nullable Consumer<World> destroyWorldConsumer) {
        this.worldMaxAlloc = worldMaxAlloc;
        this.regionSize = regionSize;
        this.spaceGap = spaceGap;
        this.worldCreatorConsumer = worldCreatorConsumer;
        this.worldConsumer = worldConsumer;
        this.destroyWorldConsumer = destroyWorldConsumer;
        createWorld();
    }

    private LazyWorld createWorld() {
        var world = TemporaryWorld.createTemporaryWorld(worldCreatorConsumer, worldConsumer);
        var lazyWorld = new LazyWorld(world);
        worlds.add(lazyWorld);
        return lazyWorld;
    }

    private void deleteWorld(LazyWorld lazyWorld) {
        worlds.remove(lazyWorld);
        TemporaryWorld.deleteTemporaryWorld(lazyWorld.world);
    }

    @Override
    public IAllocate allocate() {
        if (isDestroyed()) throw new AllocatorDestroyedException(this);

        var lazyWorld = worlds.getLast();
        if (lazyWorld.idGenerator.peekNext() >= worldMaxAlloc) {
            lazyWorld = createWorld();
        }

        var region = new RegionAllocate(regionSize, spaceGap, (int) lazyWorld.idGenerator.next(), lazyWorld.world, this, false);
        lazyWorld.activeAllocations.add(region);
        allocateLazyWorldMap.put(region, lazyWorld);
        return region;
    }

    @Override
    public void deallocate(IAllocate allocate) {
        var lazyWorld = allocateLazyWorldMap.get(allocate);

        if (!allocate.getAllocator().equals(this) || (!(allocate instanceof RegionAllocate regionAllocate)) || lazyWorld == null)
            throw new IllegalArgumentException("This allocator is not responsible for the provided allocation.");

        lazyWorld.activeAllocations.remove(allocate);
        allocateLazyWorldMap.remove(allocate);

        if (lazyWorld.idGenerator.peekNext() > worldMaxAlloc && lazyWorld.activeAllocations.isEmpty()) {
            deleteWorld(lazyWorld);
        }
    }

    @Override
    public boolean isAllocated(IAllocate allocate) {
        if (!allocate.getAllocator().equals(this) || !(allocate instanceof RegionAllocate regionAllocate))
            return false;
        return allocateLazyWorldMap.containsKey(allocate);
    }

    @Override
    public Set<IAllocate> allocatedSpaces() {
        return new HashSet<>(allocateLazyWorldMap.keySet());
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
        for (var world : new ArrayList<>(worlds)) {
            if (destroyWorldConsumer != null)
                destroyWorldConsumer.accept(world.world);
            deleteWorld(world);
        }
        isDestroyed = true;
    }
}

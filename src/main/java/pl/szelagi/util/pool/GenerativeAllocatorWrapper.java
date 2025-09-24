/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.util.pool;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.allocator.IAllocate;
import pl.szelagi.allocator.ISpaceAllocator;

import java.util.Set;

public class GenerativeAllocatorWrapper implements ISpaceAllocator {
    private final ISpaceAllocator spaceAllocator;

    public GenerativeAllocatorWrapper(ISpaceAllocator spaceAllocator) {
        this.spaceAllocator = spaceAllocator;
    }

    private void preloadChunks(@Nullable Location location) {
        if (location == null || location.getWorld() == null) return;

        var world = location.getWorld();
        int viewDistance = Bukkit.getViewDistance();

        int centerChunkX = location.getBlockX() >> 4;
        int centerChunkZ = location.getBlockZ() >> 4;

        for (int dx = -viewDistance; dx <= viewDistance; dx++) {
            for (int dz = -viewDistance; dz <= viewDistance; dz++) {
                int chunkX = centerChunkX + dx;
                int chunkZ = centerChunkZ + dz;

                world.getChunkAtAsync(chunkX, chunkZ, true)
                        .thenAccept(chunk -> {
                            if (!chunk.isLoaded()) {
                                chunk.load(true);
                            }
                        });
            }
        }
    }

    @Override
    public IAllocate allocate() {
        var space = spaceAllocator.allocate();
        preloadChunks(space.getCenter());
        return space;
    }

    @Override
    public void deallocate(IAllocate allocate) {
        spaceAllocator.deallocate(allocate);
    }

    @Override
    public boolean isAllocated(IAllocate allocate) {
        return spaceAllocator.isAllocated(allocate);
    }

    @Override
    public Set<IAllocate> allocatedSpaces() {
        return spaceAllocator.allocatedSpaces();
    }

    @Override
    public boolean isDestroyed() {
        return spaceAllocator.isDestroyed();
    }

    @Override
    public void destroy() {
        spaceAllocator.destroy();
    }
}

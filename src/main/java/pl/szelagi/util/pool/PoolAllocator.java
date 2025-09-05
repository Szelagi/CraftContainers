/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.util.pool;

import pl.szelagi.allocator.IAllocate;
import pl.szelagi.allocator.ISpaceAllocator;

import java.util.Set;

public class PoolAllocator implements ISpaceAllocator {
    private final ISpaceAllocator allocator;
    private final Pool<IAllocate> pool;
    public PoolAllocator(ISpaceAllocator allocator, Pool<IAllocate> pool) {
        this.allocator = allocator;
        this.pool = pool;
    }

    @Override
    public IAllocate allocate() {
        return pool.acquire();
    }

    @Override
    public void deallocate(IAllocate allocate) {
        allocator.deallocate(allocate);
    }

    @Override
    public boolean isAllocated(IAllocate allocate) {
        return allocator.isAllocated(allocate);
    }

    @Override
    public Set<IAllocate> allocatedSpaces() {
        return allocator.allocatedSpaces();
    }

    @Override
    public void initialize() {
        allocator.initialize();
    }
}

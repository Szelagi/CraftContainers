/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.space;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class SpaceAllocator {
    // todo: dodać system, który atymatycznie zwalnie puste rekordy SpaceAllocate
    private static final long TIME_BLOCK = 30_000;
    private static final int startX = 0;
    private static final int startY = 0;
    private static final HashMap<World, ArrayList<SpaceAllocate>> totalAllocations = new HashMap<>();

    @Deprecated
    private static boolean setTimeLock(@NotNull ArrayList<SpaceAllocate> allocates, int slot, long lockMillis) {
        var spaceAllocate = getSpaceAllocate(allocates, slot);
        if (spaceAllocate == null)
            return false;
        spaceAllocate.setPutOnTimeLock(true);
        spaceAllocate.setLockEndMillis(System.currentTimeMillis() + lockMillis);
        return true;
    }

    private static boolean isTimeBlocked(@NotNull ArrayList<SpaceAllocate> allocates, int slot) {
        var spaceAllocate = getSpaceAllocate(allocates, slot);
        if (spaceAllocate == null)
            return false;
        if (!spaceAllocate.isPutOnTimeLock())
            return false;

        if (System.currentTimeMillis() >= spaceAllocate.getLockEndMillis()) {
            spaceAllocate.setPutOnTimeLock(false);
            return false;
        }
        return true;
    }

    private static boolean isReservedSlot(@NotNull ArrayList<SpaceAllocate> allocates, int slot) {
        var spaceAllocate = getSpaceAllocate(allocates, slot);
        if (spaceAllocate == null)
            return false;
        return spaceAllocate.isAllocate();
    }

    private static boolean isExistsSpace(ArrayList<SpaceAllocate> allocates, int slot) {
        return allocates.stream()
                .anyMatch(spaceAllocate -> spaceAllocate.getSlot() == slot);
    }

    @Nullable
    private static SpaceAllocate getSpaceAllocate(@NotNull World world, int slot) {
        var allocates = totalAllocations.get(world);
        if (allocates == null)
            return null;
        return allocates.stream()
                .filter(sa -> sa.getSlot() == slot)
                .findFirst().orElse(null);
    }

    @Nullable
    private static SpaceAllocate getSpaceAllocate(@NotNull ArrayList<SpaceAllocate> allocates, int slot) {
        return allocates.stream()
                .filter(sa -> sa.getSlot() == slot)
                .findFirst().orElse(null);
    }

    private static boolean deleteAllocate(SpaceAllocate spaceAllocate) {
        var allocates = totalAllocations.get(spaceAllocate.getWorld());
        if (allocates == null)
            return false;
        return allocates.remove(spaceAllocate);
    }

    public static Space allocate(World world) {
        int slot = 0;
        var allocates = totalAllocations.computeIfAbsent(world, k -> new ArrayList<>());
        while (isReservedSlot(allocates, slot) || isTimeBlocked(allocates, slot))
            slot++;

        if (isExistsSpace(allocates, slot)) {
            var spaceAllocate = getSpaceAllocate(allocates, slot);
            assert spaceAllocate != null;
            deleteAllocate(spaceAllocate);
        }

        SpaceAllocate spaceAllocate = new SpaceAllocate(slot, world);
        allocates.add(spaceAllocate);
        return spaceAllocate;
    }

    public static void deallocate(Space space) {
        var allocates = totalAllocations.get(space.getWorld());
        if (allocates == null)
            throw new DeallocateException("allocates ArrayList is not initialized", space);
        if (allocates.isEmpty())
            throw new DeallocateException("allocates ArrayList is empty", space);
        var spaceAllocate = allocates.stream()
                .filter(sa -> sa.getSlot() == space.getSlot())
                .findFirst()
                .orElse(null);
        if (spaceAllocate == null)
            throw new DeallocateException("allocates ArrayList do not exists element", space);
        spaceAllocate.setAllocate(false);
        spaceAllocate.setPutOnTimeLock(true);
        spaceAllocate.setLockEndMillis(System.currentTimeMillis() + TIME_BLOCK);
    }
}

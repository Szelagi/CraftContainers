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
import pl.szelagi.ConfigManager;
import pl.szelagi.SessionAPI;
import pl.szelagi.spatial.ISpatial;

public class Space implements ISpatial {
    public static final int SPACE_SIZE = ConfigManager.config().maxBoardSize;
    public static final int AREA_SIZE = ConfigManager.config().distanceBetweenMaps;
    public static final int TOTAL_SIZE = 2 * AREA_SIZE + SPACE_SIZE;
    private final Location startPoint;
    private final Location endPoint;
    private final Location startAreaPoint;
    private final Location endAreaPoint;
    private final Location centerPoint;
    private final World world;
    private final int slot;

    protected Space(int slot, @NotNull World world) {
        final int worldMaxHeight = world.getMaxHeight();
        final int worldMinHeight = world.getMinHeight();

        this.slot = slot;
        this.world = world;
        int startPoint = slot * TOTAL_SIZE;
        int endPoint = (slot + 1) * TOTAL_SIZE - 1;
        int startSpace = startPoint + AREA_SIZE;
        int endSpace = endPoint - AREA_SIZE;
        this.startAreaPoint = new Location(world, startPoint, worldMinHeight, 0);
        this.endAreaPoint = new Location(world, endPoint, worldMaxHeight, TOTAL_SIZE - 1);
        this.startPoint = new Location(world, startSpace, worldMinHeight, AREA_SIZE);
        this.endPoint = new Location(world, endSpace, worldMaxHeight, TOTAL_SIZE - AREA_SIZE - 1);

        this.centerPoint = ISpatial.super.getCenter();
    }

    public int getSlot() {
        return slot;
    }

    public Location getStartPoint() {
        return startPoint;
    }

    public Location getEndPoint() {
        return endPoint;
    }

    public Location getStartAreaPoint() {
        return startAreaPoint;
    }

    public Location getEndAreaPoint() {
        return endAreaPoint;
    }

    public Location getCenter() {
        return centerPoint;
    }

    public Location getCenterClone() {
        return centerPoint.clone();
    }

    public Location getStartPointClone() {
        return startPoint.clone();
    }

    public Location getEndPointClone() {
        return endPoint.clone();
    }

    public Location getStartAreaPointClone() {
        return startAreaPoint.clone();
    }

    public Location getEndAreaPointClone() {
        return endAreaPoint.clone();
    }

    public double getCenterY() {
        return getCenter().getY();
    }

    public int getCenterBlockY() {
        return getCenter().getBlockY();
    }

    public World getWorld() {
        return world;
    }

    @Override
    public @NotNull Location getFirstPoint() {
        return getStartPoint();
    }

    @Override
    public @NotNull Location getSecondPoint() {
        return getEndPoint();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Space space = (Space) o;

        if (slot != space.slot)
            return false;
        return world.equals(space.world);
    }

    @Override
    public int hashCode() {
        int result = world.hashCode();
        result = 31 * result + slot;
        return result;
    }

    @Override
    public String toString() {
        return "Space{" + "world=" + world.getName() + ", slot=" + slot + '}';
    }
}

/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.relative;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class RelativeLocation extends Location {
    private double xRelative;
    private double yRelative;
    private double zRelative;

    public RelativeLocation(double xMoveRelative, double yMoveRelative, double zMoveRelative, Location base) {
        super(base.getWorld(), base.getX() + xMoveRelative, base.getY() + yMoveRelative, base.getZ() + zMoveRelative);
        this.xRelative = xMoveRelative;
        this.yRelative = yMoveRelative;
        this.zRelative = zMoveRelative;
    }

    public RelativeLocation(Location location, Location base) {
        super(base.getWorld(), location.getX(), location.getY(), location.getZ());
        rebase(base);
    }

    public RelativeLocation(Location location, double xRelative, double yRelative, double zRelative) {
        super(location.getWorld(), location.getX(), location.getY(), location.getZ());
        this.xRelative = xRelative;
        this.yRelative = yRelative;
        this.zRelative = zRelative;
    }

    public void rebase(Location base) {
        xRelative = getX() - base.getX();
        yRelative = getY() - base.getY();
        zRelative = getZ() - base.getZ();
    }

    public boolean equalsRelative(RelativeLocation location) {
        if (this == location)
            return true;
        if (xRelative != location.getXRelative())
            return false;
        if (yRelative != location.getYRelative())
            return false;
        return zRelative == location.getZRelative();
    }

    public boolean equalsRelative(RelativePoint point) {
        if (xRelative != point.getXRelative())
            return false;
        if (yRelative != point.getYRelative())
            return false;
        return zRelative == point.getZRelative();
    }

    public double getXRelative() {
        return xRelative;
    }

    public double getYRelative() {
        return yRelative;
    }

    public double getZRelative() {
        return zRelative;
    }

    public RelativePoint toRelativePoint() {
        return new RelativePoint(xRelative, yRelative, zRelative);
    }

    @Override
    public @NotNull RelativeLocation clone() {
        return new RelativeLocation(this, xRelative, yRelative, zRelative);
    }
}

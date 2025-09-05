/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.relative;

import org.bukkit.Location;

import java.io.Serializable;

@Deprecated
public class RelativePoint implements Serializable {
    private final double xRelative;
    private final double yRelative;
    private final double zRelative;

    public RelativePoint(double xRelative, double yRelative, double zRelative) {
        this.xRelative = xRelative;
        this.yRelative = yRelative;
        this.zRelative = zRelative;
    }

    public Location toLocation(Location base) {
        Location out = base.clone();
        return out.add(xRelative, yRelative, zRelative);
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

    public boolean equalsRelative(RelativePoint point) {
        if (this == point)
            return true;
        if (xRelative != point.getXRelative())
            return false;
        if (yRelative != point.getYRelative())
            return false;
        return zRelative == point.getZRelative();
    }

    public boolean equalsRelative(RelativeLocation location) {
        if (xRelative != location.getXRelative())
            return false;
        if (yRelative != location.getYRelative())
            return false;
        return zRelative == location.getZRelative();
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(xRelative);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(yRelative);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(zRelative);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public RelativeLocation toRelativeLocation(Location base) {
        return new RelativeLocation(xRelative, yRelative, zRelative, base);
    }
}

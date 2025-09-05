/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.transform;

import org.bukkit.Location;
import org.joml.Vector3i;


public interface ITransformable<T extends ITransformable<T>> {
    Location getBase();

    default T translateAbsoluteTo(Location location) {
        if (!location.getWorld().getName().equals(getBase().getWorld().getName()))
            throw new IllegalArgumentException("Cannot translate to a other world location");

        var delta = location.toVector().toVector3i().sub(getBase().toVector().toVector3i());
        return translateAbsolute(delta.x(), delta.y(), delta.z());
    }
    /**
     * Translates the object along a given direction, including the reference point.
     */
    T translateAbsolute(int dx, int dy, int dz);
    default T translateAbsolute(int distance, Direction move) {
        var delta = deltaVector(distance, move);
        return translateAbsolute(delta.x(), delta.y(), delta.z());
    }

    /**
     * Rotates the object around a specified axis by a given angle.
     */
    T rotate(Degree angle, Rotation direction, RotAxis axis);

    private Vector3i deltaVector(int distance, Direction move) {
        var deltaX = distance * move.getDx();
        var deltaY = distance * move.getDy();
        var deltaZ = distance * move.getDz();
        return new Vector3i(deltaX, deltaY, deltaZ);
    }
}

/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.util;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Draw {
    public static List<Vector3f> createLinePoints(Vector3f start, Vector3f end, double distanceBetweenPoints) {
        var points = new ArrayList<Vector3f>();

        // Wektor różnicy
        var dx = end.x() - start.x();
        var dy = end.y() - start.y();
        var dz = end.z() - start.z();

        // Długość całej linii
        var totalDistance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        // Liczba segmentów (zawsze minimum 1)
        int segments = Math.max(1, (int) Math.round(totalDistance / distanceBetweenPoints));

        for (int i = 0; i <= segments; i++) {
            var t = (float) i / segments;
            var x = (float) (start.x() + t * dx);
            var y = (float) (start.y() + t * dy);
            var z = (float) (start.z() + t * dz);
            points.add(new Vector3f(x, y, z));
        }

        return points;
    }
}

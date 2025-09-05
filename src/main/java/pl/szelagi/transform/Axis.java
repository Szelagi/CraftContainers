/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.transform;

import com.sk89q.worldedit.math.transform.AffineTransform;

public enum Axis {
    X, Y, Z;

    public AffineTransform flipTransform() {
        var transform = new AffineTransform();
        return switch (this) {
            case X -> transform = transform.scale(-1, 1, 1);
            case Y -> transform = transform.scale(1, -1, 1);
            case Z -> transform = transform.scale(1, 1, -1);
        };
    }
}

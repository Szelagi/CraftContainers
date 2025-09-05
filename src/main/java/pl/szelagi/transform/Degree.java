/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.transform;

/**
 * Rotation angles in 90-degree increments.
 * <p>
 * Only multiples of 90 are supported to prevent block overlap or disappearance
 * in a discrete coordinate system.
 */
public enum Degree {
    DEG_90(90),
    DEG_180(180),
    DEG_270(270);

    private final int degree;

    Degree(int degree) {
        this.degree = degree;
    }

    public int getDegree() {
        return degree;
    }
}

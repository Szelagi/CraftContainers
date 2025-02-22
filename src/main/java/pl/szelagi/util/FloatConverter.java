/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.util;

public class FloatConverter {
    public static String floatingFormat(float n) {
        return String.format("%.02f", n);
    }

    public static String floatingFloorFormat(float n) {
        return String.format("%.0f", Math.floor(n));
    }

    public static String floatingCeilFormat(float n) {
        return String.format("%.0f", Math.ceil(n));
    }
}

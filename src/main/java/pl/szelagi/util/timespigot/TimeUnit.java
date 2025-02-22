/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.util.timespigot;

public enum TimeUnit {
    // Base unit = MILLIS
    SECONDS(1000), MILLIS(1), TICKS(50);
    private final int multiple;

    TimeUnit(int multiple) {
        this.multiple = multiple;
    }

    public int getMultiple() {
        return multiple;
    }
}

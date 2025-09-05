/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.util.timespigot;

/**
 * Represents units of time and their conversion factor to milliseconds.
 */
public enum TimeUnit {
    /** One second = 1000 milliseconds */
    SECONDS(1000),

    /** Base unit: one millisecond */
    MILLIS(1),

    /** One tick = 50 milliseconds */
    TICKS(50);

    private final int multiple;

    /**
     * Constructs a time unit with its conversion factor to milliseconds.
     *
     * @param multiple the number of milliseconds in this unit
     */
    TimeUnit(int multiple) {
        this.multiple = multiple;
    }

    /**
     * Returns the number of milliseconds represented by this unit.
     *
     * @return conversion factor to milliseconds
     */
    public int getMultiple() {
        return multiple;
    }
}

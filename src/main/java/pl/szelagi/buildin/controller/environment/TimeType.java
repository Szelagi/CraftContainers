/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller.environment;

public enum  TimeType {
    BEGINNING(0), DAY(1000), NOON(6000), SUNSET(12000), NIGHT(13000), MIDNIGHT(18000), SUNRISE(23000);
    private final long time;

    TimeType(long time) {
        this.time = time;
    }

    public long getTicks() {
        return time;
    }
}

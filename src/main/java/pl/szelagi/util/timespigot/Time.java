/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.util.timespigot;

import pl.szelagi.util.FloatConverter;

public class Time {
    private final TimeUnit unit;
    private final long span;

    public Time(long span, TimeUnit unit) {
        this.span = span;
        this.unit = unit;
    }

    public static Time zero() {
        return new Time(0, TimeUnit.TICKS);
    }

    public static Time seconds(int span) {
        return new Time(span, TimeUnit.SECONDS);
    }

    public static Time ticks(int span) {
        return new Time(span, TimeUnit.TICKS);
    }

    public static Time millis(long span) {
        return new Time(span, TimeUnit.MILLIS);
    }

    public long toMillis() {
        return span * unit.getMultiple();
    }

    public int toTicks() {
        return (int) (toMillis() / TimeUnit.TICKS.getMultiple());
    }

    public int toSeconds() {
        return (int) (toMillis() / TimeUnit.SECONDS.getMultiple());
    }

    public float toFloatSeconds() {
        return toMillis() / (float) TimeUnit.SECONDS.getMultiple();
    }

    public String toVisualCeilSeconds() {
        return FloatConverter.floatingCeilFormat(toFloatSeconds());
    }

    public String toVisualFloatSeconds() {
        return FloatConverter.floatingFormat(toFloatSeconds());
    }
}
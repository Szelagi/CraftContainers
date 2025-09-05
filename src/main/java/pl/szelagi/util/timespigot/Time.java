/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.util.timespigot;

/**
 * Represents a time span with a specific {@link TimeUnit}.
 * Provides convenient factory methods and conversions to different units.
 */
public class Time {
    private static final String FLOAT_FORMAT = "%.02f";
    private final TimeUnit unit;
    private final long span;

    /**
     * Creates a new time span with the given length and unit.
     *
     * @param span the amount of time
     * @param unit the unit of time
     */
    public Time(long span, TimeUnit unit) {
        this.span = span;
        this.unit = unit;
    }

    /** Returns a zero-length time span (0 ticks). */
    public static Time zero() {
        return new Time(0, TimeUnit.TICKS);
    }

    /** Creates a time span in seconds. */
    public static Time seconds(int span) {
        return new Time(span, TimeUnit.SECONDS);
    }

    /** Creates a time span in ticks. */
    public static Time ticks(int span) {
        return new Time(span, TimeUnit.TICKS);
    }

    /** Creates a time span in milliseconds. */
    public static Time millis(long span) {
        return new Time(span, TimeUnit.MILLIS);
    }

    /** Converts this time span to milliseconds. */
    public long toMillis() {
        return span * unit.getMultiple();
    }

    /** Converts this time span to ticks. */
    public int toTicks() {
        return (int) (toMillis() / TimeUnit.TICKS.getMultiple());
    }

    /** Converts this time span to seconds (integer). */
    public int toSeconds() {
        return (int) (toMillis() / TimeUnit.SECONDS.getMultiple());
    }

    /** Converts this time span to seconds (float). */
    public float toFloatSeconds() {
        return toMillis() / (float) TimeUnit.SECONDS.getMultiple();
    }

    /** Returns the ceiling of seconds as a string for display purposes. */
    public String toVisualCeilSeconds() {
        var ceilSeconds = (int) Math.ceil(toFloatSeconds());
        return Integer.toString(ceilSeconds);
    }

    /** Returns the seconds as a formatted float string for display purposes. */
    public String toVisualFloatSeconds() {
        return String.format(FLOAT_FORMAT, toFloatSeconds());
    }
}
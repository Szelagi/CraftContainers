/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.spatial;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class Spatial implements ISpatial {
    private final Location first;
    private final Location second;

    public Spatial(Location first, Location second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public @NotNull Location getMin() {
        return first;
    }

    @Override
    public @NotNull Location getMax() {
        return second;
    }

    @Override
    public String toString() {
        return "Spatial{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}

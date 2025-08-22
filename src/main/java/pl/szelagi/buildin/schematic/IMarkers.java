/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.schematic;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IMarkers {
    @NotNull List<Marker> getMarkers();
    @NotNull Marker create(String name, Location location);
    @Nullable Marker getById(int id);
    @Nullable List<Marker> getByName(String name);
    @NotNull List<Marker> getNearbyMarkers(Location location, double radius);
    @NotNull List<Marker> removeNearbyMarkers(Location location, double radius);
    @Nullable Marker removeById(int id);
    @Nullable List<Marker> removeByName(String name);
    @NotNull List<Marker> drop();
}

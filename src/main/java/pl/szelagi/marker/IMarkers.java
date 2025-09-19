/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.marker;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.SessionAPI;
import pl.szelagi.transform.ITransformable;

import java.io.File;
import java.util.List;

public interface IMarkers<T extends IMarkers<T>> extends ITransformable<T> {
    String SEPARATOR_NAME = "#";
    String EXTENSION_NAME = "mrks";
    String DIR_NAME = "blueprint";

    @NotNull Location getBase();
    @NotNull List<Marker> getMarkers();
    @NotNull Marker create(String name, Location location);
    @Nullable Marker getById(int id);
    @NotNull List<Marker> getByName(String name);
    @NotNull List<Marker> getNearbyMarkers(Location location, double radius);
    @NotNull List<Marker> removeNearbyMarkers(Location location, double radius);
    @Nullable Marker removeById(int id);
    @Nullable List<Marker> removeByName(String name);
    @NotNull List<Marker> drop();

    default @NotNull List<Marker> requireAnyByName(String name) {
        var markers = getByName(name);
        if (markers.isEmpty())
            throw new MarkerException("No markers found with name: " + name);
        return markers;
    }

    default @NotNull Marker requireOneByName(String name) {
        var markers = requireAnyByName(name);
        if (markers.size() > 1)
            throw new MarkerException("Expected exactly one marker with name: " + name + ", but found " + markers.size());
        return markers.getFirst();
    }

    default @NotNull Location requireOneLocationByName(String name) {
        return requireOneByName(name).getLocation();
    }

    default @NotNull List<Location> requireAnyLocationsByName(String name) {
        return requireAnyByName(name).stream().map(Marker::getLocation).toList();
    }

    void save(@NotNull File file, @NotNull Location base);

    static File getDataFolder() {
        return new File(SessionAPI.instance().getDataFolder(), DIR_NAME);
    }

    static File getFile(String name) {
        var dataPath = SessionAPI.instance().getDataFolder().toPath();
        var fileName = name + "." + EXTENSION_NAME;
        return dataPath.resolve(DIR_NAME).resolve(fileName).toFile();
    }

    static File getFile(NamespacedKey namespace) {
        var name = namespace.getNamespace() + SEPARATOR_NAME + namespace.getKey();
        return getFile(name);
    }

    static File getFile(Plugin plugin, String key) {
        return getFile(new NamespacedKey(plugin, key));
    }
}

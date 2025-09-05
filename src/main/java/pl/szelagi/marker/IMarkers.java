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
    @Nullable List<Marker> getByName(String name);
    @NotNull List<Marker> getNearbyMarkers(Location location, double radius);
    @NotNull List<Marker> removeNearbyMarkers(Location location, double radius);
    @Nullable Marker removeById(int id);
    @Nullable List<Marker> removeByName(String name);
    @NotNull List<Marker> drop();

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

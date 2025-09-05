/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.fawe;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import pl.szelagi.SessionAPI;
import pl.szelagi.spatial.ISpatial;
import pl.szelagi.transform.ITransformable;
import pl.szelagi.util.Cloneable;

import java.io.File;

public interface ISchematic<T extends ISchematic<T>> extends ISpatial, ITransformable<T>, Cloneable<T> {
    String SEPARATOR_NAME = "#";
    String EXTENSION_NAME = "schem";
    String DIR_NAME = "blueprint";

    Location getOrigin();
    void load();
    void clean();

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

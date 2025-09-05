/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * A registry for mapping plugin name to their corresponding {@link JavaPlugin} instances.
 */
public class PluginRegistry {
    /**
     * Internal map storing plugin name and their associated JavaPlugin instances.
     */
    private final static HashMap<String, JavaPlugin> REGISTRY = new HashMap<>();

    /**
     * Updates the registry by scanning all loaded plugins from the Bukkit server
     * and mapping their JAR file names to their {@link JavaPlugin} instances.
     */
    private static void updateRegistry() {
        Plugin[] plugins = Bukkit.getServer().getPluginManager().getPlugins();
        for (Plugin plugin : plugins) {
            if (!(plugin instanceof JavaPlugin javaPlugin))
                continue;
            try {
                Method getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
                getFileMethod.setAccessible(true);
                File file = (File) getFileMethod.invoke(javaPlugin);
                REGISTRY.put(file.getName(), javaPlugin);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Retrieves a plugin instance based on its plugin name.
     * @param pluginName the file name of the plugin's JAR file
     * @return the corresponding {@link JavaPlugin} instance, or {@code null} if not found
     */
    public static @Nullable JavaPlugin getPlugin(String pluginName) {
        var plugin = REGISTRY.get(pluginName);
        if (plugin == null) {
            updateRegistry();
            return REGISTRY.get(pluginName);
        }
        return plugin;
    }
}

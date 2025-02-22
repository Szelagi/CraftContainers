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
import java.util.HashMap;

public class PluginRegistry {
    private final static HashMap<String, JavaPlugin> REGISTRY = new HashMap<>();

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

    public static @Nullable JavaPlugin getPlugin(String pluginFileName) {
        var plugin = REGISTRY.get(pluginFileName);
        if (plugin == null) {
            updateRegistry();
            plugin = REGISTRY.get(pluginFileName);
        }
        return plugin;
    }
}

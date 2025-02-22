/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pl.szelagi.command.Command;
import pl.szelagi.manager.BoardManager;
import pl.szelagi.manager.ControllerManager;
import pl.szelagi.manager.SessionManager;
import pl.szelagi.manager.VersionManager;
import pl.szelagi.recovery.RecoveryManager;
import pl.szelagi.world.SessionWorldManager;

import java.io.File;

public final class SessionAPI extends JavaPlugin {
    FileConfiguration config = getConfig();
    public static final File SESSION_API_DIRECTORY = new File(Bukkit
            .getServer()
            .getPluginsFolder()
            .getPath() + "/SessionAPI");
    public static final File RECOVERY_DIRECTORY = new File(SESSION_API_DIRECTORY.getPath() + "/recovery");
    public static final File BOARD_DIRECTORY = new File(SESSION_API_DIRECTORY.getPath() + "/board");
    private static SessionAPI instance;

    @Deprecated
    public static SessionAPI getInstance() {
        return instance;
    }

    public static SessionAPI instance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic
        if (!SESSION_API_DIRECTORY.exists())
            SESSION_API_DIRECTORY.mkdir();
        if (!RECOVERY_DIRECTORY.exists())
            RECOVERY_DIRECTORY.mkdir();
        if (!BOARD_DIRECTORY.exists())
            BOARD_DIRECTORY.mkdir();

        createConfig();

        VersionManager.initialize();
        SessionManager.initialize(this);
        BoardManager.initialize(this);
        ControllerManager.initialize(this);
        RecoveryManager.initialize(this);
        SessionWorldManager.initialize(this);
        Command.registerCommands();
    }

    private void createConfig() {
        config.addDefault("max-board-size", 300);
        config.addDefault("distance-between-maps", 500);
        config.addDefault("minecraft_version", "auto");
        config.options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public FileConfiguration config() {
        return config;
    }
}

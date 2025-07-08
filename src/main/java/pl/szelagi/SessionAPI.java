/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pl.szelagi.command.Command;
import pl.szelagi.manager.BoardManager;
import pl.szelagi.manager.ControllerManager;
import pl.szelagi.manager.SessionManager;
import pl.szelagi.manager.VersionManager;
import pl.szelagi.recovery.RecoveryManager;
import pl.szelagi.test.Tests;
import pl.szelagi.util.CooldownVolatile;
import pl.szelagi.util.Debug;
import pl.szelagi.world.SessionWorldManager;

import java.io.File;

public class SessionAPI extends JavaPlugin {
    public static final String RECOVERY_DIRNAME = "recovery";
    public static final String BOARD_DIRNAME = "board";

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
        var directory = getDataFolder();
        if (!directory.exists()) directory.mkdir();

        var recoveryDir = new File(directory, RECOVERY_DIRNAME);
        if (!recoveryDir.exists()) recoveryDir.mkdir();

        var boardDir = new File(directory, BOARD_DIRNAME);
        if (!boardDir.exists()) boardDir.mkdir();

        ConfigManager.init(this);

        var debugOnStart = ConfigManager.config().debugOnStart;
        if (debugOnStart)
            Debug.enable(true);

        CooldownVolatile.initialize(this);
        VersionManager.initialize();
        SessionManager.initialize(this);
        BoardManager.initialize(this);
        ControllerManager.initialize(this);
        RecoveryManager.initialize(this);
        SessionWorldManager.initialize(this);
        Command.registerCommands();

        Tests.loadAll();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public FileConfiguration config() {
        return ConfigManager.config().fileConfiguration;
    }
}

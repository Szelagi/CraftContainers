/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi;

import org.bukkit.plugin.java.JavaPlugin;
import pl.szelagi.command.Command;
import pl.szelagi.minecraftVersion.MinecraftVersion;
import pl.szelagi.recovery.RecoveryManager;
import pl.szelagi.allocator.Allocators;
import pl.szelagi.allocator.TemporaryWorld;
import pl.szelagi.util.CooldownVolatile;
import pl.szelagi.util.Debug;

import java.io.File;

public class CraftContainers extends JavaPlugin {
    public static final String RECOVERY_DIRNAME = "recovery";
    public static final String BLUEPRINT_DIRNAME = "blueprint";
    private static CraftContainers instance;
    private static Config config;
    public static CraftContainers instance() {
        return instance;
    }
    public static Config config() {
        return config;
    }

    @Override
    public void onEnable() {
        var directory = getDataFolder();
        directory.mkdirs();

        instance = this;
        config = new Config(this);

        var recoveryDir = new File(directory, RECOVERY_DIRNAME);
        if (!recoveryDir.exists()) recoveryDir.mkdirs();

        var boardDir = new File(directory, BLUEPRINT_DIRNAME);
        if (!boardDir.exists()) boardDir.mkdirs();

        var debugOnStart = CraftContainers.config().debugOnStart;
        if (debugOnStart)
            Debug.enable(true);

        getServer().getPluginManager().registerEvents(new ContainerWatcher(), this);

        TemporaryWorld.clean();
        CooldownVolatile.initialize(this);
        MinecraftVersion.initialize();
        RecoveryManager.initialize(this);
        Allocators.initialize();
        Command.registerCommands();
    }

    @Override
    public void onDisable() {
        TemporaryWorld.clean();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        config = new Config(this);
    }
}

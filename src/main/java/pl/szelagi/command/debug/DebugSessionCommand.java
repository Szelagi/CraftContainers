/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.command.debug;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.util.Debug;

import static pl.szelagi.command.CommandHelper.PREFIX;

public class DebugSessionCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (Debug.enable()) {
            Debug.enable(false);
            sender.sendMessage(PREFIX + "§aDebug mode has been §coff§a.");
        } else {
            Debug.enable(true);
            sender.sendMessage(PREFIX + "§aDebug mode has been §aon§a.");
            sender.sendMessage(PREFIX + "From now on, debug messages will be sent to the console and saved in the debug.log file.");
        }
        return true;
    }
}

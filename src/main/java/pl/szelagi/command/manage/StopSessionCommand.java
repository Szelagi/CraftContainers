/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.command.manage;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.command.CommandHelper;

import java.util.List;

import static pl.szelagi.command.CommandHelper.PREFIX;

public class StopSessionCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player)) return false;

        if (strings.length != 1) {
            player.sendMessage(PREFIX + "§cInvalid pattern! Usage: /session-stop <session-name>:<session-id>");
            return false;
        }

        var session = CommandHelper.selectSession(commandSender, strings[0]);
        if (session == null) return false;

        session.stop();
        player.sendMessage(PREFIX + "§aSession stopped successfully: §f" + strings[0]);
        return true;

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return strings.length == 1 ? CommandHelper.sessionsComplete(commandSender) : null;
    }
}

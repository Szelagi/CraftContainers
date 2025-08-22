/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static pl.szelagi.command.CommandHelper.PREFIX;

public abstract class CommandRouter implements CommandExecutor, TabCompleter {
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    protected void register(SubCommand subCommand) {
        subCommands.put(subCommand.getName(), subCommand);
    }

    public abstract String getDescription();

    public boolean preprocess(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return true;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!preprocess(sender, command, label, args)) return true;

        if (args.length == 0) {
            sender.sendMessage(PREFIX + "§6Usage: /" + label + " <subcommand>");
            sender.sendMessage("§7 " + getDescription());
            sender.sendMessage("");
            sender.sendMessage("§6Subcommands:");

            subCommands.values().forEach(subCommand -> {
                var message = "§e" + subCommand.getName() + " " + subCommand.getUsage() + " §8- §7" + subCommand.getDescription();
                sender.sendMessage(message);
            });
            return true;
        }

        var sub = subCommands.get(args[0].toLowerCase());
        if (sub == null) {
            sender.sendMessage(PREFIX + "§cUnknown subcommand: " + args[0]);
            return true;
        }

        var subArgs = Arrays.copyOfRange(args, 1, args.length);
        sub.execute(sender, subArgs);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String cmd, @NotNull String[] args) {
        if (args.length == 1) {
            return subCommands.keySet().stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        var sub = subCommands.get(args[0].toLowerCase());
        if (sub != null) {
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            return sub.tabComplete(sender, subArgs);
        }

        return List.of();
    }
}

/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.command;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.component.container.Container;
import pl.szelagi.manager.ContainerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandHelper {
    public static final String PREFIX = "§6[§eCraftContainers§6] §r";
    public static final String NEUTRAL_COLOR = "§f";
    public static final String SUCCESS_COLOR = "§a";
    public static final String ERROR_COLOR = "§c";

    public static @Nullable ImmutablePair<Container, Player> extractContainerAndPlayer(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(PREFIX + ERROR_COLOR + "Invalid arguments. Expected: <container> <player>");
            return null;
        }

        var container = selectContainer(sender, args[0]);
        if (container == null) {
            sender.sendMessage(PREFIX + ERROR_COLOR + "No container found with the given ID.");
            return null;
        }

        var player = selectPlayer(sender, args[1]);
        if (player == null) {
            sender.sendMessage(PREFIX + ERROR_COLOR + "No player found with the given name.");
            return null;
        }

        return new ImmutablePair<>(container, player);
    }

    public static @Nullable Container extractContainer(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(PREFIX + ERROR_COLOR + "Invalid arguments. Expected: <container>");
            return null;
        }
        return selectContainer(sender, args[0]);
    }

    public static @Nullable List<String> containerAndPlayerTabComplete(@NotNull CommandSender commandSender, @NotNull String[] strings) {
        final var argCount = strings.length;

        return switch (argCount) {
            case 1 -> CommandHelper.sessionsComplete(commandSender);
            case 2 -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            default -> null;
        };

    }

    public static @Nullable Container sessionByNameId(String nameId) {
        return ContainerManager
                .containers().stream()
                .filter(loopSession -> {
                    var name = loopSession.identifier();
                    return nameId.equals(name);
                }).findFirst()
                .orElse(null);
    }

    public static List<String> sessionsComplete(CommandSender commandSender) {
        var sessions = ContainerManager.containers().stream().map(session -> session.name() + ":" + session.id()).collect(Collectors.toCollection(ArrayList::new));

        if (commandSender instanceof Player player) {
            var session = ContainerManager.container(player);
            if (session != null) {
                sessions.addFirst("current");
            }
        }

        return sessions;
    }

    public static @Nullable Container selectContainer(CommandSender commandSender, String sessionString) {

        Container container;
        if (sessionString.equals("current")) {

            if (!(commandSender instanceof Player player)) {
                commandSender.sendMessage(PREFIX + "§cOnly players can use the 'current' session identifier!");
                return null;
            }

            container = ContainerManager.container(player);
            if (container == null) {
                commandSender.sendMessage(PREFIX + "§cYou are not currently in any session!");
                return null;
            }
            return container;
        }

        container = CommandHelper.sessionByNameId(sessionString);
        if (container == null) {
            commandSender.sendMessage(PREFIX + "§cNo session found with the identifier: '" + sessionString + "'!");
            return null;
        }
        return container;
    }

    public static @Nullable Player selectPlayer(CommandSender commandSender, String playerString) {
        Player player = Bukkit.getPlayer(playerString);
        if (player == null) {
            commandSender.sendMessage(PREFIX + "§cPlayer not found with name: '" + playerString + "'!");
        }
        return player;
    }

    public static @Nullable Player selectSenderPlayer(CommandSender sender) {
        if (sender instanceof Player player) return player;

        sender.sendMessage(PREFIX + ERROR_COLOR +
                "This command can only be used by players.");
        return null;
    }

    public static @Nullable Container selectPlayerContainer(Player player) {
        var container = Container.getForPlayer(player);
        if (container == null) {
            player.sendMessage(PREFIX + "§cYou are not currently in any container.");
        }
        return container;
    }
}
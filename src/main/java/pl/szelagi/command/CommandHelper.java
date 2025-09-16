/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.component.container.Container;
import pl.szelagi.manager.ContainerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandHelper {
    public static final String PREFIX = "§6[§eSessionAPI§6] §r";

    public static @Nullable Container sessionByNameId(String nameId) {
        return ContainerManager
                .containers().stream()
                .filter(loopSession -> {
                    var name = loopSession.name() + ":" + loopSession.id();
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

    public static @Nullable Container selectSession(CommandSender commandSender, String sessionString) {

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
}
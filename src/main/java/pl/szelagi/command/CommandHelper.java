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
import pl.szelagi.component.session.Session;
import pl.szelagi.manager.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandHelper {
    public static final String PREFIX = "§6[§eSessionAPI§6] §r";

    public static @Nullable Session sessionByNameId(String nameId) {
        return SessionManager
                .sessions().stream()
                .filter(loopSession -> {
                    var name = loopSession.name() + ":" + loopSession.id();
                    return nameId.equals(name);
                }).findFirst()
                .orElse(null);
    }

    public static List<String> sessionsComplete(CommandSender commandSender) {
        var sessions = SessionManager.sessions().stream().map(session -> session.name() + ":" + session.id()).collect(Collectors.toCollection(ArrayList::new));

        if (commandSender instanceof Player player) {
            var session = SessionManager.session(player);
            if (session != null) {
                sessions.addFirst("current");
            }
        }

        return sessions;
    }

    public static @Nullable Session selectSession(CommandSender commandSender, String sessionString) {

        Session session;
        if (sessionString.equals("current")) {

            if (!(commandSender instanceof Player player)) {
                commandSender.sendMessage(PREFIX + "§cOnly players can use the 'current' session identifier!");
                return null;
            }

            session = SessionManager.session(player);
            if (session == null) {
                commandSender.sendMessage(PREFIX + "§cYou are not currently in any session!");
                return null;
            }
            return session;
        }

        session = CommandHelper.sessionByNameId(sessionString);
        if (session == null) {
            commandSender.sendMessage(PREFIX + "§cNo session found with the identifier: '" + sessionString + "'!");
            return null;
        }
        return session;
    }

    public static @Nullable Player selectPlayer(CommandSender commandSender, String playerString) {
        Player player = Bukkit.getPlayer(playerString);
        if (player == null) {
            commandSender.sendMessage(PREFIX + "§cPlayer not found with name: '" + playerString + "'!");
        }
        return player;
    }
}
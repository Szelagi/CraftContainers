package pl.szelagi.command.manage;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.manager.SessionManager;

import static pl.szelagi.command.CommandHelper.PREFIX;

public class LeaveSessionCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player)) return false;

        var session = SessionManager.getSession(player);
        if (session == null) {
            player.sendMessage(PREFIX + "§cYou are not currently in any session.");
            return false;
        }

        if (strings.length != 0) {
            player.sendMessage(PREFIX + "§cInvalid usage. Pattern: /session-leave");
            return false;
        }

        session.removePlayer(player);
        player.sendMessage(PREFIX + "§aYou have successfully left the session.");
        return true;
    }
}

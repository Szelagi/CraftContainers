/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.command.editor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.buildin.creator.Creator;
import pl.szelagi.manager.SessionManager;

import static pl.szelagi.command.CommandHelper.PREFIX;

@Deprecated
public class ExitBoardCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player)) return false;

        var session = SessionManager.session(player);
        if (session == null) {
            player.sendMessage(PREFIX + "§cYou are not currently in a session.");
            return false;
        }
        if (!(session instanceof Creator creator)) {
            player.sendMessage(PREFIX + "§cYou are not in the editor.");
            return false;
        }
        if (creator.isRecording()) {
            player.sendMessage(PREFIX + "§cYou cannot exit while the map is being saved.");
            return false;
        }
        creator.stop();
        player.sendMessage(PREFIX + "§aYou have successfully exited the board editor.");
        return true;
    }
}

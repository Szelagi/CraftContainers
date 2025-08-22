/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.command.marker;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.buildin.editor.BlueprintBoard;
import pl.szelagi.buildin.editor.BlueprintSession;
import pl.szelagi.manager.SessionManager;

import static pl.szelagi.command.CommandHelper.PREFIX;

public class MarkerSubCommand {
    public @Nullable BlueprintBoard getBlueprintBoard(CommandSender sender) {
        if (!(sender instanceof Player player)) return null;

        var session = SessionManager.session(player);
        if (!(session instanceof BlueprintSession blueprintSession)) {
            player.sendMessage(PREFIX + "§cYou are not in a blueprint editor.");
            return null;
        }

        var board = blueprintSession.board();
        if (!(board instanceof BlueprintBoard blueprintBoard)) {
            player.sendMessage(PREFIX + "§cBoard is not loaded.");
            return null;
        }
        return blueprintBoard;
    }
}

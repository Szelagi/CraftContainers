/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.command.blueprint;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.szelagi.buildin.editor.BlueprintSession;
import pl.szelagi.command.SubCommand;
import pl.szelagi.manager.SessionManager;

import static pl.szelagi.command.CommandHelper.PREFIX;

public class BlueprintExitCommand implements SubCommand {
    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public String getDescription() {
        return "Exits the editor.";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return;

        var session = SessionManager.session(player);
        if (!(session instanceof BlueprintSession blueprintSession)) {
            player.sendMessage(PREFIX + "§cYou are not already in a blueprint editor.");
            return;
        }

        blueprintSession.stop();
    }
}

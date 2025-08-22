/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.command.marker;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.szelagi.command.SubCommand;

import static pl.szelagi.command.CommandHelper.PREFIX;

public class MarkerRemoveName extends MarkerSubCommand implements SubCommand {
    @Override
    public String getName() {
        return "removename";
    }

    @Override
    public String getDescription() {
        return "Removes all markers with the specified name.";
    }

    @Override
    public String getUsage() {
        return "<name>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return;
        var board = getBlueprintBoard(player);
        if (board == null) return;

        if (args.length != 1) {
            player.sendMessage(PREFIX + "§cYou must provide a name, which must be a single concatenated word.");
            return;
        }

        var name = args[0];

        var markers = board.getMarkers().removeByName(name);
        if (markers != null) {
            for (var marker : markers) {
                var message = String.format(MarkerRemoveId.SUCCESS_TEMPLATE, marker.getName(), marker.getId());
                player.sendMessage(message);
            }
        } else {
            var message = String.format(MarkerRemoveId.NO_MARKER_FOUND, name);
            player.sendMessage(message);
        }
    }
}

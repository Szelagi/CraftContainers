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

public class MarkerInfo extends MarkerSubCommand implements SubCommand {
    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Displays detailed information about a marker.";
    }

    @Override
    public String getUsage() {
        return "<id>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return;
        var board = getBlueprintBoard(player);
        if (board == null) return;

        if (args.length != 1) {
            player.sendMessage(PREFIX + "§cYou must provide id.");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(PREFIX + "§cThe marker ID must be a number.");
            return;
        }

        var markers = board.getMarkers();
        var marker = markers.getById(id);

        if (marker == null) {
            player.sendMessage(PREFIX + "§cNo marker found with ID " + id + ".");
            return;
        }

        player.sendMessage(marker.toString());
    }
}

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

public class MarkerRemoveId extends MarkerSubCommand implements SubCommand {
    public static final String SUCCESS_TEMPLATE = PREFIX + "Marker named %s with ID %d has been successfully removed.";
    public static final String NO_MARKER_FOUND = PREFIX + "§cNo marker found with %s.";

    @Override
    public String getName() {
        return "removeid";
    }

    @Override
    public String getDescription() {
        return "Removes a specific marker by its ID.";
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
            player.sendMessage(PREFIX + "§cYou must provide a ID.");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(PREFIX + "§cInvalid ID format.");
            return;
        }

        var marker = board.getMarkers().removeById(id);
        if (marker != null) {
            var message = String.format(SUCCESS_TEMPLATE, marker.getName(), marker.getId());
            player.sendMessage(message);
        } else {
            var message = String.format(NO_MARKER_FOUND, id);
            player.sendMessage(message);
        }
    }
}

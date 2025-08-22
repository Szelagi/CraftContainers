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

public class MarkerTp extends MarkerSubCommand implements SubCommand {
    public static final String NO_MARKER_FOUND_TEMPLATE = PREFIX + "§cNo marker found with %s.";
    public static final String TELEPORT_SUCCESS_TEMPLATE = PREFIX + "You have been teleported to marker %s with ID %d.";

    @Override
    public String getName() {
        return "tp";
    }

    @Override
    public String getDescription() {
        return "Teleports the player to the specified marker.";
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

        var marker = board.getMarkers().getById(id);
        if (marker == null) {
            var formatted = String.format(NO_MARKER_FOUND_TEMPLATE, id);
            player.sendMessage(formatted);
            return;
        }

        var formatted = String.format(TELEPORT_SUCCESS_TEMPLATE, marker.getName(), marker.getId());
        player.sendMessage(formatted);

        player.teleport(marker.getLocation());
    }
}

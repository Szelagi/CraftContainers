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

public class MarkerSetMetadata extends MarkerSubCommand implements SubCommand {
    public static final String SUCCESS_TEMPLATE = PREFIX + "Metadata '%s' set to '%s' for marker with ID %d.";

    @Override
    public String getName() {
        return "setmetadata";
    }

    @Override
    public String getDescription() {
        return "Sets metadata for an existing marker.";
    }

    @Override
    public String getUsage() {
        return "<id> <key> <value>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return;
        var board = getBlueprintBoard(player);
        if (board == null) return;

        if (args.length != 3) {
            player.sendMessage(PREFIX + "§cYou must provide id, key, value.");
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
        if (markers.getById(id) == null) {
            player.sendMessage(PREFIX + "§cNo marker found with ID " + id + ".");
            return;
        }

        var key = args[1];
        var value = args[2];

        var marker = markers.updateMetadata(id, ((marker1, iMutableMetadata) -> {
            iMutableMetadata.put(key, value);
        }));

        var message = String.format(SUCCESS_TEMPLATE, key, value, marker.getId());
        player.sendMessage(message);
    }
}

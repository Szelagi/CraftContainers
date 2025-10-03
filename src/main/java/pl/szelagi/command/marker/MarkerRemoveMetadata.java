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

public class MarkerRemoveMetadata extends MarkerSubCommand implements SubCommand {
    public static final String SUCCESS_TEMPLATE = PREFIX + "Metadata key '%s' has been removed from marker with ID %d.";

    @Override
    public String getName() {
        return "removemetadata";
    }

    @Override
    public String getDescription() {
        return "Deletes a metadata key from an existing marker.";
    }

    @Override
    public String getUsage() {
        return "<id> <key>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return;
        var board = getBlueprintBoard(player);
        if (board == null) return;

        if (args.length != 2) {
            player.sendMessage(PREFIX + "§cYou must provide id, key.");
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
        var prevMarker = markers.getById(id);

        if (prevMarker == null) {
            player.sendMessage(PREFIX + "§cNo marker found with ID " + id + ".");
            return;
        }

        var key = args[1];
        if (prevMarker.getMetadata().getString(key) == null) {
            player.sendMessage(PREFIX + "§cMarker with ID " + id + " does not have metadata key '" + key + "'.");
            return;
        }

        var marker = board.getMarkers().updateMetadata(id, ((marker1, iMutableMetadata) -> {
            iMutableMetadata.remove(key);
        }));

        var message = String.format(SUCCESS_TEMPLATE, key, marker.getId());
        player.sendMessage(message);
    }
}

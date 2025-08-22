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

public class MarkerAddHere extends MarkerSubCommand implements SubCommand {
    public static final String SUCCESS_TEMPLATE = PREFIX + "Marker named %s with ID %d has been created successfully.";

    @Override
    public String getName() {
        return "addhere";
    }

    @Override
    public String getDescription() {
        return "Creates a marker at the player’s current position.";
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
        var location = player.getLocation();
        var marker = board.getMarkers().create(name, location);

        var message = String.format(SUCCESS_TEMPLATE, name, marker.getId());
        player.sendMessage(message);
    }
}

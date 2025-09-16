/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.command.blueprint;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.szelagi.ConfigManager;
import pl.szelagi.buildin.editor.BlueprintGameMap;
import pl.szelagi.buildin.editor.BlueprintContainer;
import pl.szelagi.command.SubCommand;
import pl.szelagi.manager.ContainerManager;

import static pl.szelagi.command.CommandHelper.PREFIX;

public class BlueprintRadiusCommand implements SubCommand {
    @Override
    public String getName() {
        return "radius";
    }

    @Override
    public String getDescription() {
        return "Sets the maximum size of the schematic. (size = 2*radius-1)";
    }

    @Override
    public String getUsage() {
        return "<number>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return;

        var session = ContainerManager.container(player);
        if (!(session instanceof BlueprintContainer blueprintSession)) {
            player.sendMessage(PREFIX + "§cYou are not in a blueprint editor.");
            return;
        }

        var board = blueprintSession.gameMap();
        if (!(board instanceof BlueprintGameMap blueprintBoard)) {
            player.sendMessage(PREFIX + "§cBoard is not loaded.");
            return;
        }

        if (args.length != 1) {
            player.sendMessage(PREFIX + "Current radius is " + blueprintBoard.getRadius() + ".");
            return;
        }

        int size;
        try {
            size = Integer.parseInt(args[0]);
            if (size <= 0) throw new NumberFormatException();
            if (size >= ConfigManager.config().maxBoardSize)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage(PREFIX + "§cInvalid size specified.");
            return;
        }

        blueprintBoard.setRadius(size);
    }
}

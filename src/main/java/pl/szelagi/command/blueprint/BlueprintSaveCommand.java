/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.command.blueprint;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.szelagi.buildin.editor.BlueprintGameMap;
import pl.szelagi.buildin.editor.BlueprintContainer;
import pl.szelagi.command.SubCommand;
import pl.szelagi.manager.ContainerManager;

import static pl.szelagi.command.CommandHelper.PREFIX;

public class BlueprintSaveCommand implements SubCommand {
    @Override
    public String getName() {
        return "save";
    }

    @Override
    public String getDescription() {
        return "Saves the currently edited schematic without exiting.";
    }

    @Override
    public String getUsage() {
        return "";
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

        player.sendMessage(PREFIX + "§7§oSaving...");
        long millis = System.currentTimeMillis();

        blueprintBoard.save();

        long deltaTotalMillis = System.currentTimeMillis() - millis;
        player.sendMessage(PREFIX + "§aBlueprint saved successfully! §f(" + deltaTotalMillis + "ms)");
    }
}

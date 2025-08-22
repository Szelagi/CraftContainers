/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.command.blueprint;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.command.CommandRouter;

public class BlueprintCommand extends CommandRouter {
    public BlueprintCommand() {
        register(new BlueprintEditCommand());
        register(new BlueprintSaveCommand());
        register(new BlueprintExitCommand());
        register(new BlueprintRadiusCommand());
    }

    @Override
    public String getDescription() {
        return "Manages the editor for creating and editing schematic structures. Use /marker to manage coordinates in the editor.";
    }

    @Override
    public boolean preprocess(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        var isPlayer = sender instanceof Player;
        if (!isPlayer) {
            sender.sendMessage("§cYou must be a player to use this command.");
        }
        return isPlayer;
    }
}

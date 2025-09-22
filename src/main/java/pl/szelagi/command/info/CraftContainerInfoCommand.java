/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.command.info;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static pl.szelagi.command.CommandHelper.PREFIX;

public class CraftContainerInfoCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        sender.sendMessage(PREFIX);
        sender.sendMessage("§aThis server uses §eCraftContainers§a, a framework by §bSzelagi§a.");
        sender.sendMessage("§7It enables the creation of isolated game environments,");
        sender.sendMessage("§7significantly accelerating the development of minigames and other modular game scenarios.");
        sender.sendMessage("§7It provides ready-to-use solutions that simplify and accelerate the creation process.");
        sender.sendMessage("§aCheck it out and download it here: §9https://github.com/Szelagi/CraftContainers§a.");
        sender.sendMessage("§7If you like it, don’t forget to leave a §6★ Star§7 on the GitHub repository!");
        return true;
    }
}

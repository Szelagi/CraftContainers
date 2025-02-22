/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ServerWarning {
    public static void show(String error) {
        String errorMessage = "§6[Warning] §f" + error;
        for (Player player : Bukkit.getServer().getOnlinePlayers())
            if (player.isOp())
                player.sendMessage(errorMessage);
        Bukkit.getServer().getConsoleSender().sendMessage(errorMessage);
    }
}
/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.command.debug;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.SessionAPI;
import pl.szelagi.buildin.selfTest.SelfTest;
import pl.szelagi.buildin.system.testsession.TestSession;

import static pl.szelagi.command.CommandHelper.PREFIX;

public class SelfTestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(PREFIX + "You must be a player to use this command.");
            return false;
        }

        var session = new SelfTest(SessionAPI.instance(), player);
        session.start();
        commandSender.sendMessage(PREFIX + "§aSelfTest has been successfully started.");
        return true;
    }
}

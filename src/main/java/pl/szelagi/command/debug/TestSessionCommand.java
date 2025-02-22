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
import org.jetbrains.annotations.NotNull;
import pl.szelagi.SessionAPI;
import pl.szelagi.buildin.system.testSession.TestSession;

import static pl.szelagi.command.CommandHelper.PREFIX;

public class TestSessionCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        var session = new TestSession(SessionAPI.getInstance());
        session.start();
        commandSender.sendMessage(PREFIX + "§aTest session has been successfully started.");
        return true;
    }
}

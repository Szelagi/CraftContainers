/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.command.test;

import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.test.TestName;
import pl.szelagi.test.Tests;

import java.io.PrintWriter;
import java.io.StringWriter;

public class TestCommand implements CommandExecutor {
    public static final String COMMAND_NAME = "craftcontainers-e2e-test";
    public static final String SUCCESS = "OK";

    private String exceptionToString(Throwable e) {
        var sw = new StringWriter();
        var pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    private void sendError(CommandSender sender, String message) {
        if (message.equals(SUCCESS)) {
            throw new IllegalArgumentException("Success cannot be an error.");
        }
        sender.sendMessage(message);
    }

    private void sendSuccess(CommandSender sender) {
        sender.sendMessage(SUCCESS);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof RemoteConsoleCommandSender rcon)) {
            sendError(commandSender, "This command can only be executed by a remote console.");
            return true;
        }

        TestName testName;

        try {
            testName = TestName.valueOf(strings[0]);
        } catch (IllegalArgumentException e) {
            sendError(commandSender, "Invalid test name.");
            return true;
        }

        try {
            Tests.perform(testName);
            sendSuccess(commandSender);
        } catch (Exception e) {
            sendError(commandSender, exceptionToString(e));
        }
        return true;
    }
}

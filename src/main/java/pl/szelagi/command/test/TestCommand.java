/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.command.test;

import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import pl.szelagi.test.SAPITest;
import pl.szelagi.test.TestName;
import pl.szelagi.test.Tests;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestCommand implements CommandExecutor {
    public static final String COMMAND_NAME = "session-internal-test";
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
            return true;
        } catch (Exception e) {
            sendError(commandSender, exceptionToString(e));
        }
        return true;
    }
}

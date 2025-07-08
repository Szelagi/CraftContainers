/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.command.test;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.test.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class IntegrationTestCommand implements CommandExecutor {
    public static final String SUCCESS = "OK";
    private static final Map<String, Test> TESTS = new HashMap<>();

    public static void register(String name, Test test) {
        if (TESTS.containsKey(name))
            throw new IllegalArgumentException("Test already registered." + name);
        TESTS.put(name, test);
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
        if (strings.length == 0) {
            sendError(commandSender, "Test name not provided.");
            return true;
        }

        var testName = strings[0];
        var args = Arrays.copyOfRange(strings, 1, strings.length);

        var test = TESTS.get(testName);
        if (test == null) {
            sendError(commandSender, "Unknown test.");
            return true;
        }

        try {
            test.execute(args);
            sendSuccess(commandSender);
        } catch (Exception e) {
            var stackTrace = Arrays.stream(e.getStackTrace())
                    .map(StackTraceElement::toString)
                    .collect(Collectors.joining("\n"));

            var errorMessage = e.getClass().getName() + ": " + e.getMessage() + "\n" + stackTrace;

            sendError(commandSender, errorMessage);
        }

        return true;
    }
}

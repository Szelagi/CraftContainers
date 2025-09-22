/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.command;

import pl.szelagi.SessionAPI;
import pl.szelagi.command.container.ContainerCommand;
import pl.szelagi.command.debug.DebugSessionCommand;
import pl.szelagi.command.debug.TestSessionCommand;
import pl.szelagi.command.info.CraftContainerInfoCommand;
import pl.szelagi.command.blueprint.BlueprintCommand;
import pl.szelagi.command.marker.MarkerCommand;
import pl.szelagi.command.test.TestCommand;

public class Command {
    public static void registerCommands() {
        var sapi = SessionAPI.getInstance();

        // sessionapi
        var craftcontainers = sapi.getCommand("craftcontainers");
        assert craftcontainers != null;
        var craftcontainersResolver = new CraftContainerInfoCommand();
        craftcontainers.setExecutor(craftcontainersResolver);

        // session-test
        var sessionTest = sapi.getCommand("session-test");
        assert sessionTest != null;
        var sessionTestResolver = new TestSessionCommand();
        sessionTest.setExecutor(sessionTestResolver);

        // session-debug
        var sessionDebug = sapi.getCommand("session-debug");
        assert sessionDebug != null;
        var sessionDebugResolver = new DebugSessionCommand();
        sessionDebug.setExecutor(sessionDebugResolver);

        // session-integration-test
        var internalTest = sapi.getCommand(TestCommand.COMMAND_NAME);
        assert internalTest != null;
        var internalTestResolver = new TestCommand();
        internalTest.setExecutor(internalTestResolver);

        // blueprint
        var blueprint = sapi.getCommand("blueprint");
        assert blueprint != null;
        var blueprintResolver = new BlueprintCommand();
        blueprint.setExecutor(blueprintResolver);
        blueprint.setTabCompleter(blueprintResolver);

        // marker
        var marker = sapi.getCommand("marker");
        assert marker != null;
        var markerResolver = new MarkerCommand();
        marker.setExecutor(markerResolver);
        marker.setTabCompleter(markerResolver);

        // container
        var container = sapi.getCommand("container");
        assert container != null;
        var containerResolver = new ContainerCommand();
        container.setExecutor(containerResolver);
        container.setTabCompleter(containerResolver);

    }
}
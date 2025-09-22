/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.command.container;
import pl.szelagi.command.CommandRouter;

public class ContainerCommand extends CommandRouter {
    public ContainerCommand() {
        register(new StopCommand());
        register(new JoinCommand());
        register(new LeaveCommand());
        register(new AddPlayerCommand());
        register(new RemovePlayerCommand());
    }

    @Override
    public String getDescription() {
        return "Allows managing containers and the players in them.";
    }
}

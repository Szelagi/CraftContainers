/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.component;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.component.base.Component;
import pl.szelagi.component.base.StartException;
import pl.szelagi.component.base.StopException;
import pl.szelagi.event.bukkit.ControllerStartEvent;
import pl.szelagi.event.bukkit.ControllerStopEvent;
import pl.szelagi.component.container.Container;

import java.util.List;

public abstract class Controller extends Component {
    private final Container container;

    public Controller(@NotNull Component parent) {
        super(parent);
        this.container = parent.container();
    }

    @Override
    public final void start() throws StartException {
        super.start();

        var event = new ControllerStartEvent(this);
        callBukkit(event);
    }

    @Override
    public final void stop() throws StopException {
        super.stop();

        var event = new ControllerStopEvent(this);
        callBukkit(event);
    }

    @Override
    public final @NotNull List<Player> players() {
        return container.players();
    }

    @Override
    public @NotNull Container container() {
        return container;
    }

    @Override
    public final @Nullable GameMap gameMap() {
        return container.gameMap();
    }

}
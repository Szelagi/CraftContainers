/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.component.controller;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.component.baseComponent.StartException;
import pl.szelagi.component.baseComponent.StopException;
import pl.szelagi.component.board.Board;
import pl.szelagi.component.controller.bukkitEvent.ControllerStartEvent;
import pl.szelagi.component.controller.bukkitEvent.ControllerStopEvent;
import pl.szelagi.component.session.Session;

import java.util.List;

public abstract class Controller extends BaseComponent {
    private final Session session;

    public Controller(@NotNull BaseComponent parent) {
        super(parent);
        this.session = parent.session();
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
        return session.players();
    }

    @Override
    public @NotNull Session session() {
        return session;
    }

    @Override
    public final @Nullable Board board() {
        return session.board();
    }

}
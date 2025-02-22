/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.component.board.bukkitEvent;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.szelagi.component.board.Board;

public class BoardStopEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Board board;

    public BoardStopEvent(Board board) {
        this.board = board;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Board getBoard() {
        return board;
    }

    @Override
    public String getEventName() {
        return "BoardStopEvent";
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}

/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.event.bukkit;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.szelagi.component.GameMap;

public class BoardStopEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final GameMap gameMap;

    public BoardStopEvent(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public GameMap getBoard() {
        return gameMap;
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

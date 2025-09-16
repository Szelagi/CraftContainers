/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.event.bukkit;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.szelagi.component.container.Container;

public class SessionStopEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Container container;

    public SessionStopEvent(Container container) {
        this.container = container;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Container getSession() {
        return container;
    }

    @Override
    public String getEventName() {
        return "SessionStopEvent";
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}

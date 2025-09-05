/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.event.bukkit;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.szelagi.component.session.Session;

public class SessionStopEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Session session;

    public SessionStopEvent(Session session) {
        this.session = session;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Session getSession() {
        return session;
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

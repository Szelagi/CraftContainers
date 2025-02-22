/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.component.controller.bukkitEvent;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.szelagi.component.controller.Controller;

public class ControllerStopEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Controller controller;

    public ControllerStopEvent(Controller controller) {
        this.controller = controller;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Controller getController() {
        return controller;
    }

    @Override
    public String getEventName() {
        return "ControllerStopEvent";
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}

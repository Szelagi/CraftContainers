/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller.interaction;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.component.controller.Controller;
import pl.szelagi.manager.BoardManager;
import pl.szelagi.manager.listener.ListenerManager;
import pl.szelagi.manager.listener.Listeners;

public class NoPlaceBreak extends Controller {
    public NoPlaceBreak(BaseComponent baseComponent) {
        super(baseComponent);
    }

    @Override
    public Listeners defineListeners() {
        return super.defineListeners().add(MyListener.class);
    }

    private static class MyListener implements Listener {
        @EventHandler(ignoreCancelled = true)
        public void onBlockPlace(BlockPlaceEvent event) {
            var session = BoardManager.session(event.getBlock());
            ListenerManager.first(session, getClass(), NoPlaceBreak.class, noPlaceBreak -> {
                event.setCancelled(true);
            });
        }

        @EventHandler(ignoreCancelled = true)
        public void onBlockBreak(BlockBreakEvent event) {
            var session = BoardManager.session(event.getBlock());
            ListenerManager.first(session, getClass(), NoPlaceBreak.class, noPlaceBreak -> {
                event.setCancelled(true);
            });
        }
    }
}

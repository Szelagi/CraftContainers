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
import pl.szelagi.annotation.SingletonComponent;
import pl.szelagi.component.GameMap;
import pl.szelagi.component.base.Component;
import pl.szelagi.component.Controller;
import pl.szelagi.manager.GameMapManager;
import pl.szelagi.manager.listener.AdaptedListener;
import pl.szelagi.manager.listener.ListenerManager;
import pl.szelagi.manager.listener.Listeners;

import java.util.concurrent.atomic.AtomicBoolean;

@SingletonComponent
public class NoPlaceBreak extends Controller {
    public NoPlaceBreak(Component component) {
        super(component);
    }

    @Override
    public Listeners defineListeners() {
        return super.defineListeners().add(MyListener.class);
    }

    protected boolean canBreak(BlockBreakEvent event) {
        return false;
    }

    protected boolean canPlace(BlockPlaceEvent event) {
        return false;
    }

    private static class MyListener implements AdaptedListener {
        @EventHandler(ignoreCancelled = true)
        public void onBlockPlace(BlockPlaceEvent event) {
            var container = GameMap.getContainerForBlock(event.getBlock());
            var noPlaceBreakComponents = each(container, NoPlaceBreak.class);
            var anyAllowed = noPlaceBreakComponents.stream().anyMatch(noPlaceBreak -> noPlaceBreak.canPlace(event));
            if (!anyAllowed)
                event.setCancelled(true);
        }

        @EventHandler(ignoreCancelled = true)
        public void onBlockBreak(BlockBreakEvent event) {
            var container = GameMap.getContainerForBlock(event.getBlock());
            var noPlaceBreakComponents = each(container, NoPlaceBreak.class);
            var anyAllowed = noPlaceBreakComponents.stream().anyMatch(noPlaceBreak -> noPlaceBreak.canBreak(event));
            if (!anyAllowed)
                event.setCancelled(true);
        }
    }
}

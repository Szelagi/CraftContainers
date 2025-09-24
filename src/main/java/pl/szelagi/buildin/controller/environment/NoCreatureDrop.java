/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller.environment;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import pl.szelagi.component.GameMap;
import pl.szelagi.component.base.Component;
import pl.szelagi.component.Controller;
import pl.szelagi.manager.GameMapManager;
import pl.szelagi.manager.listener.AdaptedListener;
import pl.szelagi.manager.listener.ListenerManager;
import pl.szelagi.manager.listener.Listeners;

public class NoCreatureDrop extends Controller {
    public NoCreatureDrop(Component component) {
        super(component);
    }

    @Override
    public Listeners defineListeners() {
        return super.defineListeners().add(MyListener.class);
    }

    private static final class MyListener implements AdaptedListener {
        @EventHandler(ignoreCancelled = true)
        public void onEntityDeath(EntityDeathEvent event) {
            var session = GameMap.getContainerForEntity(event.getEntity());
            first(session, NoCreatureDrop.class, noCreatureDrop -> {
                event.getDrops().clear();
                event.setDroppedExp(0);
            });
        }
    }
}

/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller.environment;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import pl.szelagi.component.base.Component;
import pl.szelagi.component.Controller;
import pl.szelagi.manager.GameMapManager;
import pl.szelagi.manager.listener.ListenerManager;
import pl.szelagi.manager.listener.Listeners;

public class NoCreatureNaturalSpawn extends Controller {
    public NoCreatureNaturalSpawn(Component component) {
        super(component);
    }

    @Override
    public Listeners defineListeners() {
        return super.defineListeners().add(MyListener.class);
    }

    private static final class MyListener implements Listener {
        @EventHandler(ignoreCancelled = true)
        public void onCreatureSpawn(CreatureSpawnEvent event) {
            if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL)
                return;
            var session = GameMapManager.container(event.getEntity());
            ListenerManager.first(session, getClass(), NoCreatureNaturalSpawn.class, noCreatureDrop -> {
                event.setCancelled(true);
            });
        }

    }
}

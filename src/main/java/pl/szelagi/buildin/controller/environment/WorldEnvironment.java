/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller.environment;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import pl.szelagi.component.GameMap;
import pl.szelagi.component.base.Component;
import pl.szelagi.component.Controller;
import pl.szelagi.manager.GameMapManager;
import pl.szelagi.manager.listener.AdaptedListener;
import pl.szelagi.manager.listener.ListenerManager;
import pl.szelagi.manager.listener.Listeners;

public class WorldEnvironment extends Controller implements Listener {
    private boolean explosionDestroy = true;
    private boolean blockIgnite = true;
    private boolean fireSpread = true;
    private boolean blockBurn = true;

    public WorldEnvironment(Component component) {
        super(component);
    }

    public WorldEnvironment setExplosionDestroy(boolean state) {
        explosionDestroy = state;
        return this;
    }

    public WorldEnvironment setBlockIgnite(boolean state) {
        blockIgnite = state;
        return this;
    }

    public WorldEnvironment setFireSpread(boolean state) {
        fireSpread = state;
        return this;
    }

    public WorldEnvironment setBlockBurn(boolean state) {
        blockBurn = state;
        return this;
    }

    @Override
    public Listeners defineListeners() {
        return super.defineListeners().add(MyListener.class);
    }

    private static class MyListener implements AdaptedListener {
        @EventHandler(ignoreCancelled = true)
        public void onEntityExplode(EntityExplodeEvent event) {
            var container = GameMap.getContainerForLocation(event.getLocation());
            each(container, WorldEnvironment.class, worldEnvironment -> {
                if (worldEnvironment.explosionDestroy) return;
                event.blockList().clear();
            });
        }

        @EventHandler(ignoreCancelled = true)
        public void onBlockIgnite(BlockIgniteEvent event) {
            var container = GameMap.getContainerForBlock(event.getBlock());
            each(container, WorldEnvironment.class, worldEnvironment -> {
                if (worldEnvironment.blockIgnite) {
                    event.setCancelled(true);
                    return;
                }
                var cause = event.getCause();
                if (cause == BlockIgniteEvent.IgniteCause.SPREAD && !worldEnvironment.fireSpread) {
                    event.setCancelled(true);
                }
            });

        }

        @EventHandler(ignoreCancelled = true)
        public void onBlockBurn(BlockBurnEvent event) {
            var container = GameMap.getContainerForBlock(event.getBlock());
            each(container, WorldEnvironment.class, worldEnvironment -> {
                if (worldEnvironment.blockBurn) return;
                event.setCancelled(true);
            });
        }
    }
}

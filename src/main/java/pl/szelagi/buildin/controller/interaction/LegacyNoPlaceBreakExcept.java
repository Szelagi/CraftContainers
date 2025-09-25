/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller.interaction;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import pl.szelagi.component.GameMap;
import pl.szelagi.component.base.Component;
import pl.szelagi.component.Controller;
import pl.szelagi.manager.listener.AdaptedListener;
import pl.szelagi.manager.listener.Listeners;

import java.util.HashSet;
import java.util.Set;

public class LegacyNoPlaceBreakExcept extends Controller {
    Set<Material> allowBreak = new HashSet<>();
    Set<Material> allowPlace = new HashSet<>();

    public LegacyNoPlaceBreakExcept(Component component) {
        super(component);
    }

    public LegacyNoPlaceBreakExcept setPlaceFlag(Material material, boolean allow) {
        if (allow)
            allowPlace.add(material);
        else
            allowPlace.remove(material);
        return this;
    }

    public LegacyNoPlaceBreakExcept setBreakFlag(Material material, boolean allow) {
        if (allow)
            allowBreak.add(material);
        else
            allowBreak.remove(material);
        return this;
    }

    public void clearPlaceFlags() {
        allowPlace.clear();
    }

    public void clearBreakFlags() {
        allowBreak.clear();
    }

    public void clearAllFlags() {
        clearPlaceFlags();
        clearBreakFlags();
    }

    public boolean canPlace(Material material) {
        return allowPlace.contains(material);
    }

    public boolean canBreak(Material material) {
        return allowBreak.contains(material);
    }

    @Override
    public Listeners defineListeners() {
        return super.defineListeners().add(MyListener.class);
    }

    private static class MyListener implements AdaptedListener {
        @EventHandler(ignoreCancelled = true)
        public void onBlockPlace(BlockPlaceEvent event) {
            var container = GameMap.getContainerForBlock(event.getBlock());
            if (container == null)
                return;
            var material = event.getBlock()
                    .getType();
            each(container, LegacyNoPlaceBreakExcept.class, legacyNoPlaceBreakExcept -> {
                if (legacyNoPlaceBreakExcept.canPlace(material))
                    return;
                event.setCancelled(true);
            });
        }

        @EventHandler(ignoreCancelled = true)
        public void onBlockBreak(BlockBreakEvent event) {
            var container = GameMap.getContainerForBlock(event.getBlock());
            if (container == null)
                return;
            var material = event.getBlock()
                    .getType();
            each(container, LegacyNoPlaceBreakExcept.class, legacyNoPlaceBreakExcept -> {
                if (legacyNoPlaceBreakExcept.canBreak(material))
                    return;
                event.setCancelled(true);
            });
        }
    }
}

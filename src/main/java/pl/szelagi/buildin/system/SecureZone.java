/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.system;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.component.controller.Controller;
import pl.szelagi.manager.BoardManager;
import pl.szelagi.manager.SessionManager;
import pl.szelagi.manager.listener.ListenerManager;
import pl.szelagi.manager.listener.Listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SecureZone extends Controller {
    public SecureZone(@NotNull BaseComponent parent) {
        super(parent);
    }

    @Override
    public Listeners defineListeners() {
        return super.defineListeners().add(MyListener.class);
    }

    private static final class MyListener implements Listener {
        private boolean check(Player player, Block block) {
            var session = SessionManager.session(player);
            var controller = ListenerManager.first(session, getClass(), Controller.class);
            if (controller == null) return false;

            var blockLoc = block.getLocation();

            assert session != null;
            return !session.board().secureZone().isLocationIn(blockLoc);
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
        public void onBlockPlace(BlockPlaceEvent event) {
            if (check(event.getPlayer(), event.getBlock()))
                event.setCancelled(true);
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
        public void onBlockBreak(BlockBreakEvent event) {
            if (check(event.getPlayer(), event.getBlock()))
                event.setCancelled(true);
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
        public void onBlockPistonExtend(BlockPistonExtendEvent event) {
            var session = BoardManager.session(event.getBlock());
            var controller = ListenerManager.first(session, getClass(), SecureZone.class);
            if (controller == null) return;

            assert session != null;
            Function<Location, Boolean> isLocationIn = session
                    .board()
                    .secureZone()::isLocationIn;

            var headLocation = event.getBlock()
                    .getRelative(event.getDirection(), event
                            .getBlocks()
                            .size() + 1)
                    .getLocation();
            if (!isLocationIn.apply(headLocation)) {
                event.setCancelled(true);
                return;
            }
            for (var block : event.getBlocks()) {
                if (isLocationIn.apply(block.getLocation()))
                    continue;
                event.setCancelled(true);
                return;
            }
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
        public void onBlockPistonRetract(BlockPistonRetractEvent event) {
            var session = BoardManager.session(event.getBlock());
            var controller = ListenerManager.first(session, getClass(), SecureZone.class);
            if (controller == null) return;

            assert session != null;
            Function<Location, Boolean> isLocationIn = session
                    .board()
                    .secureZone()::isLocationIn;

            if (!event.isSticky())
                return;
            Location stickyBlockLocation = event
                    .getBlock()
                    .getRelative(event.getDirection(), -2)
                    .getLocation();
            if (!isLocationIn.apply(stickyBlockLocation)) {
                event.setCancelled(true);
            }
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
        public void onStructureGrow(StructureGrowEvent event) {
            var session = BoardManager.session(event.getLocation());
            var controller = ListenerManager.first(session, getClass(), SecureZone.class);
            if (controller == null) return;

            List<BlockState> toRemove = new ArrayList<>();

            event.getBlocks().forEach(block -> {
                assert session != null;
                boolean isApart = !session
                        .board()
                        .secureZone()
                        .isLocationIn(block.getLocation());
                if (isApart)
                    toRemove.add(block);
            });

            event.getBlocks().removeAll(toRemove);
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
        public void onBlockFromTo(BlockFromToEvent event) {
            var material = event.getBlock()
                    .getType();
            var isFluid = material == Material.WATER || material == Material.LAVA;
            if (!isFluid)
                return;
            var to = event.getToBlock()
                    .getLocation();
            var from = event.getBlock()
                    .getLocation();
            var session = BoardManager.session(from);
            var controller = ListenerManager.first(session, getClass(), SecureZone.class);
            if (controller == null) return;

            assert session != null;
            Function<Location, Boolean> isLocationIn = session
                    .board()
                    .secureZone()::isLocationIn;

            if (!isLocationIn.apply(to))
                event.setCancelled(true);
        }

        public boolean waterBucketCheck(Player player, Location location) {
            var session = SessionManager.session(player);
            var controller = ListenerManager.first(session, getClass(), SecureZone.class);
            if (controller == null) return false;

            assert session != null;
            Function<Location, Boolean> isLocationIn = session
                    .board()
                    .secureZone()::isLocationIn;
            return !isLocationIn.apply(location);
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
        public void onPlayerBucketFill(PlayerBucketFillEvent event) {
            if (waterBucketCheck(event.getPlayer(), event
                    .getBlock().getLocation()))
                event.setCancelled(true);
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
        public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
            if (waterBucketCheck(event.getPlayer(), event
                    .getBlock().getLocation()))
                event.setCancelled(true);
        }
    }
}

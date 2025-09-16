/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.manager;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.component.GameMap;
import pl.szelagi.component.container.Container;
import pl.szelagi.event.bukkit.BoardStartEvent;
import pl.szelagi.event.bukkit.BoardStopEvent;

import java.util.ArrayList;
import java.util.List;

public class GameMapManager {
    private static final List<GameMap> RUNNING_GAME_MAPS = new ArrayList<>();
    private static JavaPlugin plugin;

    public static void initialize(JavaPlugin p) {
        plugin = p;

        class MyListener implements Listener {
            @EventHandler(ignoreCancelled = true)
            public void onBoardStart(BoardStartEvent event) {
                if (!RUNNING_GAME_MAPS.contains(event.getBoard())) {
                    RUNNING_GAME_MAPS.add(event.getBoard());
                }
            }

            @EventHandler(ignoreCancelled = true)
            public void onBoardStop(BoardStopEvent event) {
                RUNNING_GAME_MAPS.remove(event.getBoard());
            }
        }

        plugin.getServer().getPluginManager()
                .registerEvents(new MyListener(), plugin);
    }

    public static @Nullable Container container(@Nullable Location location) {
        if (location == null) {
            return null;
        }
        for (var board : RUNNING_GAME_MAPS) {
            var space = board.space();
            if (space.isLocationIn(location))
                return board.container();
        }
        return null;
    }

    public static @Nullable Container container(@Nullable LivingEntity entity) {
        if (entity == null) return null;
        return container(entity.getLocation());
    }

    public static @Nullable Container container(@Nullable Block block) {
        if (block == null) return null;
        return container(block.getLocation());
    }
}

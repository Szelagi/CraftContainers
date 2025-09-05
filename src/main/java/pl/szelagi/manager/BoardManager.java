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
import pl.szelagi.component.Board;
import pl.szelagi.event.bukkit.BoardStartEvent;
import pl.szelagi.event.bukkit.BoardStopEvent;
import pl.szelagi.component.session.Session;

import java.util.ArrayList;
import java.util.List;

public class BoardManager {
    private static final List<Board> runningBoards = new ArrayList<>();
    private static JavaPlugin plugin;

    public static void initialize(JavaPlugin p) {
        plugin = p;

        class MyListener implements Listener {
            @EventHandler(ignoreCancelled = true)
            public void onBoardStart(BoardStartEvent event) {
                if (!runningBoards.contains(event.getBoard())) {
                    runningBoards.add(event.getBoard());
                }
            }

            @EventHandler(ignoreCancelled = true)
            public void onBoardStop(BoardStopEvent event) {
                runningBoards.remove(event.getBoard());
            }
        }

        plugin.getServer().getPluginManager()
                .registerEvents(new MyListener(), plugin);
    }

    public static @Nullable Session session(@Nullable Location location) {
        if (location == null) {
            return null;
        }
        for (var board : runningBoards) {
            var space = board.space();
            if (space.isLocationIn(location))
                return board.session();
        }
        return null;
    }

    public static @Nullable Session session(LivingEntity entity) {
        return session(entity.getLocation());
    }

    public static @Nullable Session session(@Nullable Block block) {
        if (block == null)
            return null;
        return session(block.getLocation());
    }
}

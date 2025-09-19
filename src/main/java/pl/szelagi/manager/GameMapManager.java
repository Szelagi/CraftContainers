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

@Deprecated
public class GameMapManager {
    public static @Nullable Container container(@Nullable Location location) {
        return GameMap.getContainerForLocation(location);
    }

    public static @Nullable Container container(@Nullable LivingEntity entity) {
        return container(entity.getLocation());
    }

    public static @Nullable Container container(@Nullable Block block) {
        if (block == null) return null;
        return container(block.getLocation());
    }
}

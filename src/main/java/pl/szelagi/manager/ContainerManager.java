/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.manager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import pl.szelagi.SessionAPI;
import pl.szelagi.component.container.Container;
import pl.szelagi.event.bukkit.SessionStartEvent;
import pl.szelagi.event.bukkit.SessionStopEvent;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ContainerManager {
    public static final Set<Container> CONTAINERS = new LinkedHashSet<>();
    private static final Map<Player, Container> PLAYER_CONTAINER_HASH_MAP = new HashMap<>();

    public static void addRelation(Player p, Container d) {
        PLAYER_CONTAINER_HASH_MAP.put(p, d);
    }

    public static void removeRelation(Player p) {
        PLAYER_CONTAINER_HASH_MAP.remove(p);
    }

    public static boolean inSession(Player p) {
        return PLAYER_CONTAINER_HASH_MAP.containsKey(p);
    }

    @Nullable
    public static Container container(Player p) {
        return PLAYER_CONTAINER_HASH_MAP.get(p);
    }

    @Nullable
    public static <T extends Container> Container container(Player p, Class<T> classType) {
        var session = container(p);
        return classType.isInstance(session) ? classType.cast(session) : null;
    }

    public static @NotNull @Unmodifiable Set<Container> containers() {
        return CONTAINERS;
    }

    public static void initialize(JavaPlugin p) {
        class ManagerListener implements Listener {
            @EventHandler(ignoreCancelled = true)
            public void onSessionStartEvent(SessionStartEvent event) {
                CONTAINERS.add(event.getSession());
            }

            @EventHandler(ignoreCancelled = true)
            public void onSessionStopEvent(SessionStopEvent event) {
                CONTAINERS.remove(event.getSession());
            }

            @EventHandler(ignoreCancelled = true)
            public void onPlayerQuit(PlayerQuitEvent event) {
                var player = event.getPlayer();
                var session = container(player);
                if (session == null) return;
                session.removePlayer(player);
            }

            @EventHandler(ignoreCancelled = true)
            public void onPluginDisable(PluginDisableEvent event) {
                if (!SessionAPI.instance().equals(event.getPlugin())) return;
                for (var session : containers()) {
                    session.stop();
                }
            }
        }
        p.getServer().getPluginManager()
                .registerEvents(new ManagerListener(), p);
    }
}

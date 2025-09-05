/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.system;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.component.Controller;
import pl.szelagi.component.session.Session;
import pl.szelagi.manager.BoardManager;
import pl.szelagi.manager.SessionManager;
import pl.szelagi.manager.listener.ListenerManager;
import pl.szelagi.manager.listener.Listeners;
import pl.szelagi.util.timespigot.Time;

import static pl.szelagi.command.CommandHelper.PREFIX;


public class BoardWatchDog extends Controller {
    private static final String ILLEGAL_SPACE_EXIT = PREFIX + "§cYou cannot leave the area this way.";
    private static final String ILLEGAL_SPACE_ENTER = PREFIX + "§cYou do not have access to this area.";

    public BoardWatchDog(@NotNull BaseComponent parent) {
        super(parent);
    }

    @Override
    public Listeners defineListeners() {
        return super.defineListeners().add(MyListener.class);
    }

    public boolean isLocationCorrect(Location location) {
        var board = session().board();
        if (board == null) return true;
        var space = board.space();
        return space.isLocationInXZ(location);
    }

    private static final class MyListener implements Listener {
        @EventHandler(ignoreCancelled = true)
        public void onPlayerMove(PlayerMoveEvent event) {
            var from = event.getFrom();
            var to = event.getTo();

            // Ignore micro movements
            if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ())
                return;

            var player = event.getPlayer();
            var session = SessionManager.session(player);
            if (session == null) return;
            ListenerManager.first(session, getClass(), BoardWatchDog.class, boardWatchDog -> {
                if (!boardWatchDog.isLocationCorrect(event.getTo())) {
                    event.setCancelled(true);
                }
            });
        }

        @EventHandler(ignoreCancelled = true)
        public void onPlayerTeleport(PlayerTeleportEvent event) {
            var player = event.getPlayer();
            var from = event.getFrom();
            var to = event.getTo();

            var sessionInDestination = BoardManager.session(to);
            var playerSession = SessionManager.session(event.getPlayer());

            if (playerSession != null) {
                handlePlayerInSession(playerSession, sessionInDestination, player, from, to, event);
            } else if (sessionInDestination != null) {
                // Gracz teleportuje się do sesji, która nie należy do niego
                event.setCancelled(true);
                player.sendMessage(ILLEGAL_SPACE_ENTER);
            }
        }

        private void handlePlayerInSession(
                Session playerSession,
                Session sessionInDestination,
                Player player,
                Location from,
                Location to,
                PlayerTeleportEvent event
        ) {
            ListenerManager.first(playerSession, getClass(), BoardWatchDog.class, boardWatchDog -> {
                // Jeżeli teleportacja jest wewnątrz sesji, pomiń
                if (boardWatchDog.isLocationCorrect(to)) return;

                if (sessionInDestination != null && !playerSession.equals(sessionInDestination)) {
                    // Gracz próbuje przenieść się na inną sesję
                    event.setCancelled(true);
                    player.sendMessage(ILLEGAL_SPACE_ENTER);
                } else {
                    // Zablokuj teleportacje poza mapą, używając zadania opóźnionego
                    boardWatchDog.runTaskLater(() -> {
                        var currentSession = SessionManager.session(player);
                        if (currentSession == null) return;

                        ListenerManager.first(playerSession, getClass(), BoardWatchDog.class, watchdogCheck -> {
                            if (!watchdogCheck.isLocationCorrect(player.getLocation())) {
                                player.teleport(from);
                                player.sendMessage(ILLEGAL_SPACE_EXIT);
                            }
                        });
                    }, Time.ticks(1));
                }
            });
        }
    }
}

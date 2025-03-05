/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.system;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.Scheduler;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.component.baseComponent.internalEvent.component.ComponentConstructor;
import pl.szelagi.component.controller.Controller;
import pl.szelagi.manager.SessionManager;
import pl.szelagi.manager.listener.ListenerManager;
import pl.szelagi.manager.listener.Listeners;
import pl.szelagi.util.ServerWarning;
import pl.szelagi.util.timespigot.Time;
import pl.szelagi.world.SessionWorldManager;


public class BoardWatchDog extends Controller {
    public BoardWatchDog(@NotNull BaseComponent parent) {
        super(parent);
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        runTaskTimer(this::checkAllPlayers, Time.seconds(0), Time.seconds(3));
    }

    @Override
    public Listeners defineListeners() {
        return super.defineListeners().add(MyListener.class);
    }

    public void checkAllPlayers() {
        for (var player : players())
            if (stopWhenPlayerExitSpace(player))
                return;
    }

    public boolean stopWhenPlayerExitSpace(Player player) {
        var space = session().board().space();
        if (!space.isLocationInXZ(player.getLocation())) {
            var identifier = session().identifier() + ", " + session().board().identifier();
            session().stop();
            var message = "Player §7" + player.getName() + "§f performed an illegal exit from the assigned board area! §7(" + identifier + ")";
            ServerWarning.show(message);
            return true;
        }
        return false;
    }

    public void warnWhenAlienInSpace(Player player, Location backLocation) {
        boolean isPlayerCorrect = session().players().contains(player);
        if (isPlayerCorrect)
            return;
        var space = session().board().space();
        boolean isIn = space.isLocationInXZ(player.getLocation());
        if (!isIn)
            return;

        if (player.isOp() && player.getGameMode() == GameMode.SPECTATOR)
            return;

        boolean isBackIn = space.isLocationInXZ(backLocation);
        if (isBackIn) {
            session().stop();
            var identifier = session().identifier() + ", " + session().board().identifier();
            var message = "Incorrect back location for player §7" + player.getName() + "§f. Illegal entry into restricted board area, §7(" + identifier + ")";
            ServerWarning.show(message);
        }

        player.teleport(backLocation);

        if (player.isOp()) {
            player.sendMessage("§4[ADMIN] §fYou tried to enter a restricted board area without §c§SPECTATOR§f game mode!");
        } else {
            var identifier = session().identifier() + ", " + session().board().identifier();
            ServerWarning.show("Player §7" + player.getName() + "§f attempted illegal entry into restricted board area §7(" + identifier + ")");
        }
    }

    private static final class MyListener implements Listener {
        @EventHandler(ignoreCancelled = true)
        public void onPlayerTeleport(PlayerTeleportEvent event) {
            var previousLocation = event.getPlayer()
                    .getLocation()
                    .clone();
            var playerSession = SessionManager.session(event.getPlayer());
            if (playerSession != null) {
                ListenerManager.each(playerSession, getClass(), BoardWatchDog.class, baseComponent -> {
                    baseComponent.runTaskLater(() -> {
                        var playerSession2 = SessionManager.session(event.getPlayer());
                        if (playerSession != playerSession2) return;
                        baseComponent.stopWhenPlayerExitSpace(event.getPlayer());
                    }, Time.ticks(1));
                });
            } else {
                Scheduler.runTaskLater(() -> {
                    if (event.getPlayer()
                            .getWorld()
                            .getName()
                            .equals(SessionWorldManager
                                    .getSessionWorld()
                                    .getName())) {
                        for (var session : SessionManager.sessions()) {
                            ListenerManager.first(session, getClass(), BoardWatchDog.class, baseComponent -> {
                                baseComponent.warnWhenAlienInSpace(event.getPlayer(), previousLocation);
                            });
                        }
                    }
                }, Time.ticks(1));
            }
        }
    }
}

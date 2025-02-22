/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.system;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.component.controller.Controller;
import pl.szelagi.manager.SessionManager;
import pl.szelagi.manager.listener.ListenerManager;
import pl.szelagi.manager.listener.Listeners;
import pl.szelagi.util.ServerWarning;
import pl.szelagi.util.timespigot.Time;
import pl.szelagi.world.SessionWorldManager;

public class SessionWatchDog extends Controller {
    public SessionWatchDog(@NotNull BaseComponent parent) {
        super(parent);
    }

    @Override
    public Listeners defineListeners() {
        return super.defineListeners().add(MyListener.class);
    }

    private static final class MyListener implements Listener {
        @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
        public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
            var player = event.getPlayer();
            var session = SessionManager.session(player);
            ListenerManager.first(session, getClass(), baseComponent -> {
                baseComponent.runTaskLater(() -> checkCorrectWorld(player), Time.ticks(1));
            });
        }

        private void checkCorrectWorld(Player player) {
            var session = SessionManager.session(player);
            ListenerManager.first(session, getClass(), baseComponent -> {
                var worldName = player.getWorld().getName();
                var sessionWorldName = SessionWorldManager.getSessionWorld().getName();
                var isSame = worldName.equals(sessionWorldName);
                if (isSame) return;
                assert session != null;
                var identifier = session.identifier() + ", " + session.board().identifier();
                session.stop();
                var message = "Player " + player.getName() + " performed an illegal teleportation out of the assigned SessionWorld! §7(" + identifier + "§7)";
                ServerWarning.show(message);
            });
        }
    }
}

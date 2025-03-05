/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller.interaction;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.component.controller.Controller;
import pl.szelagi.manager.SessionManager;
import pl.szelagi.manager.listener.ListenerManager;
import pl.szelagi.manager.listener.Listeners;
import pl.szelagi.util.CooldownVolatile;
import pl.szelagi.util.timespigot.Time;

public class NoPvP extends Controller {
    public NoPvP(BaseComponent baseComponent) {
        super(baseComponent);
    }

    @Override
    public Listeners defineListeners() {
        return super.defineListeners().add(MyListener.class);
    }

    private static class MyListener implements Listener {
        @EventHandler(ignoreCancelled = true)
        public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
            if (!(event.getEntity() instanceof Player victim))
                return;
            if (!(event.getDamager() instanceof Player attacker))
                return;
            var session = SessionManager.session(victim);
            ListenerManager.first(session, getClass(), NoPvP.class, noPvP -> {
                if (CooldownVolatile.canUseAndStart(attacker, noPvP.name(), Time.seconds(2)))
                    attacker.sendMessage("§cYou cannot attack a player because pvp is disabled!");
                event.setCancelled(true);
            });
        }
    }
}

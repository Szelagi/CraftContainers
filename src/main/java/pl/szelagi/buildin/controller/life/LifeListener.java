/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller.life;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import pl.szelagi.manager.ControllerManager;
import pl.szelagi.manager.SessionManager;
import pl.szelagi.util.timespigot.Time;

public class LifeListener implements Listener {
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;
		if (player.getHealth() - event.getFinalDamage() > 0)
			return;
		var dungeon = SessionManager.getSession(player);
		if (dungeon == null)
			return;
		Life controller = ControllerManager.getFirstController(dungeon, Life.class);
		if (controller == null)
			return;
		event.setDamage(0);
		controller.killPlayer(player);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
		if (!event.isSneaking())
			return;
		var player = event.getPlayer();
		if (player.getGameMode() != GameMode.SPECTATOR)
			return;
		var dungeon = SessionManager.getSession(player);
		if (dungeon == null)
			return;
		Life controller = ControllerManager.getFirstController(dungeon, Life.class);
		if (controller == null)
			return;
		var state = controller
				.getPlayerStateContainer()
				.getOrCreate(player);
		if (state.isAlive())
			return;
		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.getCause() != PlayerTeleportEvent.TeleportCause.SPECTATE)
			return;
		var player = event.getPlayer();
		var dungeon = SessionManager.getSession(player);
		if (dungeon == null)
			return;
		Life controller = ControllerManager.getFirstController(dungeon, Life.class);
		if (controller == null)
			return;
		var state = controller
				.getPlayerStateContainer()
				.getOrCreate(player);
		if (state.isStopOneSpectateEvent()) {
			state.setStopOneSpectateEvent(false);
			return;
		}
		controller.getProcess()
		          .runControlledTaskLater(() -> player.setSpectatorTarget(controller.getFirstAlivePlayer()), Time.Ticks(1));
	}
}

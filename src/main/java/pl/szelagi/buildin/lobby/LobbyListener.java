/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.lobby;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import pl.szelagi.manager.ControllerManager;
import pl.szelagi.manager.SessionManager;

public class LobbyListener implements Listener {
	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		if (check(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (check(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player player)
			if (check(player))
				event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (check(event.getPlayer()))
			event.setCancelled(true);
	}

	private boolean check(Player player) {
		var session = SessionManager.getSession(player);
		if (session == null)
			return false;
		var controller = ControllerManager.getFirstController(session, Lobby.class);
		if (controller == null)
			return false;
		return controller.isLobby();
	}
}

/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller.interaction;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.component.controller.Controller;
import pl.szelagi.manager.BoardManager;
import pl.szelagi.manager.listener.ListenerManager;
import pl.szelagi.manager.listener.Listeners;

public class ProtectItemFrame extends Controller {
	public ProtectItemFrame(BaseComponent baseComponent) {
		super(baseComponent);
	}

	@Override
	public Listeners defineListeners() {
		return super.defineListeners().add(MyListener.class);
	}

	private static class MyListener implements Listener {
		public boolean check(Entity entity) {
			var entityType = entity.getType();
			if (entityType != EntityType.ITEM_FRAME)
				return false;
			var session = BoardManager.session(entity.getLocation());
			var component = ListenerManager.first(session, getClass(), ProtectItemFrame.class);
			return (component != null);
		}

		@EventHandler(ignoreCancelled = true)
		public void onEntityDamage(EntityDamageEvent event) {
			if (check(event.getEntity()))
				event.setCancelled(true);
		}

		@EventHandler(ignoreCancelled = true)
		public void onHangingBreak(HangingBreakEvent event) {
			if (check(event.getEntity()))
				event.setCancelled(true);
		}

		@EventHandler(ignoreCancelled = true)
		public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
			if (check(event.getRightClicked()))
				event.setCancelled(true);
		}
	}
}

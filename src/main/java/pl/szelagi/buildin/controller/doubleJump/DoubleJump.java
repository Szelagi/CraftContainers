/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller.doubleJump;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.component.ISessionComponent;
import pl.szelagi.component.controller.Controller;
import pl.szelagi.event.component.ComponentConstructorEvent;
import pl.szelagi.event.player.initialize.PlayerConstructorEvent;
import pl.szelagi.event.player.initialize.PlayerDestructorEvent;
import pl.szelagi.event.player.recovery.PlayerStateRecoveryEvent;
import pl.szelagi.manager.ControllerManager;
import pl.szelagi.manager.SessionManager;
import pl.szelagi.state.PlayerContainer;
import pl.szelagi.util.timespigot.Time;

public class DoubleJump extends Controller {
	private Time cooldown;
	private PlayerContainer<DoubleJumpState> playerContainer;

	public DoubleJump(ISessionComponent sessionComponent, Time cooldown) {
		super(sessionComponent);
		this.cooldown = cooldown;
	}

	@Override
	public void componentConstructor(ComponentConstructorEvent event) {
		super.componentConstructor(event);
		playerContainer = new PlayerContainer<>(player -> new DoubleJumpState(player, player.getAllowFlight()));
	}

	@Override
	public void playerConstructor(PlayerConstructorEvent event) {
		super.playerConstructor(event);
		var player = event.getPlayer();
		playerContainer.getOrCreate(player);
		player.setAllowFlight(true);
	}

	@Override
	public void playerDestructor(PlayerDestructorEvent event) {
		super.playerDestructor(event);
		var player = event.getPlayer();
		var playerState = playerContainer.getOrCreate(player);
		player.setAllowFlight(playerState.oldFlyState());
	}

	@Override
	public void playerDestructorRecovery(PlayerStateRecoveryEvent event) {
		super.playerDestructorRecovery(event);
		var forPlayer = event.getForPlayer();
		var oldFlyState = playerContainer
				.getOrCreate(forPlayer).oldFlyState();
		event.getLambdas().add(player -> {
			player.setAllowFlight(oldFlyState);
		});
	}

	@Override
	public @Nullable Listener getListener() {
		return new MyListener();
	}

	public static class MyListener implements Listener {
		@EventHandler
		public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
			var player = event.getPlayer();

			if (player.getGameMode() == GameMode.CREATIVE)
				return;

			if (player.isFlying())
				return;

			var session = SessionManager.getSession(player);
			var controller = ControllerManager.getFirstController(session, DoubleJump.class);
			if (controller == null)
				return;

			event.setCancelled(true);

			var state = controller.playerContainer.getOrCreate(player);
			if (state.canJump(controller.cooldown)) {
				state.jump();
				var jumpBoost = player
						.getVelocity().setY(0.75);
				player.setVelocity(jumpBoost);
			}
		}
	}

	public Time cooldown() {
		return cooldown;
	}

	public void setCooldown(Time cooldown) {
		this.cooldown = cooldown;
	}
}

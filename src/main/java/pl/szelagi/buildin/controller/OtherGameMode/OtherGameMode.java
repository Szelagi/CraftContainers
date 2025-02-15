/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller.OtherGameMode;

import org.bukkit.GameMode;
import pl.szelagi.component.ISessionComponent;
import pl.szelagi.component.controller.Controller;
import pl.szelagi.event.component.ComponentConstructorEvent;
import pl.szelagi.event.player.initialize.PlayerConstructorEvent;
import pl.szelagi.event.player.initialize.PlayerDestructorEvent;
import pl.szelagi.event.player.recovery.PlayerStateRecoveryEvent;
import pl.szelagi.state.PlayerContainer;

public class OtherGameMode extends Controller {
	private PlayerContainer<GameModeState> states;
	private final GameMode gameMode;

	public OtherGameMode(ISessionComponent sessionComponent, GameMode gameMode) {
		super(sessionComponent);
		this.gameMode = gameMode;
	}

	@Override
	public void componentConstructor(ComponentConstructorEvent event) {
		super.componentConstructor(event);
		states = new PlayerContainer<>(GameModeState::new);
	}

	@Override
	public void playerConstructor(PlayerConstructorEvent event) {
		super.playerConstructor(event);
		states.getOrCreate(event.getPlayer());
		event.getPlayer().setGameMode(gameMode);
	}

	@Override
	public void playerDestructor(PlayerDestructorEvent event) {
		super.playerDestructor(event);
		var player = event.getPlayer();
		var state = states.getOrCreate(player);
		player.setGameMode(state.getGameMode());
		states.clearState(player);
	}

	@Override
	public void playerDestructorRecovery(PlayerStateRecoveryEvent event) {
		super.playerDestructorRecovery(event);
		var state = states.getOrCreate(event.getForPlayer());
		final var gameMode = state.getGameMode();
		event.getLambdas().add(player -> {
			player.setGameMode(gameMode);
		});
	}
}

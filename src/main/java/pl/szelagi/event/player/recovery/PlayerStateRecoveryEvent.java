/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.event.player.recovery;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.component.constructor.PlayerDestructorLambdas;
import pl.szelagi.event.SAPIEvent;

public class PlayerStateRecoveryEvent extends SAPIEvent {
	private final @NotNull Player forPlayer;
	private final @NotNull PlayerDestructorLambdas lambdas = new PlayerDestructorLambdas();

	public PlayerStateRecoveryEvent(@NotNull Player forPlayer) {
		this.forPlayer = forPlayer;
	}

	public @NotNull Player getForPlayer() {
		return forPlayer;
	}

	public @NotNull PlayerDestructorLambdas getLambdas() {
		return lambdas;
	}
}

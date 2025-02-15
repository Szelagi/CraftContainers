/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.event.player.initialize;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.event.SAPIEvent;

import java.util.Collection;

public abstract class PlayerChangeEvent extends SAPIEvent {
	private final @NotNull Player player;
	private final @NotNull Collection<Player> otherSessionPlayers;
	private final @NotNull Collection<Player> allSessionPlayers;
	private final @NotNull InvokeType invokeType;

	public PlayerChangeEvent(@NotNull Player player, @NotNull Collection<Player> otherSessionPlayers, @NotNull Collection<Player> allSessionPlayers, @NotNull InvokeType invokeType) {
		this.player = player;
		this.otherSessionPlayers = otherSessionPlayers;
		this.allSessionPlayers = allSessionPlayers;
		this.invokeType = invokeType;
	}

	public @NotNull Player getPlayer() {
		return player;
	}

	public @NotNull Collection<Player> getOtherSessionPlayers() {
		return otherSessionPlayers;
	}

	public @NotNull Collection<Player> getAllSessionPlayers() {
		return allSessionPlayers;
	}

	public @NotNull InvokeType getInvokeType() {
		return invokeType;
	}
}

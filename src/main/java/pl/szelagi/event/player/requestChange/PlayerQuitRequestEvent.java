/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.event.player.requestChange;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.event.player.requestChange.type.QuitType;

import java.util.List;

public class PlayerQuitRequestEvent extends PlayerChangeRequestEvent {
	private final QuitType type;

	public PlayerQuitRequestEvent(@NotNull Player player, @NotNull List<Player> currentPlayers, QuitType type) {
		super(player, currentPlayers, type);
		this.type = type;
	}

	public QuitType getType() {
		return type;
	}
}

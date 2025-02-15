/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.event.player.requestChange;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.event.player.requestChange.type.JoinType;

import java.util.List;

public class PlayerJoinRequestEvent extends PlayerChangeRequestEvent {
	private final JoinType type;

	public PlayerJoinRequestEvent(@NotNull Player player, @NotNull List<Player> currentPlayers, JoinType type) {
		super(player, currentPlayers, type);
		this.type = type;
	}

	public JoinType getType() {
		return type;
	}
}

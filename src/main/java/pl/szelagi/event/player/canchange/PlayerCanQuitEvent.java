/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.event.player.canchange;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.event.player.canchange.type.QuitType;

import java.util.List;

public class PlayerCanQuitEvent extends PlayerCanChangeEvent {
	private final QuitType type;

	public PlayerCanQuitEvent(@NotNull Player player, @NotNull List<Player> currentPlayers, QuitType type) {
		super(player, currentPlayers, type);
		this.type = type;
	}

	public QuitType getType() {
		return type;
	}
}

/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller.otherGameMode;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import pl.szelagi.state.PlayerState;

public class GameModeState extends PlayerState {
	private final GameMode gameMode;

	public GameModeState(Player player) {
		super(player);
		gameMode = player.getGameMode();
	}

	public GameMode getGameMode() {
		return gameMode;
	}
}

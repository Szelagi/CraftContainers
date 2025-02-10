/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.grouper;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.component.session.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Group {
	private final List<Player> players = new ArrayList<>();

	public Group(List<Player> players) {
		this.players.addAll(players);
	}

	public @NotNull List<Player> players() {
		return players;
	}

	public @NotNull List<Player> inSessionPlayers(Session session) {
		return players.stream()
		              .filter(player -> session
				              .getPlayers()
				              .contains(player))
		              .collect(Collectors.toList());
	}

	public boolean hasPlayer(Player player) {
		return players.contains(player);
	}

	public int size() {
		return players.size();
	}

	public int inSessionSize(Session session) {
		return (int) players.stream()
		                    .filter(player -> session
				                    .getPlayers()
				                    .contains(player))
		                    .count();
	}


}

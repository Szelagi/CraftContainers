/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.recovery;

import org.bukkit.entity.Player;
import pl.szelagi.component.constructor.PlayerDestructorLambda;
import pl.szelagi.event.player.recovery.PlayerStateRecoveryEvent;
import pl.szelagi.manager.SessionManager;
import pl.szelagi.recovery.exception.RecoveryException;

import java.io.Serializable;
import java.util.ArrayList;

public final class PlayerRecovery implements Serializable {
	private final ArrayList<PlayerDestructorLambda> destructors;
	// Warning! Player UUID maybe can change!
	private final String playerAccountName;

	public PlayerRecovery(Player player) throws RecoveryException {
		var session = SessionManager.getSession(player);
		if (session == null)
			throw new RecoveryException("player is not in session");

		var event = new PlayerStateRecoveryEvent(player);
		session.getMainProcess()
		       .invokeReverseAllListeners(event);

		var lambdas = event.getLambdas()
		                   .getLambdas();

		var cloneLambdas = new ArrayList<>(lambdas);

		this.destructors = cloneLambdas;
		this.playerAccountName = player.getName();
	}

	public ArrayList<PlayerDestructorLambda> getDestructors() {
		return destructors;
	}

	public void run(Player player) throws RecoveryException {
		if (!playerAccountName.equals(player.getName()))
			throw new RecoveryException("player name is other than saved!");
		for (var destructor : destructors) {
			destructor.run(player);
		}
	}
}
/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.event.player.requestChange;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.cancelable.CanCancelable;
import pl.szelagi.cancelable.CancelCause;
import pl.szelagi.cancelable.CancelNotCancelableException;
import pl.szelagi.cancelable.Cancelable;
import pl.szelagi.event.SAPIEvent;

import java.util.List;

public abstract class PlayerChangeRequestEvent extends SAPIEvent implements Cancelable {
	private final @NotNull Player player;
	private final @NotNull List<Player> currentPlayers;
	private final @NotNull CanCancelable cancelable;
	private @Nullable CancelCause cancelCause;
	private boolean isCanceled;

	public PlayerChangeRequestEvent(@NotNull Player player, @NotNull List<Player> currentPlayers, @NotNull CanCancelable cancelable) {
		this.player = player;
		this.currentPlayers = currentPlayers;
		this.cancelable = cancelable;
		this.cancelCause = null;
		this.isCanceled = false;
	}

	public @NotNull Player getPlayer() {
		return player;
	}

	public @NotNull List<Player> getCurrentPlayers() {
		return currentPlayers;
	}

	public @Nullable CancelCause getCancelCause() {
		return cancelCause;
	}

	@Override
	public void setCanceled(CancelCause cause) throws CancelNotCancelableException {
		if (!isCancelable())
			throw new CancelNotCancelableException("");
		isCanceled = true;
		cancelCause = cause;
	}

	public boolean isCanceled() {
		return isCanceled;
	}

	@Override
	public boolean isCancelable() {
		return cancelable.isCancelable();
	}
}

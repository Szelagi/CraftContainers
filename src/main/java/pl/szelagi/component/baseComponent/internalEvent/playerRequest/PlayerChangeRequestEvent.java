/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.component.baseComponent.internalEvent.playerRequest;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.event.internal.InternalEvent;

import java.util.List;

public abstract class PlayerChangeRequestEvent extends InternalEvent {
    private final @NotNull Player player;
    private final @NotNull List<Player> currentPlayers;
    private @Nullable Reason reason;
    private boolean canceled;

    public PlayerChangeRequestEvent(@NotNull Player player, @NotNull List<Player> currentPlayers) {
        this.player = player;
        this.currentPlayers = currentPlayers;
        this.reason = null;
        this.canceled = false;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull List<Player> getCurrentPlayers() {
        return currentPlayers;
    }

    public @Nullable Reason getCancelCause() {
        return reason;
    }

    public void setCanceled(Reason cause) {
        canceled = true;
        reason = cause;
    }

    public boolean isCanceled() {
        return canceled;
    }

}

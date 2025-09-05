/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.event.internal.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PlayerDestructor extends PlayerEvent {
    private final @NotNull PlayerDestroyCause destroyCause;
    private final @Nullable PlayerChange playerChange;

    public PlayerDestructor(@NotNull Player player, @NotNull List<Player> allPlayers, @NotNull PlayerDestroyCause cause, @Nullable PlayerChange playerChange) {
        super(player, allPlayers);
        if (cause.isPlayerChange() && playerChange == null) {
            throw new IllegalArgumentException("Player cause is required for this cause");
        }
        if (!cause.isPlayerChange() && playerChange != null) {
            throw new IllegalArgumentException("Player cause is not required for this cause");
        }
        this.destroyCause = cause;
        this.playerChange = playerChange;
    }

    @Override
    public @Nullable PlayerChange playerChange() {
        return playerChange;
    }

    @Override
    public @NotNull PlayerDestroyCause cause() {
        return destroyCause;
    }
}

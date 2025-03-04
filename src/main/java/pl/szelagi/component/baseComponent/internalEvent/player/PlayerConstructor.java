/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.component.baseComponent.internalEvent.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PlayerConstructor extends PlayerEvent {
    private final @NotNull PlayerInitCause initCause;
    private final @Nullable PlayerChange playerChange;

    public PlayerConstructor(@NotNull Player player, @NotNull List<Player> allPlayers, @NotNull PlayerInitCause cause, @Nullable PlayerChange playerChange) {
        super(player, allPlayers);
        if (cause.isPlayerChange() && playerChange == null) {
            throw new IllegalArgumentException("Player cause is required for this cause");
        }
        if (!cause.isPlayerChange() && playerChange != null) {
            throw new IllegalArgumentException("Player cause is not required for this cause");
        }
        this.initCause = cause;
        this.playerChange = playerChange;
    }

    @Override
    public @Nullable PlayerChange playerChange() {
        return playerChange;
    }

    @Override
    public @NotNull PlayerInitCause cause() {
        return initCause;
    }
}
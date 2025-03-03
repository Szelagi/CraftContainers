/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.component.baseComponent.internalEvent.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerChangeDestructor extends PlayerDestructor implements PlayerChange {
    private final @NotNull List<Player> prevPlayers;
    private final @NotNull List<Player> newPlayers;
    private final @NotNull Cause cause;
    public PlayerChangeDestructor(@NotNull Player player, @NotNull List<Player> allPlayers, @NotNull List<Player> prevPlayers, @NotNull List<Player> newPlayers, Cause cause) {
        super(player, allPlayers);
        this.prevPlayers = prevPlayers;
        this.newPlayers = newPlayers;
        this.cause = cause;
    }

    @Override
    public @NotNull Cause cause() {
        return cause;
    }

    @Override
    public @NotNull List<Player> prevPlayers() {
        return prevPlayers;
    }

    @Override
    public @NotNull List<Player> newPlayers() {
        return newPlayers;
    }
}
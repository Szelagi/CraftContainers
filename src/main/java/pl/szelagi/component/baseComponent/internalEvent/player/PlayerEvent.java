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
import pl.szelagi.event.internal.InternalEvent;

import java.util.Collection;
import java.util.List;

public abstract class PlayerEvent extends InternalEvent {
    private final @NotNull Player player;
    private final @NotNull Collection<Player> allPlayers;

    public PlayerEvent(@NotNull Player player, @NotNull Collection<Player> allPlayers) {
        this.player = player;
        this.allPlayers = allPlayers;
    }

    @Deprecated
    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull Player player() {
        return player;
    }

    public @NotNull Collection<Player> allPlayers() {
        return allPlayers;
    }

    public @NotNull List<Player> otherPlayers() {
        return allPlayers.stream().filter(p -> !p.equals(player)).toList();
    }

    public abstract @Nullable PlayerChange playerChange();

    public abstract @NotNull Object cause();
}

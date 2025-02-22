/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.component.baseComponent.internalEvent.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PlayerConstructor extends PlayerChangeEvent {
    public PlayerConstructor(@NotNull Player player, @NotNull Collection<Player> otherSessionPlayers, @NotNull Collection<Player> allSessionPlayers, @NotNull InvokeType invokeType) {
        super(player, otherSessionPlayers, allSessionPlayers, invokeType);
    }
}

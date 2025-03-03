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

public class PlayerDestructor extends PlayerEvent {
    public PlayerDestructor(@NotNull Player player, @NotNull List<Player> allPlayers) {
        super(player, allPlayers);
    }
}

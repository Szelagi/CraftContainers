/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.state;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.function.Function;

public class PlayerContainer<T extends PlayerState> extends Container<Player, T> implements Serializable {
    public PlayerContainer(@NotNull Function<Player, T> creator) {
        super(creator);
    }
}

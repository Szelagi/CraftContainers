/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.state;

import org.bukkit.entity.Player;

import java.io.Serializable;

public abstract class PlayerState implements Serializable {
    private transient final Player player;

    public PlayerState(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}

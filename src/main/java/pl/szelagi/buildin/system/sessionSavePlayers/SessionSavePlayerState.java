/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.system.sessionSavePlayers;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import pl.szelagi.bukkitAdapted.LocationAdapted;
import pl.szelagi.state.PlayerState;

import java.io.Serializable;

public class SessionSavePlayerState extends PlayerState implements Serializable {
    private final GameMode gameMode;
    private final LocationAdapted locationAdapted;
    private final float fallDistance;
    private final int fireTicks;

    public SessionSavePlayerState(Player player) {
        super(player);
        this.gameMode = getPlayer().getGameMode();
        this.locationAdapted = new LocationAdapted(getPlayer().getLocation());
        this.fallDistance = getPlayer().getFallDistance();
        this.fireTicks = getPlayer().getFireTicks();
    }

    public void load(Player player) {
        player.setGameMode(gameMode);
        player.teleport(locationAdapted.getLocation());
        player.setFallDistance(fallDistance);
        player.setFireTicks(fireTicks);
    }
}

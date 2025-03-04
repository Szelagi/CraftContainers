/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller.otherSpeed;

import org.bukkit.entity.Player;
import pl.szelagi.state.PlayerState;

public class OtherSpeedState extends PlayerState {
    private final float walkSpeed;
    private final float flySpeed;

    public OtherSpeedState(Player player) {
        super(player);
        walkSpeed = player.getWalkSpeed();
        flySpeed = player.getFlySpeed();
    }

    public float walkSpeed() {
        return walkSpeed;
    }

    public float flySpeed() {
        return flySpeed;
    }

    public void load(Player player) {
        player.setWalkSpeed(walkSpeed);
        player.setFlySpeed(flySpeed);
    }
}

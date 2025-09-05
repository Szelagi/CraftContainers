/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.event.internal.player;

public enum PlayerInitCause {
    COMPONENT_INIT(false), PLAYER_JOIN(true);

    private final boolean isPlayerChange;

    PlayerInitCause(boolean isPlayerChange) {
        this.isPlayerChange = isPlayerChange;
    }

    public boolean isPlayerChange() {
        return isPlayerChange;
    }
}

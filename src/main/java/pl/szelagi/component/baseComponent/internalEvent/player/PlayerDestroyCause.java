/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.component.baseComponent.internalEvent.player;

public enum PlayerDestroyCause {
    COMPONENT_DESTROY(false), PLAYER_QUIT(true), SESSION_DESTROY(true);

    private final boolean isPlayerChange;
    PlayerDestroyCause(boolean isPlayerChange) {
        this.isPlayerChange = isPlayerChange;
    }

    public boolean isPlayerChange() {
        return isPlayerChange;
    }
}

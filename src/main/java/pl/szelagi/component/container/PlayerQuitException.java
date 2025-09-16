/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.component.container;

import pl.szelagi.util.ServerRuntimeException;

public class PlayerQuitException extends ServerRuntimeException {
    public PlayerQuitException(String name) {
        super(name);
    }
}

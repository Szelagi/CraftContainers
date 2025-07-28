/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package e2e;

public class RemoteTestFailed extends RuntimeException {
    public RemoteTestFailed(String message) {
        super("Remote test failure:\n" + ((message == null || message.isBlank()) ? "(empty message)" : message));
    }
}

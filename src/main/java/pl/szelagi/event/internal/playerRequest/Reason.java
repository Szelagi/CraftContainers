/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.event.internal.playerRequest;

import org.jetbrains.annotations.NotNull;
import pl.szelagi.component.base.Component;

public record Reason(
        @NotNull Component invokeComponent,
        @NotNull String message) {
    @Override
    public String toString() {
        return "CancelCause{" + "invokeComponent=" + invokeComponent + ", message='" + message + '\'' + '}';
    }
}

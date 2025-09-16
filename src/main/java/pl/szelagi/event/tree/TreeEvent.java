/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.event.tree;

import java.util.UUID;

public abstract class TreeEvent {
    private final UUID uuid = UUID.randomUUID();

    public UUID getUuid() {
        return uuid;
    }
}

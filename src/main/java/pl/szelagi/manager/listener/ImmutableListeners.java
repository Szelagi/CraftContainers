/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.manager.listener;

import org.bukkit.event.Listener;

import java.util.Set;

public interface ImmutableListeners {
    Set<Class<? extends Listener>> get();
}

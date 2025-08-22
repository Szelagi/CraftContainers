/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.space;

import org.bukkit.Bukkit;
import pl.szelagi.SessionAPI;

import java.util.Set;

public interface ISpaceAllocator {
    IAllocate allocate();
    void deallocate(IAllocate allocate);
    boolean isAllocated(IAllocate allocate);
    Set<IAllocate> allocatedSpaces();
    void initialize();
}
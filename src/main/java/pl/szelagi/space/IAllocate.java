/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.space;

import pl.szelagi.spatial.ISpatial;

public interface IAllocate extends ISpatial {
    ISpaceAllocator getAllocator();

    default boolean isAllocated() {
        return getAllocator().isAllocated(this);
    }

    default void deallocate() {
        getAllocator().deallocate(this);
    }
}

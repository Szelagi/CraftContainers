/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.tree;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IHierarchical<T extends IHierarchical<T>> {
    @Nullable T parent();
    List<T> children();
}

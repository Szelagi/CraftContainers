/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.test.sessionAddRemovePlayer;

import org.jetbrains.annotations.NotNull;
import pl.szelagi.component.GameMap;
import pl.szelagi.component.container.Container;

class MyGameMap extends GameMap {
    public MyGameMap(@NotNull Container container) {
        super(container);
    }

    @Override
    protected void generate() {

    }

    @Override
    protected void degenerate() {

    }
}

/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.test.treeProcessingTest;

import org.jetbrains.annotations.NotNull;
import pl.szelagi.component.board.Board;
import pl.szelagi.spatial.ISpatial;
import pl.szelagi.spatial.Spatial;

class B extends Board {
    public B(@NotNull S session) {
        super(session);
    }

    @Override
    public ISpatial defineSecureZone() {
        return new Spatial(space().getFirstPoint(), space().getSecondPoint());
    }

}

/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.test.sessionAddRemovePlayer;

import org.jetbrains.annotations.NotNull;
import pl.szelagi.component.Board;
import pl.szelagi.component.session.Session;
import pl.szelagi.spatial.ISpatial;
import pl.szelagi.spatial.Spatial;

class MyBoard extends Board {
    public MyBoard(@NotNull Session session) {
        super(session);
    }

    @Override
    protected void generate() {

    }

    @Override
    protected void degenerate() {

    }
}

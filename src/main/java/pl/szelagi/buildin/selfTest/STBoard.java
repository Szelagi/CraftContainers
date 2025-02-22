/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.selfTest;

import org.bukkit.Material;
import pl.szelagi.Scheduler;
import pl.szelagi.component.board.Board;
import pl.szelagi.component.session.Session;
import pl.szelagi.spatial.ISpatial;
import pl.szelagi.spatial.Spatial;

public class STBoard extends Board {
    public STBoard(Session session) {
        super(session);
    }

    @Override
    protected void generate() {
        Scheduler.runAndWait(() -> {
            center().getBlock()
                    .setType(Material.BEDROCK);
        });
    }

    @Override
    public ISpatial defineSecureZone() {
        return new Spatial(center(), center());
    }

    @Override
    protected void degenerate() {
        Scheduler.runAndWait(() -> {
            center().getBlock()
                    .setType(Material.AIR);
        });
    }
}

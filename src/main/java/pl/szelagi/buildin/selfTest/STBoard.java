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

public class STBoard extends Board {
    public STBoard(Session session) {
        super(session);
    }

    @Override
    protected void generate() {
        Scheduler.runAndWait(() -> {
            getBase().getBlock()
                    .setType(Material.BEDROCK);
        });
        setSecureZone(getSpace());
    }

    @Override
    protected void degenerate() {
        Scheduler.runAndWait(() -> {
            getBase().getBlock()
                    .setType(Material.AIR);
        });
    }
}

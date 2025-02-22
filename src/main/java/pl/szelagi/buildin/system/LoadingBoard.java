/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.system;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import pl.szelagi.SessionAPI;
import pl.szelagi.component.board.Board;
import pl.szelagi.component.session.Session;
import pl.szelagi.spatial.ISpatial;
import pl.szelagi.spatial.Spatial;

public class LoadingBoard extends Board {
    public LoadingBoard(Session session) {
        super(session);
    }

    @Override
    protected void generate() {
        var centerBlock = space().getCenter()
                .getBlock();
        centerBlock.setType(Material.BEDROCK);
    }

    @Override
    protected void degenerate() {
        var centerBlock = space().getCenter()
                .getBlock();
        Bukkit.getScheduler()
                .runTask(SessionAPI.instance(), () -> {
                    centerBlock.setType(Material.AIR);
                });
    }

    @Override
    public ISpatial defineSecureZone() {
        return new Spatial(center(), center());
    }
}

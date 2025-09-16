/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.test.sessionStartStopTest;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.component.GameMap;
import pl.szelagi.component.container.Container;

class MyContainer extends Container {
    public MyContainer(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    protected @NotNull GameMap defaultBoard() {
        return new MyGameMap(this);
    }
}

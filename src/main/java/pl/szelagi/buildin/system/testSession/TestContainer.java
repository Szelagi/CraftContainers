/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.system.testSession;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.buildin.controller.otherEquipment.OtherEquipment;
import pl.szelagi.component.container.Container;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.component.GameMap;

public class TestContainer extends Container {
    public TestContainer(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    protected @NotNull GameMap defaultGameMap() {
        return new TestGameMap(this);
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        new OtherEquipment(this).start();
    }

}

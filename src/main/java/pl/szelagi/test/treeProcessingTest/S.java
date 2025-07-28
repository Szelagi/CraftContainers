/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.test.treeProcessingTest;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.component.baseComponent.internalEvent.component.ComponentConstructor;
import pl.szelagi.component.board.Board;
import pl.szelagi.component.session.Session;

import java.util.ArrayList;
import java.util.List;

class S extends Session {
    public final List<String> componentConstructors = new ArrayList<>();
    public final List<String> componentDestructors = new ArrayList<>();
    public final List<String> playerConstructors = new ArrayList<>();
    public final List<String> playerDestructors = new ArrayList<>();
    public final List<String> playerJoinRequest = new ArrayList<>();

    public S(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    protected @NotNull Board defaultBoard() {
        return new B(this);
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        new C1(this).start();
        new C4(this).start();
        new C8(this).start();
    }
}

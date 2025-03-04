/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.system.testSession;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.buildin.InactiveDelayClose;
import pl.szelagi.buildin.ZeroPlayerClose;
import pl.szelagi.buildin.controller.otherEquipment.OtherEquipment;
import pl.szelagi.component.baseComponent.internalEvent.component.ComponentConstructor;
import pl.szelagi.component.baseComponent.internalEvent.component.ComponentDestructor;
import pl.szelagi.component.board.Board;
import pl.szelagi.component.session.Session;

public class TestSession extends Session {
	public TestSession(JavaPlugin plugin) {
		super(plugin);
	}

	@Override
	protected @NotNull Board defaultBoard() {
		return new TestBoard(this);
	}

	@Override
	public void onComponentInit(ComponentConstructor event) {
		super.onComponentInit(event);
		new OtherEquipment(this).start();
	}

}

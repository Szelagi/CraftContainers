/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.creator;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.component.board.Board;
import pl.szelagi.component.session.Session;

public class Creator extends Session {
	private final String mainDirectory;
	private boolean isRecording = false;

	public Creator(JavaPlugin plugin, String mainDirectory) {
		super(plugin);
		this.mainDirectory = mainDirectory;
	}

	@NotNull
	@Override
	protected Board getDefaultStartBoard() {
		return new CreatorBoard(this, mainDirectory);
	}

	@NotNull
	@Override
	public String getName() {
		return "creatorSession";
	}

	public boolean isRecording() {
		return isRecording;
	}

	public void setRecording(boolean recording) {
		isRecording = recording;
	}
}

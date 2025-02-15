/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.event.player.requestChange.type;

import pl.szelagi.cancelable.CanCancelable;

public enum QuitType implements CanCancelable {
	PLUGIN(true), PLUGIN_FORCE(false), DISCONNECT(false), SESSION_STOP(false);
	private final boolean isCancelable;

	QuitType(boolean isCancelable) {
		this.isCancelable = isCancelable;
	}

	public boolean isCancelable() {
		return isCancelable;
	}
}

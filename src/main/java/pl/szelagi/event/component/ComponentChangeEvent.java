/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.event.component;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.component.BaseComponent;
import pl.szelagi.event.SAPIEvent;

import java.util.Collection;
import java.util.List;

public abstract class ComponentChangeEvent extends SAPIEvent {
	private final @NotNull BaseComponent component;
	private final @Nullable BaseComponent parentComponent;
	private final @NotNull List<Player> currentPlayers;

	public ComponentChangeEvent(@NotNull BaseComponent component, @NotNull List<Player> currentPlayers) {
		this.component = component;
		if (component.getParentProcess() != null) {
			this.parentComponent = component
					.getParentProcess()
					.getComponent();
		} else {
			this.parentComponent = null;
		}
		this.currentPlayers = currentPlayers;
	}

	public @NotNull BaseComponent getComponent() {
		return component;
	}

	public @Nullable BaseComponent getParentComponent() {
		return parentComponent;
	}

	public @NotNull Collection<Player> getCurrentPlayers() {
		return currentPlayers;
	}
}

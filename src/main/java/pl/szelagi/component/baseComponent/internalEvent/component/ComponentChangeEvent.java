/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.component.baseComponent.internalEvent.component;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.event.internal.InternalEvent;

import java.util.Collection;

public abstract class ComponentChangeEvent extends InternalEvent {
    private final @NotNull BaseComponent component;

    public ComponentChangeEvent(@NotNull BaseComponent component) {
        this.component = component;
    }

    public @NotNull BaseComponent component() {
        return component;
    }

    @Deprecated
    public @NotNull BaseComponent getComponent() {
        return component;
    }

    @Deprecated
    public @Nullable BaseComponent getParentComponent() {
        return component.parent();
    }

    @Deprecated
    public @NotNull Collection<Player> getCurrentPlayers() {
        return component.players();
    }
}

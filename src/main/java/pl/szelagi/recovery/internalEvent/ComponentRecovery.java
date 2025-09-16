/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.recovery.internalEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import pl.szelagi.component.base.Component;
import pl.szelagi.event.internal.InternalEvent;
import pl.szelagi.recovery.ComponentRecoveryLambda;

import java.util.HashMap;
import java.util.HashSet;

public class ComponentRecovery extends InternalEvent {
    private final @NotNull ComponentRecoveryCause cause;
    private final @NotNull HashMap<Component, HashSet<ComponentRecoveryLambda>> componentDestroyRecoveries;

    public ComponentRecovery(@NotNull ComponentRecoveryCause cause) {
        this.cause = cause;
        this.componentDestroyRecoveries = new HashMap<>();
    }

    public void register(@NotNull Component component, @NotNull ComponentRecoveryLambda lambda) {
        var recoveries = componentDestroyRecoveries.computeIfAbsent(component, k -> new HashSet<>());
        recoveries.add(lambda);
    }

    @Unmodifiable
    @NotNull
    public HashMap<Component, HashSet<ComponentRecoveryLambda>> componentDestroyRecoveries() {
        return componentDestroyRecoveries;
    }

    @NotNull
    public ComponentRecoveryCause cause() {
        return cause;
    }
}

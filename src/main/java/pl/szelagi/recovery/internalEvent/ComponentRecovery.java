/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.recovery.internalEvent;

import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.event.internal.InternalEvent;
import pl.szelagi.recovery.ComponentRecoveryLambda;

import java.util.HashMap;
import java.util.HashSet;

public class ComponentRecovery extends InternalEvent {
    private final HashMap<BaseComponent, HashSet<ComponentRecoveryLambda>> componentDestroyRecoveries = new HashMap<>();

    public void register(BaseComponent component, ComponentRecoveryLambda lambda) {
        var recoveries = componentDestroyRecoveries.computeIfAbsent(component, k -> new HashSet<>());
        recoveries.add(lambda);
    }

    public HashMap<BaseComponent, HashSet<ComponentRecoveryLambda>> componentDestroyRecoveries() {
        return componentDestroyRecoveries;
    }

}

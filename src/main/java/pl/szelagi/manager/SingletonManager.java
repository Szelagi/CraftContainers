/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.manager;

import org.jetbrains.annotations.NotNull;
import pl.szelagi.annotation.SingletonComponent;
import pl.szelagi.component.base.Component;

import java.util.HashMap;
import java.util.Map;

public class SingletonManager {
    private static final Map<Class<? extends Component>, Boolean> IS_SINGLETON = new HashMap<>();

    public static boolean isSingleton(Class<? extends Component> componentClass) {
        return IS_SINGLETON.computeIfAbsent(
                componentClass,
                k -> componentClass.isAnnotationPresent(SingletonComponent.class));
    }

    public static void check(@NotNull Component component) {
        boolean isSingleton = isSingleton(component.getClass());
        boolean cardinalityMoreThanOne = CardinalityManager.cardinality(component.container(), component.getClass()) > 1;
        if (isSingleton && cardinalityMoreThanOne)
            throw new SingletonComponentException(component);
    }
}

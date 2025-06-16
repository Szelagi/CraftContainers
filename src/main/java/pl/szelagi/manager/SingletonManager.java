/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.manager;

import org.jetbrains.annotations.NotNull;
import pl.szelagi.annotation.SingletonComponent;
import pl.szelagi.component.baseComponent.BaseComponent;

import java.util.HashMap;
import java.util.Map;

public class SingletonManager {
    private static final Map<Class<? extends BaseComponent>, Boolean> IS_SINGLETON = new HashMap<>();

    public static boolean isSingleton(BaseComponent component) {
        return IS_SINGLETON.computeIfAbsent(
                component.getClass(),
                k -> component.getClass().isAnnotationPresent(SingletonComponent.class));
    }

    public static void check(@NotNull BaseComponent baseComponent) {
        boolean isSingleton = isSingleton(baseComponent);
        boolean cardinalityMoreThanOne = CardinalityManager.cardinality(baseComponent.session(), baseComponent.getClass()) > 1;
        if (isSingleton && cardinalityMoreThanOne)
            throw new SingletonComponentException("An instance of " + baseComponent.name() +
                    " exists more than once in the same session (" +
                    baseComponent.session().name() + ").");
    }
}

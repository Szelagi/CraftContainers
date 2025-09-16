/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.manager;
import pl.szelagi.annotation.Dependency;
import pl.szelagi.component.base.Component;
import pl.szelagi.component.container.Container;

import java.util.*;
import java.util.stream.Collectors;

public class DependencyManager {
    private static final Map<Container, Map<Class<? extends Component>, Set<Class<? extends Component>>>> DEPENDENCY_USES = new HashMap<>();

    public static Set<Class<? extends Component>> getDependencies(Class<? extends Component> componentClass) {
        var dependencies = componentClass.getAnnotationsByType(Dependency.class);
        return Arrays.stream(dependencies).map(Dependency::component).collect(Collectors.toSet());
    }

    public static void componentStart(Component component) {
        // utwórz HashMap dla nowej sesji
        if (component instanceof Container container) {
            if (DEPENDENCY_USES.containsKey(container)) throw new IllegalArgumentException("session already started");
            DEPENDENCY_USES.put(container, new HashMap<>());
        }

        // pobierz kontekst konkretnej sesji
        var context = DEPENDENCY_USES.get(component.container());
        if (context == null) throw new IllegalArgumentException("session does not have dependencies set");

        // Podczas startu (czy istnieją zależności)
        for (var dependency : getDependencies(component.getClass())) {
            var has = CardinalityManager.cardinality(component.container(), dependency) > 0;
            if (!has) throw new DependencyComponentException(
                    "Missing required dependency: " + dependency.getSimpleName() +
                            " for component " + component.name()
            );

            var uses = context.computeIfAbsent(dependency, k -> new HashSet<>());
            uses.add(component.getClass());
        }
    }

    public static void componentStop(Component component) {
        if (component instanceof Container container) {
            DEPENDENCY_USES.remove(container);
            return;
        }

        var context = DEPENDENCY_USES.get(component.container());
        if (context == null) throw new IllegalArgumentException("session does not have dependencies set");

        // przypadek, kiedy wyłącza się komponent, który używa zależności
        var isLast = CardinalityManager.cardinality(component.container(), component.getClass()) == 0;
        if (isLast) {
            for (var dependency : getDependencies(component.getClass())) {
                var uses = context.get(dependency);
                if (uses == null) throw new IllegalStateException("No dependency found for component " + component.name());
                uses.remove(component.getClass());
            }
        }

        // kiedy, zależność wyłącza się
        var uses = context.get(component.getClass());
        if (uses == null) return;
        if (uses.isEmpty()) {
            context.remove(component.getClass());
        } else {
            var names = uses.stream().map(Class::getSimpleName).toList();
            var textNames = String.join(", ", names);
            throw new DependencyComponentException(
                    "Cannot disable dependency " + component.name() +
                            " because it is used by the following components: " + textNames
            );
        }



    }


}

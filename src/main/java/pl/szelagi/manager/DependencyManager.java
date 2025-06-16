/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.manager;
import pl.szelagi.annotation.Dependency;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.component.session.Session;

import java.util.*;
import java.util.stream.Collectors;

public class DependencyManager {
    private static final Map<Session, Map<Class<? extends BaseComponent>, Set<Class<? extends BaseComponent>>>> DEPENDENCY_USES = new HashMap<>();

    public static Set<Class<? extends BaseComponent>> getDependencies(Class<? extends BaseComponent> componentClass) {
        var dependencies = componentClass.getAnnotationsByType(Dependency.class);
        return Arrays.stream(dependencies).map(Dependency::component).collect(Collectors.toSet());
    }

    public static void componentStart(BaseComponent baseComponent) {
        // utwórz HashMap dla nowej sesji
        if (baseComponent instanceof Session session) {
            if (DEPENDENCY_USES.containsKey(session)) throw new IllegalArgumentException("session already started");
            DEPENDENCY_USES.put(session, new HashMap<>());
        }

        // pobierz kontekst konkretnej sesji
        var context = DEPENDENCY_USES.get(baseComponent.session());
        if (context == null) throw new IllegalArgumentException("session does not have dependencies set");

        // Podczas startu (czy istnieją zależności)
        for (var dependency : getDependencies(baseComponent.getClass())) {
            var has = CardinalityManager.cardinality(baseComponent.session(), dependency) > 0;
            if (!has) throw new DependencyComponentException(
                    "Missing required dependency: " + dependency.getSimpleName() +
                            " for component " + baseComponent.name()
            );

            var uses = context.computeIfAbsent(dependency, k -> new HashSet<>());
            uses.add(baseComponent.getClass());
        }
    }

    public static void componentStop(BaseComponent baseComponent) {
        if (baseComponent instanceof Session session) {
            DEPENDENCY_USES.remove(session);
            return;
        }

        var context = DEPENDENCY_USES.get(baseComponent.session());
        if (context == null) throw new IllegalArgumentException("session does not have dependencies set");

        // przypadek, kiedy wyłącza się komponent, który używa zależności
        var isLast = CardinalityManager.cardinality(baseComponent.session(), baseComponent.getClass()) == 0;
        if (isLast) {
            for (var dependency : getDependencies(baseComponent.getClass())) {
                var uses = context.get(dependency);
                if (uses == null) throw new IllegalStateException("No dependency found for component " + baseComponent.name());
                uses.remove(baseComponent.getClass());
            }
        }

        // kiedy, zależność wyłącza się
        var uses = context.get(baseComponent.getClass());
        if (uses == null) return;
        if (uses.isEmpty()) {
            context.remove(baseComponent.getClass());
        } else {
            var names = uses.stream().map(Class::getSimpleName).toList();
            var textNames = String.join(", ", names);
            throw new DependencyComponentException(
                    "Cannot disable dependency " + baseComponent.name() +
                            " because it is used by the following components: " + textNames
            );
        }



    }


}

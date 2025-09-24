/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.manager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.component.base.Component;
import pl.szelagi.component.GameMap;
import pl.szelagi.component.Controller;
import pl.szelagi.component.container.Container;
import pl.szelagi.event.tree.TreeEvent;
import pl.szelagi.event.tree.TreeListener;
import pl.szelagi.tree.DepthFirstSearch;
import pl.szelagi.util.PluginRegistry;
import pl.szelagi.util.ReflectionRecursive;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ComponentManager {
    // CACHE SAPI EVENT
    private static final Map<Class<? extends TreeListener>, Collection<Method>> CLASS_LISTENERS = new HashMap<>();
    private static final Map<Class<? extends TreeListener>, Map<Class<? extends TreeEvent>, Collection<Method>>> CLASS_TYPED_LISTENERS = new HashMap<>();
    public static Map<Class<? extends Component>, String> COMPONENT_TO_NAME = new HashMap<>();

    // SAPI EVENT METHODS
    private static Collection<Method> listeners(Class<? extends TreeListener> listener) {
        return CLASS_LISTENERS.computeIfAbsent(listener, ReflectionRecursive::getSAPIHandlerMethods);
    }

    public static Collection<Method> listeners(Class<? extends TreeListener> listener, Class<? extends TreeEvent> event) {
        return CLASS_TYPED_LISTENERS
                .computeIfAbsent(listener, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(event, e -> {
                    var set = new HashSet<Method>();
                    for (var method : listeners(listener)) {
                        if (method.getParameterTypes()[0].equals(event)) {
                            set.add(method);
                        }
                    }
                    return set;
                });

    }

    // IDENTIFICATION METHODS
    private static char componentTypeChar(Class<? extends Component> component) {
        if (Controller.class.isAssignableFrom(component)) {
            return 'C';
        } else if (GameMap.class.isAssignableFrom(component)) {
            return 'B';
        } else if (Container.class.isAssignableFrom(component)) {
            return 'S';
        }
        return component.getSimpleName().charAt(0);
    }

    public static String componentName(Class<? extends Component> component) {
        return COMPONENT_TO_NAME.computeIfAbsent(component, c -> {
            var currentJarFile = new File(component
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getFile());
            var plugin = PluginRegistry.getPlugin(currentJarFile.getName());
            var pluginName = plugin != null ? plugin.getName() : currentJarFile.getName();
            return c.getSimpleName() + componentTypeChar(c) + '#' + pluginName;
        });
    }

    public static String componentIdentifier(Component component) {
        return component.name() + ':' + component.id();
    }

}

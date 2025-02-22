/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.manager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.component.board.Board;
import pl.szelagi.component.controller.Controller;
import pl.szelagi.component.session.Session;
import pl.szelagi.event.sapi.SAPIEvent;
import pl.szelagi.event.sapi.SAPIListener;
import pl.szelagi.util.PluginRegistry;
import pl.szelagi.util.ReflectionRecursive;
import pl.szelagi.util.TreeAnalyzer;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ComponentManager {
    // CACHE SAPI EVENT
    private static final Map<Class<? extends SAPIListener>, Collection<Method>> CLASS_LISTENERS = new HashMap<>();
    private static final Map<Class<? extends SAPIListener>, Map<Class<? extends SAPIEvent>, Collection<Method>>> CLASS_TYPED_LISTENERS = new HashMap<>();
    public static Map<Class<? extends BaseComponent>, String> COMPONENT_TO_NAME = new HashMap<>();

    // SAPI EVENT METHODS
    private static Collection<Method> listeners(Class<? extends SAPIListener> listener) {
        return CLASS_LISTENERS.computeIfAbsent(listener, ReflectionRecursive::getSAPIHandlerMethods);
    }

    public static Collection<Method> listeners(Class<? extends SAPIListener> listener, Class<? extends SAPIEvent> event) {
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
    private static char componentTypeChar(Class<? extends BaseComponent> component) {
        if (Controller.class.isAssignableFrom(component)) {
            return 'C';
        } else if (Board.class.isAssignableFrom(component)) {
            return 'B';
        } else if (Session.class.isAssignableFrom(component)) {
            return 'S';
        }
        return component.getSimpleName().charAt(0);
    }

    public static String componentName(Class<? extends BaseComponent> component) {
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

    public static String componentIdentifier(BaseComponent component) {
        return component.name() + ':' + component.id();
    }

    public static @NotNull <T extends Controller> List<T> components(@Nullable Session session, @NotNull Class<T> clazz) {
        var analyze = new TreeAnalyzer(session);
        return analyze.layers().values().stream()
                .flatMap(List::stream)
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .toList();
    }

    public static @Nullable <T extends Controller> T firstComponent(@Nullable Session session, @NotNull Class<T> clazz) {
        var analyze = new TreeAnalyzer(session);
        return analyze.layers().values().stream()
                .flatMap(List::stream)
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .findFirst().orElse(null);
    }

}

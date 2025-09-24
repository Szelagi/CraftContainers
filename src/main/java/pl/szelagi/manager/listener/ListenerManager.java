/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.manager.listener;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.CraftContainers;
import pl.szelagi.component.base.Component;
import pl.szelagi.component.container.Container;

import java.util.*;
import java.util.function.Consumer;

public class ListenerManager {
    // Przechowuje instancje Listener, dla konkretnej klasy.
    // Używany do znalezienia aktualnej instancji Listener dla konkretnej klasy.
    // Potrzebne, aby od rejestrować listener.
    private static final Map<Class<? extends Listener>, Listener> ENABLE_LISTENERS = new HashMap<>();

    // Przechowuje, jakie ID BaseComponent aktualnie korzystają z konkretnej klasy Listener
    // Używany, aby śledzić, jakie Listener są aktualnie używane i przez jakie ID BaseComponent.
    // Potrzebne, aby wykryć, kiedy dany listener jest nieużywany i go od wyrejestrować.
    private static final Map<Class<? extends Listener>, HashSet<Long>> LISTENER_TO_COMPONENT_IDS = new HashMap<>();

    // Przechowuje jakie komponenty używają konkretnego Listener na konkretnej Session.
    // Używany, aby śledzić, które BaseComponent korzysta z konkretnego Listener na danej Session.
    // Dzięki temu nie musimy ciągle analizować drzewa sesji w poszukiwaniu odpowiadających BaseComponent.
    private static final Map<Pair<Container, Class<? extends Listener>>, LinkedHashSet<Component>> SESSION_LISTENER_TO_COMPONENTS = new HashMap<>();

    // BUKKIT LISTENER METHODS
    public static void onComponentStart(Component component) {
        var listeners = component.listeners();
        for (var listener : listeners.get()) {
            trackingStart(component, listener);
            listenerStart(component, listener);
        }
    }

    public static void onComponentStop(Component component) {
        var listeners = component.listeners();
        for (var listener : listeners.get()) {
            listenerStop(component, listener);
            trackingStop(component, listener);
        }
    }

    private static void listenerStart(Component component, Class<? extends Listener> listener) {
        var ids = LISTENER_TO_COMPONENT_IDS.computeIfAbsent(listener, k -> new HashSet<>());
        ids.add(component.id());

        ENABLE_LISTENERS.computeIfAbsent(listener, k -> {
            try {
                var plugin = CraftContainers.instance();
                var constructor = k.getDeclaredConstructor();
                constructor.setAccessible(true);
                var instance = constructor.newInstance();
                Bukkit.getPluginManager().registerEvents(instance, plugin);
                return instance;
            } catch (Exception e) {
                throw new IllegalStateException("Failed to create a new instance of listener '" + k.getName() + "' for component '" + component.name() + "'. Ensure the listener has a no-argument constructor and is properly registered.", e);
            }
        });
    }

    private static void listenerStop(Component component, Class<? extends Listener> listenerClass) {
        var ids = LISTENER_TO_COMPONENT_IDS.get(listenerClass);
        if (ids == null) {
            throw new IllegalStateException("No component IDs found for listener class '" + listenerClass.getName() + "' while stopping '" + component.name() + "'. The listener may not be properly initialized.");
        }
        ids.remove(component.id());
        if (!ids.isEmpty()) return;

        var listenerInstance = ENABLE_LISTENERS.get(listenerClass);
        if (listenerInstance == null) {
            throw new IllegalStateException("Listener instance not found for class '" + listenerClass.getName() + "' while stopping '" + component.name() + "'. Ensure the listener was properly registered.");
        }
        ENABLE_LISTENERS.remove(listenerClass);
        HandlerList.unregisterAll(listenerInstance);
    }

    private static void trackingStart(Component component, Class<? extends Listener> listener) {
        var pair = sessionListenerPair(component.container(), listener);
        var components = SESSION_LISTENER_TO_COMPONENTS.computeIfAbsent(pair, k -> {
            return new LinkedHashSet<>();
        });
        components.add(component);
    }

    private static void trackingStop(Component component, Class<? extends Listener> listener) {
        var session = component.container();
        var pair = sessionListenerPair(session, listener);
        var components = SESSION_LISTENER_TO_COMPONENTS.get(pair);
        if (components == null) {
            throw new IllegalStateException("Cannot remove component '" + component.name() + "' from session '" + session.name() + "' because no components were found for listener class '" + listener.getName() + "'.");
        }
        components.remove(component);

        if (components.isEmpty()) {
            SESSION_LISTENER_TO_COMPONENTS.remove(pair);
        }
    }

    // OPERATE
    private static Pair<Container, Class<? extends Listener>> sessionListenerPair(Container container, Class<? extends Listener> listenerClass) {
        return Pair.of(container, listenerClass);
    }

    protected static @Nullable Component findComponent(@Nullable Container container, Class<? extends Listener> listenerClass) {
        if (container == null) return null;
        var pair = sessionListenerPair(container, listenerClass);
        var components = SESSION_LISTENER_TO_COMPONENTS.get(pair);
        if (components == null) return null;
        if (components.isEmpty()) return null;

        // nie jest potrzebne
//        if (components == null) {
//            throw new IllegalStateException("No components found for session '" + session.name() + "' and listenerClass class '" + listenerClass.getName() + "'. Ensure the listenerClass is registered.");
//        }
//        if (components.isEmpty()) {
//            throw new IllegalStateException("Expected at least one component, but none were found for session '" + session.name() + "' and listenerClass class '" + listenerClass.getName() + "'.");
//        }
        return components.getFirst();
    }

    protected static @Nullable LinkedHashSet<Component> findComponents(@Nullable Container container, Class<? extends Listener> listenerClass) {
        if (container == null) return null;
        var pair = sessionListenerPair(container, listenerClass);
        var components = SESSION_LISTENER_TO_COMPONENTS.get(pair);
        //        if (components == null) {
//            throw new IllegalStateException("No components found for session '" + session.name() + "' and listenerClass class '" + listenerClass.getName() + "'. Ensure the listenerClass is registered correctly.");
//        }
        return components;
    }

    public static void first(@Nullable Container container, Class<? extends Listener> listenerClass, Consumer<Component> action) {
        var component = findComponent(container, listenerClass);
        if (component == null) return;
        action.accept(component);
    }

    public static <T extends Component> void first(@Nullable Container container, Class<? extends Listener> listenerClass, Class<T> componentClass, Consumer<T> action) {
        var components = findComponents(container, listenerClass);
        if (components == null) return;

        var typedComponent = components.stream()
                .filter(componentClass::isInstance)
                .map(componentClass::cast)
                .findFirst()
                .orElse(null);

        if (typedComponent == null) return;
        action.accept(typedComponent);
    }











}

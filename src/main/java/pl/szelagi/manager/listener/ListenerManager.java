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
import pl.szelagi.SessionAPI;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.component.session.Session;

import java.util.*;
import java.util.function.Consumer;

public class ListenerManager implements Listener {
    // Przechowuje instancje Listener, dla konkretnej klasy.
    // Używany do znalezienia aktualnej instancji Listener dla konkretnej klasy.
    // Potrzebne, aby od rejestrować listener.
    private static final HashMap<Class<? extends Listener>, Listener> ENABLE_LISTENERS = new HashMap<>();

    // Przechowuje, jakie ID BaseComponent aktualnie korzystają z konkretnej klasy Listener
    // Używany, aby śledzić, jakie Listener są aktualnie używane i przez jakie ID BaseComponent.
    // Potrzebne, aby wykryć, kiedy dany listener jest nieużywany i go od wyrejestrować.
    private static final HashMap<Class<? extends Listener>, HashSet<Long>> LISTENER_TO_COMPONENT_IDS = new HashMap<>();

    // Przechowuje jakie komponenty używają konkretnego Listener na konkretnej Session.
    // Używany, aby śledzić, które BaseComponent korzysta z konkretnego Listener na danej Session.
    // Dzięki temu nie musimy ciągle analizować drzewa sesji w poszukiwaniu odpowiadających BaseComponent.
    private static final HashMap<Pair<Session, Class<? extends Listener>>, LinkedHashSet<BaseComponent>> SESSION_LISTENER_TO_COMPONENTS = new HashMap<>();

    // BUKKIT LISTENER METHODS
    public static void controllerStart(BaseComponent component) {
        var listeners = component.listeners();
        for (var listener : listeners.set()) {
            trackingStart(component, listener);
            listenerStart(component, listener);
        }
    }

    public static void controllerStop(BaseComponent component) {
        var listeners = component.listeners();
        for (var listener : listeners.set()) {
            listenerStop(component, listener);
            trackingStop(component, listener);
        }
    }

    private static void listenerStart(BaseComponent component, Class<? extends Listener> listener) {
        var ids = LISTENER_TO_COMPONENT_IDS.computeIfAbsent(listener, k -> new HashSet<>());
        ids.add(component.id());

        ENABLE_LISTENERS.computeIfAbsent(listener, k -> {
            try {
                var plugin = SessionAPI.instance();
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

    private static void listenerStop(BaseComponent component, Class<? extends Listener> listenerClass) {
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

    private static void trackingStart(BaseComponent component, Class<? extends Listener> listener) {
        var pair = sessionListenerPair(component.session(), listener);
        var components = SESSION_LISTENER_TO_COMPONENTS.computeIfAbsent(pair, k -> {
           return new LinkedHashSet<>();
        });
        components.add(component);
    }

    private static void trackingStop(BaseComponent component, Class<? extends Listener> listener) {
        var session = component.session();
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

    private static Pair<Session, Class<? extends Listener>> sessionListenerPair(Session session, Class<? extends Listener> listenerClass) {
        return Pair.of(session, listenerClass);
    }

    private static @Nullable LinkedHashSet<BaseComponent> findComponents(@Nullable Session session, Class<? extends Listener> listenerClass) {
        if (session == null) return null;
        var pair = sessionListenerPair(session, listenerClass);
        var components = SESSION_LISTENER_TO_COMPONENTS.get(pair);
        if (components == null) {
            throw new IllegalStateException("No components found for session '" + session.name() + "' and listenerClass class '" + listenerClass.getName() + "'. Ensure the listenerClass is registered correctly.");
        }
        return components;
    }

    public static <T extends BaseComponent> void each(@Nullable Session session, Class<? extends Listener> listenerClass, Class<T> componentClass, Consumer<T> action) {
        var components = findComponents(session, listenerClass);
        if (components == null) return;
        components.stream()
                .filter(componentClass::isInstance)
                .map(componentClass::cast)
                .forEach(action);
    }

    public static void each(@Nullable Session session, Class<? extends Listener> listenerClass, Consumer<BaseComponent> action) {
        var components = findComponents(session, listenerClass);
        if (components == null) return;
        components.forEach(action);
    }

    public static @Nullable BaseComponent findComponent(@Nullable Session session, Class<? extends Listener> listenerClass) {
        if (session == null) return null;
        var pair = sessionListenerPair(session, listenerClass);
        var components = SESSION_LISTENER_TO_COMPONENTS.get(pair);
        if (components == null) {
            throw new IllegalStateException("No components found for session '" + session.name() + "' and listenerClass class '" + listenerClass.getName() + "'. Ensure the listenerClass is registered.");
        }
        if (components.isEmpty()) {
            throw new IllegalStateException("Expected at least one component, but none were found for session '" + session.name() + "' and listenerClass class '" + listenerClass.getName() + "'.");
        }
        return components.getFirst();
    }

    public static void first(@Nullable Session session, Class<? extends Listener> listenerClass, Consumer<BaseComponent> action) {
        var component = findComponent(session, listenerClass);
        if (component == null) return;
        action.accept(component);
    }

    public static <T> void first(@Nullable Session session, Class<? extends Listener> listenerClass, Class<T> componentClass, Consumer<T> action) {
        var components = findComponents(session, listenerClass);
        if (components == null) return;

        var typedComponent = components.stream()
                .filter(componentClass::isInstance)
                .map(componentClass::cast)
                .findFirst()
                .orElse(null);

        if (typedComponent == null) return;
        action.accept(typedComponent);
    }

    public static <T extends BaseComponent> List<T> components(@Nullable Session session, Class<? extends Listener> listenerClass, Class<T> componentClass) {
        var components = findComponents(session, listenerClass);
        if (components == null) return new ArrayList<>();
        return components.stream()
                .filter(componentClass::isInstance)
                .map(componentClass::cast)
                .toList();
    }

    public static <T extends BaseComponent> @Nullable T first(@Nullable Session session, Class<? extends Listener> listenerClass, Class<T> componentClass) {
        var components = findComponents(session, listenerClass);
        if (components == null) return null;

        return components.stream()
                .filter(componentClass::isInstance)
                .map(componentClass::cast)
                .findFirst()
                .orElse(null);

    }

}

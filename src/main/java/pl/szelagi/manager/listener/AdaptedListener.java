package pl.szelagi.manager.listener;

import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.component.base.Component;
import pl.szelagi.component.container.Container;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static pl.szelagi.manager.listener.ListenerManager.findComponent;
import static pl.szelagi.manager.listener.ListenerManager.findComponents;

public interface AdaptedListener extends Listener {
    static void firstFor(@Nullable Container container, Class<? extends Listener> listenerClass, Consumer<Component> action) {
        var component = findComponent(container, listenerClass);
        if (component == null) return;
        action.accept(component);
    }

    static <T extends Component> void firstFor(@Nullable Container container, Class<? extends Listener> listenerClass, Class<T> componentClass, Consumer<T> action) {
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

    static <T extends Component> @Nullable T firstFor(@Nullable Container container, Class<? extends Listener> listenerClass, Class<T> componentClass) {
        var components = findComponents(container, listenerClass);
        if (components == null) return null;

        return components.stream()
                .filter(componentClass::isInstance)
                .map(componentClass::cast)
                .findFirst()
                .orElse(null);
    }

    static void eachFor(@Nullable Container container, Class<? extends Listener> listenerClass, Consumer<Component> action) {
        var components = findComponents(container, listenerClass);
        if (components == null) return;
        components.forEach(action);
    }

    static <T extends Component> void eachFor(@Nullable Container container, Class<? extends Listener> listenerClass, Class<T> componentClass, Consumer<T> action) {
        var components = findComponents(container, listenerClass);
        if (components == null) return;
        components.stream()
                .filter(componentClass::isInstance)
                .map(componentClass::cast)
                .forEach(action);
    }

    static <T extends Component> List<T> eachFor(@Nullable Container container, Class<? extends Listener> listenerClass, Class<T> componentClass) {
        var components = findComponents(container, listenerClass);
        if (components == null) return new ArrayList<>();
        return components.stream()
                .filter(componentClass::isInstance)
                .map(componentClass::cast)
                .toList();
    }

    static @Nullable Component firstFor(@Nullable Container container, Class<? extends Listener> listenerClass) {
        return findComponents(container, listenerClass).stream().findFirst().orElse(null);
    }

    static List<Component> eachFor(@Nullable Container container, Class<? extends Listener> listenerClass) {
        return findComponents(container, listenerClass).stream().toList();
    }

    default void first(@Nullable Container container,
                       @NotNull Consumer<Component> componentConsumer) {
        firstFor(container, getClass(), componentConsumer);
    }

    default <T extends Component> void first(@Nullable Container container,
                                             @NotNull Class<T> componentClass,
                                             @NotNull Consumer<T> componentConsumer) {
        firstFor(container, getClass(), componentClass, componentConsumer);
    }

    default <T extends Component> @Nullable T first(@Nullable Container container,
                                             @NotNull Class<T> componentClass) {
        return firstFor(container, getClass(), componentClass);
    }

    default <T extends Component> void each(@Nullable Container container,
                                            @NotNull Class<T> componentClass,
                                            @NotNull Consumer<T> componentConsumer) {
        eachFor(container, getClass(), componentClass, componentConsumer);
    }

    default void each(@Nullable Container container,
                      @NotNull Consumer<Component> componentConsumer) {
        eachFor(container, getClass(), componentConsumer);
    }

    default <T extends Component> List<T> each(@Nullable Container container,
                                          @NotNull Class<T> componentClass) {
        return eachFor(container, getClass(), componentClass);
    }

    default @Nullable Component first(@Nullable Container container) {
        return firstFor(container, getClass());
    }

    default List<Component> each(@Nullable Container container) {
        return eachFor(container, getClass());
    }
}

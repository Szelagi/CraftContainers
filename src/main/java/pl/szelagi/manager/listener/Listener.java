package pl.szelagi.manager.listener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.component.base.Component;
import pl.szelagi.component.container.Container;

import java.util.function.Consumer;

public interface Listener extends org.bukkit.event.Listener {
    default <T extends Component> void each(@Nullable Container container,
                                            @NotNull Class<T> componentClass,
                                            @NotNull Consumer<T> componentConsumer) {
        ListenerManager.each(container, getClass(), componentClass, componentConsumer);
    }
    default <T extends Component> void first(@Nullable Container container,
                                             @NotNull Class<T> componentClass,
                                             @NotNull Consumer<T> componentConsumer) {
        ListenerManager.first(container, getClass(), componentClass, componentConsumer);
    }

    default void each(@Nullable Container container,
                      @NotNull Consumer<Component> componentConsumer) {
        ListenerManager.each(container, getClass(), componentConsumer);
    }
    default void first(@Nullable Container container,
                       @NotNull Consumer<Component> componentConsumer) {
        ListenerManager.first(container, getClass(), componentConsumer);
    }


}

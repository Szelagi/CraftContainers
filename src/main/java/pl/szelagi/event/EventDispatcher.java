/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.event;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Dispatches events to registered listeners.
 * <p>
 * Supports both event-specific listeners ({@link Consumer<T>}) and general
 * no-argument listeners ({@link Runnable}).
 *
 * @param <T> the type of event data passed to listeners
 */
public class EventDispatcher<T> {
    private final @NotNull Set<Consumer<T>> listeners = new HashSet<>();
    private final @NotNull Set<Runnable> runnableListeners = new HashSet<>();

    /**
     * Registers a listener that consumes the event.
     *
     * @param listener the listener to register
     */
    public void register(@NotNull Consumer<T> listener) {
        listeners.add(listener);
    }

    /**
     * Registers a listener that runs without event context.
     *
     * @param listener the listener to register
     */
    public void register(@NotNull Runnable listener) {
        runnableListeners.add(listener);
    }

    /**
     * Dispatches the event to all registered listeners.
     *
     * @param event the event object to pass to listeners
     */
    public void dispatch(T event) {
        for (var listener : listeners) {
            listener.accept(event);
        }
        for (var listener : runnableListeners) {
            listener.run();
        }
    }
}
/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.event.handler;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class HandlerEvent<T> {
    private final @NotNull Set<Function<T, ?>> listeners = new HashSet<>();
    private final @NotNull Set<Runnable> runnableListeners = new HashSet<>();

    public void bind(@NotNull Function<T, ?> listener) {
        listeners.add(listener);
    }

    public void bind(@NotNull Runnable listener) {
        runnableListeners.add(listener);
    }

    public void call(T event) {
        for (var listener : listeners) {
            listener.apply(event);
        }
        for (var listener : runnableListeners) {
            listener.run();
        }
    }
}
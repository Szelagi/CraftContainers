/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.state;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.state.manual.ManualContainerException;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class Container<I, S> implements Serializable, Iterable<S> {
    private final ManualContainer<I, S> manualContainer;
    private final @NotNull Function<I, S> creator;

    public Container(@NotNull Function<I, S> creator) {
        this.manualContainer = new ManualContainer<>();
        this.creator = creator;
    }

    // CREATE

    public @NotNull S createOrThrow(@NotNull I input, @NotNull Function<I, S> creator) throws ManualContainerException {
        return manualContainer.createOrThrow(input, creator);
    }

    public @NotNull S createOrThrow(@NotNull I input) throws ManualContainerException {
        return manualContainer.createOrThrow(input, creator);
    }

    // REFRESH

    public @NotNull S refreshOrCreate(@NotNull I input) {
        removeIfExists(input);
        return createOrThrow(input);
    }

    public @NotNull S refreshOrThrow(@NotNull I input, @NotNull Function<I, S> creator) {
        removeIfExists(input);
        return createOrThrow(input, creator);
    }

    // GET

    public @NotNull S getOrCreate(I input) {
        if (manualContainer.contains(input))
            return manualContainer.getOrThrow(input);
        return manualContainer.createOrThrow(input, creator);
    }

    public @NotNull S getOrThrow(I input) throws ManualContainerException {
        return manualContainer.getOrThrow(input);
    }

    public @Nullable S getOrNull(@NotNull I input) {
        if (manualContainer.contains(input))
            return manualContainer.getOrThrow(input);
        return null;
    }

    // REMOVE

    public @NotNull S removeOrThrow(@NotNull I input) throws ManualContainerException {
        return manualContainer.removeOrThrow(input);
    }

    public @Nullable S removeIfExists(@NotNull I input) {
        if (manualContainer.contains(input))
            return manualContainer.removeOrThrow(input);
        return null;
    }

    // EXISTS

    public boolean contains(@NotNull I input) {
        return manualContainer.contains(input);
    }

    // OPERATIONS

    public @Nullable S find(Predicate<S> predicate) {
        return manualContainer.find(predicate);
    }

    public <R> @NotNull List<R> map(Function<? super S, ? extends R> mapper) {
        return manualContainer.map(mapper);
    }

    // GETTERS

    public @NotNull Function<I, S> creator() {
        return creator;
    }

    @Override
    public @NotNull Iterator<S> iterator() {
        return manualContainer.iterator();
    }
}

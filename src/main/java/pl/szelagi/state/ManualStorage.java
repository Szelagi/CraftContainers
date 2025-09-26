/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.state;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ManualStorage<I, S> implements Serializable, Iterable<S> {
    private final HashMap<I, S> inputStorageMap = new HashMap<>();

    public @NotNull S createOrThrow(@NotNull I input, @NotNull Function<I, S> creator) throws ManualStorageException {
        if (inputStorageMap.containsKey(input))
            throw new ManualStorageException("manual container of " + input + " multi initialization");
        inputStorageMap.put(input, creator.apply(input));
        return getOrThrow(input);
    }

    public @NotNull S getOrThrow(@NotNull I input) throws ManualStorageException {
        var record = inputStorageMap.get(input);
        if (record == null)
            throw new ManualStorageException("manual container of " + input + " is not initialized");
        return record;
    }

    public @NotNull S removeOrThrow(@NotNull I input) throws ManualStorageException {
        var record = inputStorageMap.remove(input);
        if (record == null)
            throw new ManualStorageException("remove " + input + " not exists in manual container");
        return record;
    }

    public @Nullable S find(Predicate<S> predicate) {
        return inputStorageMap.values().stream()
                .filter(predicate)
                .findFirst()
                .orElse(null);
    }

    public <R> @NotNull List<R> map(Function<? super S, ? extends R> mapper) {
        return inputStorageMap.values().stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    public boolean contains(@NotNull I input) {
        return inputStorageMap.containsKey(input);
    }

    public Collection<S> toCollection() {
        return inputStorageMap.values();
    }

    @NotNull
    @Override
    public Iterator<S> iterator() {
        return toCollection().iterator();
    }

    public Stream<S> stream() {
        return toCollection().stream();
    }

    public int size() {
        return inputStorageMap.size();
    }

    public boolean isEmpty() {
        return inputStorageMap.isEmpty();
    }
}

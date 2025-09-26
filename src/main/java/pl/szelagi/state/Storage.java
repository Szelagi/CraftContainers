/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.state;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class Storage<I, S> extends ManualStorage<I, S> {
    private final @NotNull Function<I, S> creator;

    public Storage(@NotNull Function<I, S> creator) {
        this.creator = creator;
    }

    public @NotNull S createOrThrow(@NotNull I input) throws ManualStorageException {
        return createOrThrow(input, creator);
    }

    public @NotNull S refreshOrCreate(@NotNull I input) {
        removeIfExists(input);
        return createOrThrow(input);
    }

    public @NotNull S refreshOrCreate(@NotNull I input, Function<I, S> creator) {
        removeIfExists(input);
        return createOrThrow(input, creator);
    }

    public @NotNull S refreshOrThrow(@NotNull I input) {
        removeOrThrow(input);
        return createOrThrow(input);
    }

    public @NotNull S refreshOrThrow(@NotNull I input, @NotNull Function<I, S> creator) {
        removeOrThrow(input);
        return createOrThrow(input, creator);
    }

    public @NotNull S getOrCreate(I input) {
        if (contains(input))
            return getOrThrow(input);
        return createOrThrow(input);
    }

    public @NotNull S getOrCreate(I input, @NotNull Function<I, S> creator) {
        if (contains(input))
            return getOrThrow(input);
        return createOrThrow(input, creator);
    }

    public @Nullable S getOrNull(@NotNull I input) {
        if (contains(input))
            return getOrThrow(input);
        return null;
    }

    public @Nullable S removeIfExists(@NotNull I input) {
        if (contains(input))
            return removeOrThrow(input);
        return null;
    }

    public @NotNull Function<I, S> creator() {
        return creator;
    }
}

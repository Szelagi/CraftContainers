/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.tag;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

@Deprecated
public class TagQuery extends ArrayList<Tag> implements Serializable {
    @NotNull
    private final String tag;

    public TagQuery(@NotNull String tag, @NotNull List<Tag> elements) {
        super(elements);
        this.tag = tag;
    }

    @Deprecated
    public @NotNull String getQueryName() {
        return tag;
    }

    public @NotNull String name() {
        return tag;
    }

    @Deprecated
    public @NotNull Tag getFirst() {
        return get(0);
    }

    public @NotNull Tag first() throws NoSuchElementException {
        if (size() == 0) throw new NoSuchElementException();
        return get(0);
    }

    @Deprecated
    public @NotNull Location getFirstLocation() {
        return get(0).getLocation();
    }

    public @NotNull Location firstLocation() throws NoSuchElementException {
        if (size() == 0) throw new NoSuchElementException();
        return get(0).location();
    }

    public @NotNull Location firstCentredXZ() throws NoSuchElementException {
        if (size() == 0) throw new NoSuchElementException();
        return get(0).centeredXZ();
    }

    public @NotNull Location firstCentredXYZ() throws NoSuchElementException {
        if (size() == 0) throw new NoSuchElementException();
        return get(0).centeredXYZ();
    }

    @Deprecated
    public @NotNull List<Location> toLocations() {
        return stream().map(Tag::location)
                .toList();
    }

    public @NotNull List<Location> locations() {
        return stream().map(Tag::location)
                .toList();
    }

    public @NotNull <R> List<R> map(Function<Tag, R> mapper) {
        return stream().map(mapper).toList();
    }

    public @NotNull List<Tag> filter(Predicate<Tag> predicate) {
        return stream().filter(predicate)
                .toList();
    }

    public @Nullable Tag find(Predicate<Tag> predicate) {
        return stream().filter(predicate)
                .findFirst().orElse(null);
    }
}

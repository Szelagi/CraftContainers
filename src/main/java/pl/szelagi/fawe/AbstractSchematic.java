/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.fawe;

import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.allocator.IAllocate;
import pl.szelagi.transform.RotAxis;
import pl.szelagi.transform.Degree;
import pl.szelagi.transform.Rotation;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractSchematic<T extends AbstractSchematic<T>> implements ISchematic<T> {
    private final List<TransformOperation<T>> transformations;
    private final File file;
    public final IAllocate space;
    public final Location origin;

    public @Nullable Location max;
    public @Nullable Location min;

    public AbstractSchematic(@NotNull File file, @NotNull IAllocate space, @NotNull Location origin) {
        this.file = file;
        this.space = space;
        this.origin = origin;
        this.transformations = new LinkedList<>();
    }

    protected AbstractSchematic(@NotNull File file, @NotNull IAllocate space, @NotNull Location origin, Collection<TransformOperation<T>> transformOperation) {
        this.file = file;
        this.space = space;
        this.origin = origin;
        this.transformations = new LinkedList<>(transformOperation);
    }

    public abstract void load();

    public abstract void clean();

    protected abstract T cloneWithOperation(TransformOperation<T> operation);

    protected ClipboardAndHolder getClipboardAndHolder() {
        var clipboard = FaweOperations.read(file);
        var holder = new ClipboardHolder(clipboard);
        for (var operation : transformations) {
            operation.transform(clipboard, holder);
        }
        return new ClipboardAndHolder(holder);
    }

    // Ładuje MIN i MAX punkt. Jeżeli punkt był załadowany zwraca TRUE i nie aktywuje consumera.
    // Kiedy funkcja musiała załadować clipboard zwraca FALSE i uruchamia consumer z załadowanym schematem.
    protected boolean ensureMinMaxExists(@Nullable Consumer<ClipboardAndHolder> consumer) {
        if (min == null || max == null) {
            try (var cah = getClipboardAndHolder()) {
                min = FaweOperations.getAbsoluteMin(cah.holder(), origin);
                max = FaweOperations.getAbsoluteMax(cah.holder(), origin);
                checkValid();
                if (consumer != null)
                    consumer.accept(cah);
                return false;
            }
        }
        return true;
    }

    protected void checkValid() {
        if (!space.isLocationIn(min) || !space.isLocationIn(max)) {
            throw new IllegalStateException("Size of space does not match schematic size");
        }
    }

    public File getFile() {
        return file;
    }

    public IAllocate getSpace() {
        return space;
    }

    @Override
    public Location getOrigin() {
        return origin;
    }

    @Override
    public Location getBase() {
        return getOrigin();
    }

    public List<TransformOperation<T>> getTransformations() {
        return new ArrayList<>(transformations);
    }

    @Override
    public T rotate(Degree angle, Rotation direction, RotAxis axis) {
        var operation = new TransformOperation.Rotate<T>(angle, direction, axis);
        return cloneWithOperation(operation);
    }

    @Override
    public @NotNull Location getMin() {
        ensureMinMaxExists(null);
        assert min != null;
        return min;
    }

    @Override
    public @NotNull Location getMax() {
        ensureMinMaxExists(null);
        assert max != null;
        return max;
    }

    @Override
    public abstract T clone();
}

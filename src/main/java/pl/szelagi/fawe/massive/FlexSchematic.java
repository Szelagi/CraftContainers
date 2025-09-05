/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.fawe.massive;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.allocator.IAllocate;
import pl.szelagi.fawe.*;

import java.io.File;
import java.util.Collection;

public class FlexSchematic extends AbstractSchematic<FlexSchematic> {
    private final FlexLoader loader;
    private final FlexCleaner cleaner;

    public FlexSchematic(@NotNull File file,
                         @NotNull IAllocate space,
                         @NotNull Location origin,
                         FlexLoader loader,
                         FlexCleaner cleaner) {
        super(file, space, origin);
        this.loader = loader;
        this.cleaner = cleaner;
    }

    protected FlexSchematic(@NotNull File file,
                            @NotNull IAllocate space,
                            @NotNull Location origin,
                            Collection<TransformOperation<FlexSchematic>> transformOperation,
                            FlexLoader loader,
                            FlexCleaner cleaner) {
        super(file, space, origin, transformOperation);
        this.loader = loader;
        this.cleaner = cleaner;
    }

    @Override
    public void load() {
        if (ensureMinMaxExists(this::loader)) {
            try (var cah = getClipboardAndHolder()) {
                loader(cah);
            }
        }
    }

    protected void loader(ClipboardAndHolder cah) {
        loader.load(this, cah);
    }

    @Override
    public void clean() {
        ensureMinMaxExists(null);
        assert min != null;
        assert max != null;
        cleaner.clean(this, min, max);
    }

    @Override
    public FlexSchematic translateAbsolute(int dx, int dy, int dz) {
        var operation = new TranslateAbsoluteFlexSchematic(dx, dy, dz);
        return cloneWithOperation(operation);
    }

    @Override
    public FlexSchematic clone() {
        return new FlexSchematic(getFile(), getSpace(), getOrigin(), getTransformations(), loader, cleaner);
    }

    @Override
    protected FlexSchematic cloneWithOperation(TransformOperation<FlexSchematic> operation) {
        var operations = getTransformations();
        operations.add(operation);

        var schematic = new FlexSchematic(getFile(), getSpace(), getOrigin(), operations, loader, cleaner);
        return operation.build(schematic);
    }

    public FlexLoader loader() {
        return loader;
    }

    public FlexCleaner cleaner() {
        return cleaner;
    }
}

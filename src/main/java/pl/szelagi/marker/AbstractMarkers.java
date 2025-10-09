/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.marker;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.util.IncrementalGenerator;

import java.io.*;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractMarkers<T extends IMarkers<T>> implements IMarkers<T> {
    private final IncrementalGenerator idGenerator;
    private final Location base;

    public AbstractMarkers(IncrementalGenerator idGenerator, Location base) {
        this.idGenerator = idGenerator;
        this.base = base;
    }

    protected abstract @NotNull Set<MarkerData> toMarkerDataSet(Location base);

    protected abstract @NotNull MarkersData toMarkersData(Location base);

    protected @NotNull IncrementalGenerator getIdGenerator() {
        return idGenerator;
    }

    @Override
    public @NotNull Location getBase() {
        return base;
    }

}

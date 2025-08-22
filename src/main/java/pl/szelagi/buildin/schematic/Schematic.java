/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.schematic;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.file.FAWESchematicLoader;
import pl.szelagi.space.IAllocate;

import java.io.File;
import java.util.List;

public class Schematic {
    private final File file;
    public final IAllocate space;
    public final Location origin;

    public Location max;
    public Location min;

    public Schematic(@NotNull File file, @NotNull IAllocate space, @NotNull Location origin) {
        this.file = file;
        this.space = space;
        this.origin = origin;
    }

    public void load() {
        try (var clipboard = FAWESchematicLoader.read(file)) {
            if (min == null || max == null) {
                min = FAWESchematicLoader.getAbsoluteMin(clipboard, origin);
                max = FAWESchematicLoader.getAbsoluteMax(clipboard, origin);
                checkValid();
            }
            FAWESchematicLoader.paste(clipboard, origin);
        }
    }

    private void loadMinMaxIfNotExists() {
        if (min == null || max == null) {
            try (var clipboard = FAWESchematicLoader.read(file)) {
                min = FAWESchematicLoader.getAbsoluteMin(clipboard, origin);
                max = FAWESchematicLoader.getAbsoluteMax(clipboard, origin);
                checkValid();
            }
        }
    }

    public void unload() {
        loadMinMaxIfNotExists();
        FAWESchematicLoader.clearRegion(min, max);
    }

    private void checkValid() {
        if (!space.isLocationIn(min) || !space.isLocationIn(max)) {
            throw new IllegalStateException("Size of space does not match schematic size");
        }
    }

    public int size() {
        loadMinMaxIfNotExists();
        var minDX = min.getBlockX() - origin.getBlockX();
        var minDZ = min.getBlockZ() - origin.getBlockZ();
        var maxDX = max.getBlockX() - origin.getBlockX();
        var maxDZ = max.getBlockZ() - origin.getBlockZ();
        var dst = List.of(minDX, minDZ, maxDX, maxDZ);
        return dst.stream().map(integer -> (Integer) (Math.abs(integer) + 1)).max(Integer::compareTo).orElseThrow();
    }

}
